/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.controller;

import javax.servlet.http.HttpServletRequest;

import io.github.wldos.common.res.Result;
import io.github.wldos.framework.support.audit.ILoginLogger;
import io.github.wldos.framework.support.audit.LoginEvent;
import io.github.wldos.framework.support.audit.LoginEventType;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import com.wldos.platform.auth.service.LoginAuthService;
import com.wldos.platform.auth.vo.ActiveParams;
import com.wldos.platform.auth.vo.BakEmailModifyParams;
import com.wldos.platform.auth.vo.Login;
import com.wldos.platform.auth.vo.LoginAuthParams;
import com.wldos.platform.auth.vo.MFAModifyParams;
import com.wldos.platform.auth.vo.MobileModifyParams;
import com.wldos.platform.auth.vo.PasswdModifyParams;
import com.wldos.platform.auth.vo.PasswdResetParams;
import com.wldos.platform.auth.vo.Register;
import com.wldos.platform.auth.vo.SecQuestModifyParams;
import com.wldos.framework.mvc.controller.NonEntityController;
import io.github.wldos.common.Constants;
import io.github.wldos.platform.support.auth.KeyConfig;
import com.wldos.platform.core.vo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 登录相关认证、授权controller。
 *
 * @author Yuanxi Universe
 * @date 2021/4/29
 * @version 1.0
 */
@Api(tags = "登录认证")
@RefreshScope
@RequestMapping("login")
@RestController
public class LoginAuthController extends NonEntityController<LoginAuthService> {
	private final KeyConfig keyConfig;

	/**
	 * 登录日志记录器（契约接口，{@code wldos-framework} 提供默认异步实现，
	 * 商业增强或插件可通过覆盖 {@link ILoginLogger} bean 替换实现）。
	 *
	 * <p>{@code @Autowired(required=false)}：在裁剪部署或日志组件被禁用时，
	 * 登录链路仍可正常工作（调用前判空），不强耦合。
	 */
	@Autowired(required = false)
	private ILoginLogger loginLogger;

	public LoginAuthController(KeyConfig keyConfig) {
		this.keyConfig = keyConfig;
	}

	/* ============================================================
	 *  审计日志收集（私有 helper，集中所有登录路径的事件构造与分发）
	 * ============================================================ */

	/** 当前请求是否登录成功（{@code Login.status} 为 "ok"，对应 {@code Login.status} 枚举）。 */
	private static boolean isOk(Login user) {
		return user != null && user.getStatus() != null
				&& "ok".equalsIgnoreCase(user.getStatus());
	}

	/**
	 * 记录一条登录类事件。
	 *
	 * <p>{@code userId} 由 {@link LoginAuthService#queryUserIdForAudit(String)} 按 {@code account}
	 * 反查（一次主键/唯一索引 lookup，旁路调用，查不到返回 {@code null} 不抛错）：
	 * <ul>
	 *   <li>登录成功：{@code account} 一定存在，user_id 可定位；</li>
	 *   <li>登录失败（用户不存在/拼错账号）：反查为 {@code null}，仅靠 {@code login_account} 定位审计行——
	 *       这正好是合规审计想要的语义"看到一个根本不存在的账号在尝试登录"；</li>
	 *   <li>失败-账号锁定/未激活：用户其实存在，user_id 也能填上，便于按 user 维度统计风险事件。</li>
	 * </ul>
	 */
	private void audit(LoginEventType type, String account, Login result) {
		if (this.loginLogger == null) {
			return;
		}
		String ip = this.getUserIp();
		String ua = this.request == null ? null : this.request.getHeader("User-Agent");
		Long userId = this.service.queryUserIdForAudit(account);
		LoginEvent ev;
		if (isOk(result)) {
			ev = LoginEvent.success(userId, account, type, ip, ua);
		}
		else {
			String reason = result == null ? null : result.getNews();
			ev = LoginEvent.fail(userId, account, type, reason, ip, ua);
		}
		ev.withTenant(this.getDomainId(), this.getTenantId());
		this.loginLogger.recordAsync(ev);
	}

	@ApiOperation(value = "获取加密公钥", notes = "获取RSA公钥用于前端加密密码")
	@GetMapping("encrypt")
	public Result fetchPubKey() {
		byte[] pubKey = this.keyConfig.getUserPubKey();
		return Result.ok(pubKey == null ? "" : KeyConfig.toHexString(pubKey));
	}

	@ApiOperation(value = "账户密码登录", notes = "使用用户名和密码进行登录")
	@PostMapping("account")
	public Login loginAuth(HttpServletRequest request, @ApiParam(value = "登录参数", required = true) @Valid @RequestBody LoginAuthParams loginAuthParams) {

		getLog().info("{} login in ", loginAuthParams.getUsername());
		Login user = this.service.login(this.getDomain(), this.getDomainId(), this.getTenantId(), loginAuthParams, request);
		if (user == null) {
			getLog().warn("{} 登录失败", loginAuthParams.getUsername());
			user = new Login();
			user.setStatus("error");
			user.setNews("登陆失败，请重试！");
			user.setType(loginAuthParams.getType());
		}
		audit(LoginEventType.ACCOUNT, loginAuthParams.getUsername(), user);
		return user;
	}

	@ApiOperation(value = "手机验证码登录", notes = "使用手机号和验证码进行登录")
	@PostMapping("login4mobile")
	public Login loginAuthMobile(HttpServletRequest request, @ApiParam(value = "登录参数", required = true) @Valid @RequestBody LoginAuthParams loginAuthParams) {

		getLog().info("{} login in ", loginAuthParams.getUsername());
		// todo 改造为手机验证码登录验证逻辑
		Login user = this.service.login(this.getDomain(), this.getDomainId(), this.getTenantId(), loginAuthParams, request);
		if (user == null) {
			getLog().warn("{} 登录失败", loginAuthParams.getUsername());
			user = new Login();
			user.setStatus("error");
			user.setNews("登陆失败，请重试！");
			user.setType(loginAuthParams.getType());
		}
		audit(LoginEventType.MOBILE, loginAuthParams.getUsername(), user);
		return user;
	}

	@ApiOperation(value = "退出登录", notes = "用户退出登录，清除token")
	@DeleteMapping("logout")
	public User logout(@ApiParam(value = "访问令牌", required = true) @RequestHeader(value = Constants.TOKEN_ACCESS_HEADER) String token) {
		// 先抓 logout 前的上下文：当前 token 还有效，gateway 注入的 user_id 头部可读，
		// 用户名也能从 token 缓存里查到；service.logout 之后这些来源都会被清掉。
		final Long userId = this.getUserId();
		final String account = queryAccountSafely(userId);
		final String ip = this.getUserIp();
		final String ua = this.request == null ? null : this.request.getHeader("User-Agent");
		final Long domainId = this.getDomainId();
		final Long tenantId = this.getTenantId();

		try {
			return this.service.logout(token, domainId);
		}
		finally {
			// try/finally 包住，确保即使 service.logout 抛异常（如 queryGuest 时缓存命中失败）
			// 也能落审计；recordAsync 自身再用 try/catch 兜一层，绝不影响主链路。
			if (this.loginLogger != null) {
				try {
					LoginEvent ev = LoginEvent.success(userId, account, LoginEventType.LOGOUT, ip, ua);
					ev.withTenant(domainId, tenantId);
					this.loginLogger.recordAsync(ev);
				}
				catch (Exception ex) {
					getLog().warn("logout audit failed: {}", ex.getMessage());
				}
			}
		}
	}

	/**
	 * 安全地查 logout 前的真实账号名（仅用于审计字段填充）。
	 *
	 * <p>不向上抛异常：审计是旁路链路，账号查不到就退化为 null，由 {@code userId} 兜底定位。
	 */
	private String queryAccountSafely(Long userId) {
		if (userId == null || Constants.GUEST_ID.equals(userId)) {
			return null;
		}
		try {
			return this.service.queryUsernameForAudit(userId);
		}
		catch (Exception ex) {
			return null;
		}
	}

	@ApiOperation(value = "用户注册", notes = "新用户注册账号")
	@PostMapping("register")
	public Login register(@ApiParam(value = "注册参数", required = true) @Valid @RequestBody Register register) {
		register.setId(this.nextId());
		register.setRegisterIp(this.getUserIp());
		register.setLoginName(register.getEmail());
		getLog().info("register= {} ", register.getLoginName());
		Login user = this.service.register(this.getDomain(), this.getDomainId(), register, this.request);
		audit(LoginEventType.REGISTER, register.getLoginName(), user);
		return user;
	}

	@ApiOperation(value = "检查密码强度", notes = "检查密码强度等级")
	@GetMapping("passwd/status")
	public String passwdStatusCheck(@ApiParam(value = "密码", required = true) @RequestParam String passwd) {
		String status = this.service.passwdStatusCheck(passwd);
		
		return this.resJson.ok("status", status);
	}

	@ApiOperation(value = "激活账号", notes = "通过激活码激活用户账号")
	@PostMapping("active")
	public Login active(@ApiParam(value = "激活参数", required = true) @Valid @RequestBody ActiveParams active) {
		return this.service.active(this.getDomain(), active, this.request);
	}

	@ApiOperation(value = "重置密码", notes = "通过邮箱验证码重置用户密码")
	@PostMapping("reset")
	public Login resetPasswd(@ApiParam(value = "重置密码参数", required = true) @Valid @RequestBody PasswdResetParams resetParams) {

		getLog().info("用户登录名: {} 密码重置 ", resetParams.getLoginName());
		Login user = this.service.resetPasswd(resetParams);
		if (user == null) {
			getLog().warn("{} 密码重置失败", resetParams.getLoginName());
			user = new Login();
			user.setStatus("error");
			user.setNews("密码重置失败，请重试！");
		}
		audit(LoginEventType.RESET, resetParams.getLoginName(), user);
		return user;
	}

	@ApiOperation(value = "修改密码", notes = "用户修改登录密码")
	@PostMapping("passwd")
	@OpLog(module = "账号安全", action = "修改密码", resourceType = "user", resourceId = "#passwdModifyParams.id")
	public Login changePasswd(@ApiParam(value = "修改密码参数", required = true) @Valid @RequestBody PasswdModifyParams passwdModifyParams,
			@Value("${passwd.hexKey.code}") String hexKeyCode) {

		getLog().info("用户id: {} 密码修改 ", passwdModifyParams.getId());
		Login user = this.service.changePasswd(passwdModifyParams, hexKeyCode);
		if (user == null) {
			getLog().warn("{} 密码修改失败", passwdModifyParams.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密码修改失败，请重试！");
		}
		return user;
	}

	@ApiOperation(value = "修改密保手机", notes = "用户修改密保手机号")
	@PostMapping("mobile")
	@OpLog(module = "账号安全", action = "修改密保手机", resourceType = "user", resourceId = "#mobileModifyParams.id")
	public Login changeMobile(@ApiParam(value = "修改手机参数", required = true) @Valid @RequestBody MobileModifyParams mobileModifyParams) {
		getLog().info("用户id: {} 密保手机修改 ", mobileModifyParams.getId());
		Login user = this.service.changeMobile(mobileModifyParams);
		if (user == null) {
			getLog().warn("{} 密保手机修改失败", mobileModifyParams.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密保手机修改失败，请重试！");
		}
		return user;
	}

	@ApiOperation(value = "修改密保问题", notes = "用户修改密保问题")
	@PostMapping("secQuest")
	@OpLog(module = "账号安全", action = "修改密保问题", resourceType = "user", resourceId = "#params.id")
	public Login changeSecQuest(@ApiParam(value = "修改密保问题参数", required = true) @Valid @RequestBody SecQuestModifyParams params) {
		getLog().info("用户id: {} 密保问题修改 ", params.getId());
		Login user = this.service.changeSecQuest(params);
		if (user == null) {
			getLog().warn("{} 密保问题修改失败", params.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密保问题修改失败，请重试！");
		}
		return user;
	}

	@ApiOperation(value = "修改备用邮箱", notes = "用户修改备用邮箱")
	@PostMapping("bakEmail")
	@OpLog(module = "账号安全", action = "修改备用邮箱", resourceType = "user", resourceId = "#params.id")
	public Login changeBakEmail(@ApiParam(value = "修改备用邮箱参数", required = true) @Valid @RequestBody BakEmailModifyParams params) {
		getLog().info("用户id: {} 备用邮箱修改 ", params.getId());
		Login user = this.service.changeBakEmail(params);
		if (user == null) {
			getLog().warn("{} 备用邮箱修改失败", params.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("备用邮箱修改失败，请重试！");
		}
		return user;
	}

	@ApiOperation(value = "修改密保设备", notes = "用户修改MFA密保设备")
	@PostMapping("mfa")
	@OpLog(module = "账号安全", action = "修改MFA设备", resourceType = "user", resourceId = "#params.id")
	public Login changeMFA(@ApiParam(value = "修改MFA参数", required = true) @Valid @RequestBody MFAModifyParams params) {
		getLog().info("用户id: {} 密保设备修改 ", params.getId());
		Login user = this.service.changeMFA(params);
		if (user == null) {
			getLog().warn("{} 密保设备修改失败", params.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密保设备修改失败，请重试！");
		}
		return user;
	}
}
