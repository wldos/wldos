/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wldos.framework.mvc.service.EntityService;
import io.github.wldos.common.Constants;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import io.github.wldos.platform.support.rbac.RbacRowScope;
import io.github.wldos.platform.support.rbac.WriteScopeTarget;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 标准实体基础 controller，用于收口业务系统请求、响应边界。
 * 非实体 controller 基础上增加 CRUD 方法。
 *
 * <p>平台能力（{@code this.store} 等）与 Web 层注入的索引见 {@link NonEntityController}。</p>
 *
 * @param <S> 实体service
 * @param <E> 拥有数据库表的实体
 * @author Yuanxi Universe
 * @date 2021-04-16
 * @version 1.0
 * @see NonEntityController
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class EntityController<S extends EntityService, E> extends NonEntityController<S> {

	/**
	 * 标准实体新增。
	 *
	 * <p><b>审计</b>：自动记入 {@code wo_op_log}，由切面 {@code OpLogAspect} 拦截：
	 * <ul>
	 *   <li>{@code module} 缺省 → 取类上 {@code @Api.tags}（项目惯例必填，无需重复定义）；</li>
	 *   <li>{@code action} 显式 = "新增"；</li>
	 *   <li>{@code resourceId} 取 {@code #entity.id}（saveOrUpdate 内部分配雪花 ID 后回填到入参对象上，
	 *       因此切面 {@code @Around} 在业务方法返回后能拿到 id；解析失败回填 null 不影响业务）；</li>
	 *   <li>{@code recordParams} = true：默认脱敏字段（password/passwd/token/secret/captcha 等）已覆盖最常见敏感项；
	 *       业务实体若有额外敏感字段，请在子类覆写并补 {@code sensitiveFields}。</li>
	 * </ul>
	 *
	 * <p><b>覆写注意</b>：子类如显式覆写本方法，**必须**重新声明 {@code @OpLog}；
	 * Java 方法注解默认不被覆写所继承（参见 {@code 02-OpLog-API覆盖清单.md § 1.3}）。
	 * 若实体含 {@code comId} 语义（跨租户场景），覆写时应比照本方法追加 {@code targetComId = "#entity.comId"}。
	 */
	@PostMapping("add")
	@OpLog(action = "新增", resourceId = "#entity.id", targetComId = "#entity.comId")
	public String addEntity(@RequestBody E entity) {
		this.preAdd(entity);
		service.saveOrUpdate(entity);  // 统一使用 saveOrUpdate() 方法，自动判断 insert/update
		this.postAdd(entity);
		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 新增实体时，对实体二次处理的hook。默认按 {@link RbacRowScope} 反射判定归属轴并调用
	 * {@link #applyWriteScope(Object, WriteScopeTarget)} 打标 {@code comId}/{@code domainId}
	 * ——与 {@link #assertEntityRowScope(Object)} 的"默认 comId=true"语义对称，三重校验点的
	 * 写入侧不再需要业务子类逐个手写。子类需要额外业务校验或自定义归属轴时应 {@code @Override}
	 * 并自行调用等价打标（覆写后不会再触发本默认逻辑）。
	 *
	 * @param entity 实体bean的实例
	 */
	protected void preAdd(E entity) {
		this.stampWriteScopeIfEligible(entity);
	}

	/**
	 * 按实体是否声明 {@link RbacRowScope} 以及声明内容，推断归属轴目标并打标；
	 * 未声明该注解时按"默认 comId=true"处理（与 {@code DataScopeEntityAssert.assertRow} 一致），
	 * 实体既无 {@code getComId}/{@code setComId} 时打标动作本身安全 no-op，不会报错。
	 */
	private void stampWriteScopeIfEligible(Object entity) {
		if (entity == null) {
			return;
		}
		RbacRowScope scope = entity.getClass().getAnnotation(RbacRowScope.class);
		if (scope != null && !scope.comId()) {
			return; // 显式声明不参与租户轴（平台目录型实体），跳过
		}
		WriteScopeTarget target = (scope != null && scope.domainId())
				? WriteScopeTarget.SITE_AXIS : WriteScopeTarget.TENANT_AXIS;
		this.applyWriteScope(entity, target);
	}

	/**
	 * 新增完成后，触发的操作，比如刷新缓存
	 */
	protected void postAdd(E entity) {
	}

	/**
	 * 标准实体更新。审计语义与 {@link #addEntity(Object)} 一致；详见类上方说明与 {@code @OpLog}。
	 *
	 * <p>子类如覆写本方法（如解决 Ambiguous mapping），<b>必须</b>重新贴 {@code @OpLog} 注解，
	 * 若实体含 {@code comId} 语义（跨租户场景），应比照本方法追加 {@code targetComId = "#entity.comId"}。
	 */
	@PostMapping("update")
	@OpLog(action = "更新", resourceId = "#entity.id", targetComId = "#entity.comId")
	public String updateEntity(@RequestBody E entity) {
		this.preUpdate(entity);
		service.saveOrUpdate(entity);  // 统一使用 saveOrUpdate() 方法，自动判断 insert/update
		this.postUpdate(entity);
		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 更新实体时，对实体二次处理的hook。默认按请求体 id 回查现有行做行级校验（三重校验点之一，
	 * 跨边界行禁止 UPDATE，见 {@link #assertEntityRowScope(Object)}）——不能只信任请求体自带的归属字段，
	 * 否则客户端清空 comId/domainId 即可绕过。子类需要额外业务校验（如角色可管理性、层守卫）时应
	 * {@code @Override} 并自行调用等价校验。
	 *
	 * @param entity 实体bean的实例
	 */
	protected void preUpdate(E entity) {
		this.assertEntityRowScope(this.loadExistingRowForScope(entity));
	}

	/**
	 * 更新完成后，触发的操作，比如刷新缓存
	 */
	protected void postUpdate(E entity) {
	}

	/**
	 * 标准实体单条删除。审计语义同上。
	 *
	 * <p>{@code recordParams} = false：删除一般不需要在日志里记录被删实体的全部字段值
	 * （如需保留删除前快照，业务侧应在 {@link #preDelete(Object)} hook 里另外记业务追溯日志）。
	 */
	@DeleteMapping("delete")
	@OpLog(action = "删除", resourceId = "#entity.id", recordParams = false)
	public String removeEntity(@RequestBody E entity) {
		this.preDelete(entity);
		this.service.delete(entity);
		this.postDelete(entity);
		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 删除实体时，对实体二次处理的hook。默认按请求体 id 回查现有行做行级校验，语义同 {@link #preUpdate(Object)}。
	 *
	 * @param entity 实体bean的实例
	 */
	protected void preDelete(E entity) {
		this.assertEntityRowScope(this.loadExistingRowForScope(entity));
	}

	/**
	 * 删除完成后，触发的操作，比如刷新缓存
	 */
	protected void postDelete(E entity) {
	}

	/**
	 * 批量物理删
	 * @param jsonObject 批量参数 Map<String, Object> key="ids"
	 *
	 * <p>审计：{@code resourceId} 取 {@code #jsonObject['ids']}，落表的会是 {@code "[123, 456, ...]"}
	 * 字符串形式（{@code Object#toString}），便于跨条 group by 分析批量操作影响面。
	 */
	@DeleteMapping("deletes")
	@OpLog(action = "批量删除", resourceId = "#jsonObject['ids']")
	public String removeEntitiesByIds(@RequestBody Map<String, Object> jsonObject) {
		Object ids = jsonObject.get("ids");
		if (ids != null) {
			List<Object> objects = (List<Object>) ids;
			this.preDeletes(objects);
			service.deletePhysicalByIds(objects.toArray());

			this.postDeletes(objects);
		}

		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 批量删除实体时，对实体二次处理的hook。默认逐条回查现有行做行级校验，语义同 {@link #preUpdate(Object)}。
	 *
	 * @param ids 实例ids
	 */
	protected void preDeletes(List<Object> ids) {
		if (ids == null) {
			return;
		}
		for (Object raw : ids) {
			this.assertEntityRowScope(this.service.findById(this.parseIdForScope(raw)));
		}
	}

	/** 批量删除请求体 ids 多为 JSON 反序列化后的 String/Integer，统一转为 Long 再回查，避免 PK 类型不匹配。 */
	private Object parseIdForScope(Object raw) {
		if (raw instanceof Number) {
			return ((Number) raw).longValue();
		}
		try {
			return Long.parseLong(raw.toString());
		}
		catch (NumberFormatException ex) {
			return raw;
		}
	}

	/**
	 * 按请求体自带 id 回查数据库中的现有行，用于 {@link #preUpdate(Object)}/{@link #preDelete(Object)}
	 * 的行级校验——绝不能直接校验请求体本身（客户端可清空归属字段绕过）。
	 */
	@SuppressWarnings("unchecked")
	private E loadExistingRowForScope(E entity) {
		if (entity == null) {
			return null;
		}
		Object id = readEntityId(entity);
		return id != null ? (E) this.service.findById(id) : null;
	}

	private Object readEntityId(Object entity) {
		try {
			return entity.getClass().getMethod("getId").invoke(entity);
		}
		catch (ReflectiveOperationException ignored) {
			return null;
		}
	}

	/**
	 * 批量删除完成后，触发的操作，比如刷新缓存
	 */
	protected void postDeletes(List<Object> ids) {
	}

	/**
	 * 标准详情查询：默认即按 {@link #assertEntityRowScope(Object)} 做行级校验（三重校验点之一，
	 * 详情/ID 直查/外键引用不可用）；子类需要额外业务校验（如角色可管理性）时应 {@code @Override}
	 * 并显式调用 {@code assertEntityRowScope}，不能静默跳过。
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("get")
	public Object getEntityById(@RequestParam long id) {
		E entity = (E) service.findById(id);
		if (entity != null) {
			this.assertEntityRowScope(entity);
		}
		return entity;
	}

	@GetMapping("all")
	public List<E> allEntities() {
		Class<E> clazz = this.service.getEntityClass(1);
		if (!this.isMultiTenancy() || this.service.isAdmin(this.getUserId()) || !this.isTenantScopedListEntity(clazz)) {
			return this.doFilter(service.findAll());
		}
		Map<String, Object> cond = new HashMap<>();
		Long comId = this.getWorkComId();
		if (comId == null) {
			comId = this.getTenantId();
		}
		cond.put(clazz.getSimpleName().equals(Constants.CLASS_NAME_COMPANY) ? "id" : Constants.COMMON_KEY_TENANT_COLUMN, comId);
		return this.doFilter(this.service.findAllWithCond(clazz, cond));
	}

	/** {@code /all} 下拉/label 用途：仅对带租户轴的实体按 workComId 过滤；平台全局目录（如 wo_resource）不过滤。 */
	private boolean isTenantScopedListEntity(Class<?> clazz) {
		RbacRowScope scope = clazz.getAnnotation(RbacRowScope.class);
		if (scope != null) {
			return scope.comId();
		}
		try {
			clazz.getMethod("getComId");
			return true;
		}
		catch (NoSuchMethodException ex) {
			return false;
		}
	}

	/**
	 * 结果集过滤器
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	protected List<E> doFilter(List<E> res) {
		return res;
	}

	/**
	 * 不带参数的分页查询，标准实体。带参数的查询，请参照ResourceController。
	 *
	 * @param params 请求参数
	 * @return 分页数据
	 */
	@Deprecated
	@GetMapping("page")
	public Page<E> entityList(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		Pageable page = PageRequest.of(pageQuery.getCurrent() - 1, pageQuery.getPageSize(), pageQuery.getSorter());

		return service.findAll(page);
	}
}