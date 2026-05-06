/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.framework.support.audit.aspect;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.wldos.common.Constants;
import io.github.wldos.common.utils.DateUtils;
import io.github.wldos.framework.common.CommonOperation;
import io.github.wldos.framework.support.audit.IOpLogger;
import io.github.wldos.framework.support.audit.OpEvent;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import com.wldos.framework.mvc.controller.EntityController;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志 AOP 切面：拦截所有标注 {@link OpLog} 的方法，构造 {@link OpEvent} 异步入库。
 *
 * <p>核心规则：
 * <ul>
 *   <li><b>module/action 推导链</b>：{@code @OpLog.module/action} → 类上 {@code @Api.tags} / 方法上 {@code @ApiOperation.value} → 方法签名兜底；</li>
 *   <li><b>resourceId 解析</b>：SpEL 表达式（{@code "#id"} / {@code "#dto.orderId"}），失败回填 null 不影响业务；</li>
 *   <li><b>入参摘要</b>：剔除 {@code HttpServletRequest/Response/MultipartFile} 等不可序列化对象，
 *       顶层字段命中 {@code sensitiveFields} 替换为 {@code "***"}，最终 JSON 截断到 {@value #PARAMS_MAX_LEN}；</li>
 *   <li><b>用户上下文</b>：从 {@code RequestContextHolder} 取 request，header 取 {@code curUserId}/{@code curUserName}/{@code tenantId}/{@code domainId}；
 *       与项目现有 {@code MyJdbcRepository#getUserId()} 同源；</li>
 *   <li><b>结果与错误</b>：业务异常 → resultCode=500 + errorMessage 摘要；正常返回 → 默认 200。</li>
 * </ul>
 *
 * <p>容错原则：切面所有异常吞掉 + logback warn，<b>绝不</b>影响业务方法的正常返回。
 *
 * <p>{@code @Order(LOWEST_PRECEDENCE)}：让其它业务切面（事务、权限等）先执行，本切面在最外层观测最终结果。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class OpLogAspect {

	/** 入参摘要 JSON 截断长度（避免单条日志过大）。 */
	private static final int PARAMS_MAX_LEN = 4096;

	/** 错误信息截断长度。 */
	private static final int ERROR_MAX_LEN = 512;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();

	private static final ParameterNameDiscoverer PARAM_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

	@Autowired(required = false)
	private IOpLogger opLogger;

	@Autowired(required = false)
	@Lazy
	private CommonOperation commonOperation;

	@Around("@annotation(opLog)")
	public Object around(ProceedingJoinPoint pjp, OpLog opLog) throws Throwable {
		long start = System.currentTimeMillis();
		Throwable thrown = null;
		Object result = null;
		try {
			result = pjp.proceed();
			return result;
		}
		catch (Throwable t) {
			thrown = t;
			throw t;
		}
		finally {
			try {
				if (opLogger != null) {
					OpEvent ev = build(pjp, opLog, result, thrown, System.currentTimeMillis() - start);
					if (ev != null) {
						opLogger.recordAsync(ev);
					}
				}
			}
			catch (Throwable t) {
				log.warn("[op.log.aspect] build/record failed (business unaffected): {}", t.getMessage());
			}
		}
	}

	/* ============================================================
	 *  事件构造
	 * ============================================================ */

	private OpEvent build(ProceedingJoinPoint pjp, OpLog opLog, Object result, Throwable thrown, long durationMs) {
		MethodSignature sig = (MethodSignature) pjp.getSignature();
		Method method = sig.getMethod();
		Object[] args = pjp.getArgs();

		// targetClass：运行时实际类（CGLIB 代理时穿透到原始类）。
		// 注意：method.getDeclaringClass() 对继承自 EntityController 等基类的方法只能拿到基类，
		// 找不到子类（如 DomainController）的 @Api(tags=...)，会让 module 退化成 "EntityController"。
		Class<?> targetClass = pjp.getTarget() != null
				? ClassUtils.getUserClass(pjp.getTarget().getClass())
				: method.getDeclaringClass();

		OpEvent ev = new OpEvent();
		ev.setOccurAt(nowByDateUtils());
		ev.setDurationMs(durationMs);

		// module / action 推导
		ev.setModule(resolveModule(opLog, targetClass));
		ev.setAction(resolveAction(opLog, method));

		// 资源标识：
		//   - resourceType 注解显式 → 否则尝试从 EntityController<S,E> 的泛型 E 推导（如 WoDomain → domain），
		//     这样基类方法 addEntity/updateEntity/removeEntity 在子类继承时才有合理 resourceType；
		//   - resourceId 走 SpEL（注解里给）。
		String resourceType = notBlank(opLog.resourceType())
				? opLog.resourceType()
				: resolveResourceTypeFromGeneric(targetClass);
		if (notBlank(resourceType)) {
			ev.setResourceType(resourceType);
		}
		if (notBlank(opLog.resourceId())) {
			ev.setResourceId(parseSpel(opLog.resourceId(), method, args));
		}
		// 资源人性化名称：注解里显式给 SpEL 才在切面侧填；否则留给异步消费侧反查
		if (notBlank(opLog.resourceName())) {
			ev.setResourceName(parseSpel(opLog.resourceName(), method, args));
		}

		// 入参摘要
		if (opLog.recordParams()) {
			ev.setParamsSummary(buildParamsSummary(method, args, opLog.sensitiveFields()));
		}

		// 上下文（HTTP / 用户）
		HttpServletRequest request = currentRequest();
		if (commonOperation != null) {
			// 优先走 WLDOS 统一上下文能力（由 edge 统一注入请求头并解析）
			try {
				ev.setUserId(commonOperation.getUserId());
				ev.setDomainId(commonOperation.getDomainId());
				ev.setComId(commonOperation.getTenantId());
				ev.setIp(commonOperation.getUserIp());
			} catch (Exception ignored) {
				// 回退到 header 解析，不影响业务
			}
		}
		if (request != null) {
			ev.setRequestPath(request.getRequestURI());
			ev.setHttpMethod(request.getMethod());
			if (ev.getIp() == null) {
				ev.setIp(headerOrNull(request, "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"));
				if (ev.getIp() == null) {
					ev.setIp(request.getRemoteAddr());
				}
			}
			ev.setUserAgent(request.getHeader("User-Agent"));
			ev.setUserName(request.getHeader(Constants.CONTEXT_KEY_USER_NAME));
			if (ev.getUserId() == null) {
				ev.setUserId(parseLongHeader(request, Constants.CONTEXT_KEY_USER_ID));
			}
			if (ev.getDomainId() == null) {
				ev.setDomainId(parseLongHeader(request, Constants.CONTEXT_KEY_USER_DOMAIN));
			}
			if (ev.getComId() == null) {
				ev.setComId(parseLongHeader(request, Constants.CONTEXT_KEY_USER_TENANT));
			}
		} else {
			// 仅在无请求上下文（非 HTTP 场景）兜底为游客默认值。
			// 正常 HTTP 请求由 EdgeGateWayFilter 注入标准上下文头（user/domain/tenant），
			// 这里不再覆盖/猜测，避免把网关注入异常默默吞掉。
			if (ev.getUserId() == null) ev.setUserId(Constants.GUEST_ID);
			if (ev.getDomainId() == null) ev.setDomainId(Constants.DEFAULT_DOMAIN_ID);
			if (ev.getComId() == null) ev.setComId(Constants.TOP_COM_ID);
		}
		// 虚拟身份：当前实现以 (userId, domainId) 二元组作为虚拟身份，未独立分配时与 userId 同步
		ev.setVirtualUserId(ev.getUserId());

		// 结果与错误
		if (thrown != null) {
			ev.setResultCode("500");
			ev.setErrorMessage(truncate(thrown.getClass().getSimpleName() + ": " + safeMessage(thrown), ERROR_MAX_LEN));
		}
		else {
			ev.setResultCode(extractResultCode(result));
		}
		return ev;
	}

	/* ============================================================
	 *  module / action 推导
	 * ============================================================ */

	/**
	 * module 推导链：{@code @OpLog.module} → 运行时实际类（target class）上 {@code @Api.tags[0]}
	 * → target class SimpleName 兜底。
	 *
	 * <p>用 target class 而非 {@code method.getDeclaringClass()}，避免基类（如 {@link EntityController}）
	 * 方法继承到子类时拿不到子类的 {@code @Api} 注解、module 退化成 "EntityController"。
	 */
	private static String resolveModule(OpLog opLog, Class<?> targetClass) {
		if (notBlank(opLog.module())) {
			return opLog.module();
		}
		Api api = AnnotationUtils.findAnnotation(targetClass, Api.class);
		if (api != null && api.tags() != null && api.tags().length > 0 && notBlank(api.tags()[0])) {
			return api.tags()[0];
		}
		return targetClass.getSimpleName();
	}

	/**
	 * 当 {@code @OpLog.resourceType} 留空时，从 {@link EntityController}{@code <S, E>} 的泛型实参 {@code E} 推导。
	 *
	 * <p>命名约定（与 02-OpLog 文档及各模块 {@code IOpLogContextResolver} 单数命名对齐）：
	 * <ol>
	 *   <li>去 wldos 实体前缀：{@code Wo} / 单字母 {@code K}（仅在剩余首字母大写时）；</li>
	 *   <li>简单单数归一：末尾 {@code s}（非 {@code ss}）去掉
	 *       —— {@code KPubs}→{@code Pub}、{@code KComments}→{@code Comment}、{@code KTerms}→{@code Term}；</li>
	 *   <li>驼峰转 snake_case + 全小写
	 *       —— {@code WoDomain}→{@code domain}、{@code WoCalendarHoliday}→{@code calendar_holiday}。</li>
	 * </ol>
	 *
	 * <p>对于命名特殊（如 {@code WoCompany}→{@code company}，但 resolver 里登记 {@code com}）的实体，
	 * 子类应在覆写方法上贴 {@code @OpLog(resourceType="...")} 显式指定，覆盖此处推导。
	 *
	 * <p>非 {@link EntityController} 子类（如 {@code NonEntityController} 的业务方法）返回 {@code null}，
	 * 由调用方按"显式注解必填"语义处理。
	 */
	private static String resolveResourceTypeFromGeneric(Class<?> targetClass) {
		try {
			if (!EntityController.class.isAssignableFrom(targetClass)) {
				return null;
			}
			Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(targetClass, EntityController.class);
			if (generics == null || generics.length < 2 || generics[1] == null) {
				return null;
			}
			String name = generics[1].getSimpleName();
			// 去 wldos 实体命名前缀
			if (name.length() > 2 && name.charAt(0) == 'W' && name.charAt(1) == 'o' && Character.isUpperCase(name.charAt(2))) {
				name = name.substring(2);
			}
			else if (name.length() > 1 && name.charAt(0) == 'K' && Character.isUpperCase(name.charAt(1))) {
				name = name.substring(1);
			}
			// 简单单数归一（末尾 s 且不是 ss）
			int len = name.length();
			if (len > 1 && name.charAt(len - 1) == 's' && name.charAt(len - 2) != 's') {
				name = name.substring(0, len - 1);
			}
			return camelToSnake(name);
		}
		catch (Throwable ignored) {
			return null;
		}
	}

	/** 驼峰转 snake_case：{@code CalendarHoliday}→{@code calendar_holiday}，{@code Domain}→{@code domain}。 */
	private static String camelToSnake(String s) {
		if (s == null || s.isEmpty()) return s;
		StringBuilder sb = new StringBuilder(s.length() + 4);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0) sb.append('_');
				sb.append(Character.toLowerCase(c));
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private static String resolveAction(OpLog opLog, Method method) {
		if (notBlank(opLog.action())) {
			return opLog.action();
		}
		ApiOperation op = AnnotationUtils.findAnnotation(method, ApiOperation.class);
		if (op != null && notBlank(op.value())) {
			return op.value();
		}
		return method.getName();
	}

	/* ============================================================
	 *  SpEL
	 * ============================================================ */

	private static String parseSpel(String expression, Method method, Object[] args) {
		try {
			String[] paramNames = PARAM_NAME_DISCOVERER.getParameterNames(method);
			if (paramNames == null || paramNames.length == 0) {
				return null;
			}
			EvaluationContext ctx = new StandardEvaluationContext();
			for (int i = 0; i < paramNames.length && i < args.length; i++) {
				ctx.setVariable(paramNames[i], args[i]);
			}
			Expression expr = SPEL_PARSER.parseExpression(expression);
			Object value = expr.getValue(ctx);
			return value == null ? null : value.toString();
		}
		catch (Exception e) {
			return null;
		}
	}

	/* ============================================================
	 *  入参摘要
	 * ============================================================ */

	private static String buildParamsSummary(Method method, Object[] args, String[] sensitiveFields) {
		try {
			String[] paramNames = PARAM_NAME_DISCOVERER.getParameterNames(method);
			Map<String, Object> data = new LinkedHashMap<>();
			Set<String> sensitive = sensitiveFields == null ? new HashSet<>() : new HashSet<>(java.util.Arrays.asList(sensitiveFields));
			for (int i = 0; i < args.length; i++) {
				Object a = args[i];
				if (!serializable(a)) {
					continue;
				}
				String name = paramNames != null && i < paramNames.length ? paramNames[i] : ("arg" + i);
				if (sensitive.contains(name)) {
					data.put(name, "***");
				}
				else {
					data.put(name, a);
				}
			}
			String json = OBJECT_MAPPER.writeValueAsString(data);
			// 顶层 JSON 字段命中 sensitiveFields 时整体替换（保护嵌套对象里的 password 等）
			for (String f : sensitive) {
				json = json.replaceAll("\"" + java.util.regex.Pattern.quote(f) + "\"\\s*:\\s*\"[^\"]*\"", "\"" + f + "\":\"***\"");
				json = json.replaceAll("\"" + java.util.regex.Pattern.quote(f) + "\"\\s*:\\s*[0-9]+", "\"" + f + "\":\"***\"");
			}
			return truncate(json, PARAMS_MAX_LEN);
		}
		catch (Exception e) {
			return "{\"_serializeError\":\"" + safeMessage(e) + "\"}";
		}
	}

	/** 排除不可序列化或会触发 IO 副作用的参数类型。 */
	private static boolean serializable(Object a) {
		if (a == null) return true;
		if (a instanceof HttpServletRequest) return false;
		if (a instanceof HttpServletResponse) return false;
		if (a instanceof MultipartFile) return false;
		if (a instanceof java.io.InputStream) return false;
		if (a instanceof java.io.OutputStream) return false;
		if (a instanceof byte[]) return false;
		return true;
	}

	/* ============================================================
	 *  上下文 / 工具
	 * ============================================================ */

	private static HttpServletRequest currentRequest() {
		try {
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			return attrs == null ? null : attrs.getRequest();
		}
		catch (Exception e) {
			return null;
		}
	}

	private static String headerOrNull(HttpServletRequest req, String... names) {
		for (String n : names) {
			String v = req.getHeader(n);
			if (notBlank(v) && !"unknown".equalsIgnoreCase(v)) {
				int comma = v.indexOf(',');
				return comma > 0 ? v.substring(0, comma).trim() : v.trim();
			}
		}
		return null;
	}

	private static Long parseLongHeader(HttpServletRequest req, String name) {
		String v = req.getHeader(name);
		if (!notBlank(v)) return null;
		try {
			return Long.parseLong(v);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	private static String extractResultCode(Object result) {
		if (result == null) return "200";
		// 反射读 Result.code（避免硬依赖 Result 类）
		try {
			Method m = result.getClass().getMethod("getCode");
			Object code = m.invoke(result);
			if (code != null) return code.toString();
		}
		catch (Exception ignore) {
		}
		return "200";
	}

	private static String safeMessage(Throwable t) {
		String m = t.getMessage();
		return m == null ? t.getClass().getSimpleName() : m;
	}

	private static Timestamp nowByDateUtils() {
		return DateUtils.convSQLDate(new Date());
	}

	private static String truncate(String s, int max) {
		if (s == null) return null;
		return s.length() <= max ? s : s.substring(0, max);
	}

	private static boolean notBlank(String s) {
		return s != null && !s.isEmpty();
	}
}
