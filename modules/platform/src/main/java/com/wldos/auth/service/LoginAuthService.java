/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.wldos.common.Constants;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.sys.base.dto.Tenant;
import com.wldos.auth.vo.Login;
import com.wldos.auth.vo.LoginAuthParams;
import com.wldos.auth.vo.MobileModifyParams;
import com.wldos.auth.vo.PasswdModifyParams;
import com.wldos.auth.vo.Register;
import com.wldos.support.auth.LoginUtils;
import com.wldos.sys.core.entity.WoUser;
import com.wldos.sys.base.service.AuthService;
import com.wldos.sys.core.service.UserService;
import com.wldos.sys.core.vo.User;
import com.wldos.support.auth.vo.JWT;
import com.wldos.support.auth.JWTTool;
import com.wldos.support.auth.vo.Token;
import com.wldos.support.auth.vo.UserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 登录相关认证、授权服务。
 *
 * @author 树悉猿
 * @date 2021/4/29
 * @version 1.0
 */
@Service
@SuppressWarnings({ "all"})
public class LoginAuthService {
	private final BeanCopier userCopier = BeanCopier.create(WoUser.class, UserInfo.class, false);

	private final JWTTool jwtTool;

	@Autowired
	@Lazy
	@Qualifier("commonOperation")
	private LoginUtils loginUtils;

	private final AuthService authService;

	private final UserService userService;

	private final AuthCodeService authCodeService;

	public LoginAuthService(JWTTool jwtTool, AuthService authService, UserService userService, AuthCodeService authCodeService) {
		this.jwtTool = jwtTool;
		this.authService = authService;
		this.userService = userService;
		this.authCodeService = authCodeService;
	}

	/**
	 * 登录平台
	 *
	 * @param domain 域名
	 * @param domainId 域Id
	 * @param comId 用户主企业id
	 * @param loginAuthParams 登陆参数
	 * @param request 请求
	 * @param hexKeyCode 十六进制加密key
	 * @return 登录实例
	 */
	public Login login(String domain, Long domainId, Long comId, LoginAuthParams loginAuthParams, HttpServletRequest request, String hexKeyCode) {
		Login user = new Login();
		// 先校验验证码
		String verifyCode = loginAuthParams.getVerifyCode();
		if (ObjectUtils.isBlank(verifyCode)) {
			user.setStatus("error");
			user.setNews("请输入验证码"); // 防止暴力破解
			user.setType(loginAuthParams.getType());
			return user;
		}
		else {
			if (!this.authCodeService.checkCode(verifyCode)) {
				user.setStatus("error");
				user.setNews("验证码错误"); // 防止暴力破解
				user.setType(loginAuthParams.getType());
				return user;
			}
		}

		UserInfo userInfo = this.validateLogin(loginAuthParams, hexKeyCode);
		if (ObjectUtils.isBlank(userInfo)) {
			return null;
		}

		user.setStatus("ok");

		JWT jwt = new JWT(userInfo.getId(), userInfo.getTenantId(), domainId);
		String accessToken = this.jwtTool.genToken(jwt);

		Token token = new Token(accessToken, this.jwtTool.getRefreshTime());
		//token.setRefreshToken(refreshToken); // 不再使用刷新token，使用访问token加超时机制就可以实现刷新机制
		user.setToken(token);

		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, comId, userInfo.getId());
		user.setCurrentAuthority(currentAuthority);

		// 登录用户信息审计、token记录
		this.jwtTool.recLog(domain, jwt, request);

		return user;
	}

	private UserInfo validateLogin(LoginAuthParams loginAuthParams, String hexKeyCode) {
		UserInfo userInfo = new UserInfo(); // @todo 新增多次登录错误自动锁定用户或ip的功能，防止暴力破解
		WoUser woUser = this.userService.findByLoginName(loginAuthParams.getUsername());
		if (woUser == null) {
			return null;
		}

		if (loginUtils.verify(loginAuthParams.getUsername(), loginAuthParams.getPassword(), woUser.getPasswd(), hexKeyCode)) {
			this.userCopier.copy(woUser, userInfo, null);
			userInfo.setUsername(woUser.getLoginName());
			Tenant tenant = this.userService.queryTenantInfoByTAdminId(userInfo.getId());
			userInfo.setTenantId(tenant.getId());
			return userInfo;
		}
		return null;
	}

	public User logout(String token, Long domainId) {
		this.jwtTool.delToken(domainId, token);
		return this.userService.queryGuest(domainId);
	}

	/**
	 * 注册平台用户
	 *
	 * @param domain 域名
	 * @param domainId 域Id
	 * @param register 注册实例
	 * @param request 请求实例
	 * @return 登录实例
	 */
	public Login register(String domain, Long domainId, Register register, HttpServletRequest request) {

		Login login = new Login();

		// 先校验验证码
		String verifyCode = register.getVerifyCode();
		if (ObjectUtils.isBlank(verifyCode)) {
			login.setStatus("error");
			login.setNews("请输入验证码"); // 防止注册机
			return login;
		}
		else {
			if (!this.authCodeService.checkCode(verifyCode)) {
				login.setStatus("error");
				login.setNews("验证码错误"); // 防止注册机
				return login;
			}
		}

		if (this.userService.existsByLoginName(register.getLoginName())) {
			login.setStatus("error");
			login.setNews("注册邮箱已存在"); // 邮箱就是登录名
			return login;
		}

		String passwdPlain = ObjectUtils.string(register.getPasswd());

		String passwd = loginUtils.encode(passwdPlain);
		register.setPasswd(passwd);

		WoUser user = this.userService.createUser(domainId, register);

		// 更新密码强度
		this.userService.updatePasswdStatus(passwdPlain, user.getId());

		login.setStatus("ok");

		JWT jwt = new JWT(user.getId(), Constants.TOP_COM_ID/* 新用户默认平台为主企业 */, domainId);
		String accessToken = this.jwtTool.genToken(jwt);
		Token token = new Token(accessToken);

		login.setToken(token);

		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, Constants.TOP_COM_ID, user.getId());
		login.setCurrentAuthority(currentAuthority);

		// 登录用户信息审计、token记录
		this.jwtTool.recLog(domain, jwt, request);

		return login;
	}

	/**
	 * 修改密码
	 *
	 * @param passwdModifyParams 密码修改参数
	 * @param hexKeyCode 16进制加密key
	 * @return 修改信息
	 */
	public Login changePasswd(PasswdModifyParams passwdModifyParams, String hexKeyCode) {
		Login login = new Login();
		// 验证原密码是否正确
		boolean userInfo = this.validateOldPasswd(passwdModifyParams, hexKeyCode);
		if (!userInfo) {
			login.setStatus("error");
			login.setNews("原密码错误");
			return login;
		}
		// 验证新密码与确认密码是否一致
		String passwd = passwdModifyParams.getPassword();
		String confirm = passwdModifyParams.getConfirm();
		if (ObjectUtils.isBlank(passwd) || !passwd.equals(confirm)) {
			login.setStatus("error");
			login.setNews("新密码与确认密码不一致");
			return login;
		}
		// 更新密码强度
		this.userService.updatePasswdStatus(passwd, passwdModifyParams.getId());
		// 保存新密码
		passwd = loginUtils.encode(passwd);
		passwdModifyParams.setPassword(passwd);
		this.userService.updateUser(passwdModifyParams);

		// 吊销原token的方式不可取，服务器不存储token，利用token有效期强制退出

		login.setStatus("ok");

		return login;
	}

	private boolean validateOldPasswd(PasswdModifyParams passwdModifyParams, String hexKeyCode) {
		WoUser woUser = this.userService.findById(passwdModifyParams.getId());
		if (woUser == null) {
			return false;
		}

		return loginUtils.verify(woUser.getUsername(), passwdModifyParams.getPassword(), woUser.getPasswd(), hexKeyCode);
	}

	/**
	 * 修改密保手机
	 *
	 * @param mobileModifyParams 密码手机修改参数
	 * @return 修改结果
	 */
	public Login changeMobile(MobileModifyParams mobileModifyParams) {
		return this.userService.changeMobile(mobileModifyParams);
	}

	/**
	 * 管理员后台设置新密码，直接覆盖原密码
	 *
	 * @param passwdModifyParams 密码修改参数
	 * @return 修改信息
	 */
	public Login changePasswd4admin(PasswdModifyParams passwdModifyParams) {
		Login login = new Login();
		// 验证新密码与确认密码是否一致
		String passwd = passwdModifyParams.getPassword();
		String confirm = passwdModifyParams.getConfirm();
		if (ObjectUtils.isBlank(passwd) || !passwd.equals(confirm)) {
			login.setStatus("error");
			login.setNews("新密码与确认密码不一致");
			return login;
		}
		// 更新密码强度
		this.userService.updatePasswdStatus(passwd, passwdModifyParams.getId());
		// 保存新密码
		passwd = loginUtils.encode(passwd);
		passwdModifyParams.setPassword(passwd);
		this.userService.updateUser(passwdModifyParams);
		login.setStatus("ok");

		return login;
	}

	/**
	 * 管理员管理端注册平台用户
	 *
	 * @param domainId 域id
	 * @param register 注册实例
	 * @return 登录实例
	 */
	public Login addUser4admin(Long domainId, Register register) {

		Login login = new Login();

		if (this.userService.existsByLoginName(register.getLoginName())) {
			login.setStatus("error");
			login.setNews("注册邮箱已存在"); // 邮箱就是登录名
			return login;
		}

		String passwdPlain = ObjectUtils.string(register.getPasswd());

		String passwd = loginUtils.encode(passwdPlain);
		register.setPasswd(passwd);

		WoUser user = this.userService.createUser(domainId, register);

		// 更新密码强度
		this.userService.updatePasswdStatus(passwdPlain, user.getId());

		login.setStatus("ok");

		return login;
	}
}