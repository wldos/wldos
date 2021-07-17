/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.wldos.support.util.AddressUtils;
import com.wldos.support.util.DateUtils;
import com.wldos.support.util.IpUtils;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PasswdEncode;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.support.util.encrypt.AesEncryptUtils;
import com.wldos.system.auth.JWT;
import com.wldos.system.auth.JWTTool;
import com.wldos.system.auth.dto.Tenant;
import com.wldos.system.auth.entity.LoginInfo;
import com.wldos.system.auth.dto.LoginAuthParams;
import com.wldos.system.auth.vo.Login;
import com.wldos.system.auth.vo.Register;
import com.wldos.system.auth.vo.Token;
import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.service.AuthService;
import com.wldos.system.core.service.UserService;
import com.wldos.system.vo.User;
import com.wldos.system.core.entity.WoUser;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.codec.binary.Hex;

import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 登录相关认证、授权服务。
 *
 * @author 树悉猿
 * @date 2021/4/29
 * @version 1.0
 */
@Service
public class LoginAuthService {
	private final JWTTool jwtTool;

	private final AuthService authService;

	private final UserService userService;

	public LoginAuthService(JWTTool jwtTool, AuthService authService, UserService userService) {
		this.jwtTool = jwtTool;
		this.authService = authService;
		this.userService = userService;
	}

	public Login login(LoginAuthParams loginAuthParams, HttpServletRequest request, String hexKeyCode) {
		Login user = new Login();
		UserInfo userInfo = this.validateLogin(loginAuthParams, hexKeyCode);
		if (!ObjectUtil.isBlank(userInfo)) {
			user.setStatus("ok");

			JWT jwt = new JWT(userInfo.getId(), userInfo.getTenantId(), userInfo.getUsername(), userInfo.getName());
			String accessToken = this.jwtTool.genToken(jwt);

			Token token = new Token();
			token.setAccessToken(accessToken);
			//token.setRefreshToken(refreshToken); // 不再使用刷新token，使用访问token加超时机制就可以实现刷新机制
			user.setToken(token);

			List<String> currentAuthority = this.authService.queryAuthorityButton(userInfo.getId());
			user.setCurrentAuthority(currentAuthority);

			// 登录用户信息审计、token记录
			this.recLog(jwt, accessToken, request);

			return user;
		}

		return null;
	}

	@Async
	public void recLog(JWT jwt, String token, HttpServletRequest request) {
		final UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		final String ip = IpUtils.getClientIp(request);
		String address = AddressUtils.getRealAddressByIP(ip);

		LoginInfo loginInfo = new LoginInfo();

		String os = userAgent.getOperatingSystem().getName();
		String useragent = userAgent.getBrowser().getName();
		loginInfo.setId(jwt.getId());
		loginInfo.setUserId(jwt.getUserId());
		loginInfo.setLoginName(jwt.getLoginName());
		loginInfo.setUserAgent(useragent);
		loginInfo.setLoginIP(ip);
		loginInfo.setNetLocation(address);
		loginInfo.setOs(os);
		loginInfo.setLoginTime(System.currentTimeMillis());
		this.jwtTool.recToken(token, JSON.toJSONString(loginInfo, false));
	}

	public UserInfo validateLogin(LoginAuthParams loginAuthParams, String hexKeyCode) {
		UserInfo userInfo = new UserInfo();
		WoUser woUser = this.userService.findByLoginName(loginAuthParams.getUsername());
		if (woUser == null) {
			return null;
		}
		String passwdEnc = woUser.getPasswd();
		String passwdInput = "";
		try {
			String aesKey = enCryHexKey(loginAuthParams.getUsername(), hexKeyCode);
			passwdInput = AesEncryptUtils.aesDecrypt(loginAuthParams.getPassword(), aesKey);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (PasswdEncode.getInstance().matches(passwdInput, passwdEnc)) {
			BeanUtils.copyProperties(woUser, userInfo);
			Tenant tenant = this.userService.queryTenantInfoByTAdminId(userInfo.getId());
			if (tenant == null) // 用户没有设置主企业，默认为平台
				userInfo.setTenantId(PubConstants.TOP_COM_ID);
			else
				userInfo.setTenantId(tenant.getId());
			return userInfo;
		}
		return null;
	}

	public User logout(String token) {
		this.jwtTool.delToken(token);
		return this.userService.queryGuest();
	}

	/**
	 * 16位16进制密码加密key生成策略
	 *
	 * @param username 用户登陆名
	 * @return 加密后的密码
	 */
	private String enCryHexKey(String username, String hexKeyCode) {
		String date = DateUtils.getDay(new Date());
		String firstName = username.charAt(0) + "";
		String secondName = username.charAt(1) + "";
		String key = firstName + hexKeyCode + secondName + date;

		return Hex.encodeHexString(key.getBytes(StandardCharsets.UTF_8));
	}

	public Login register(Register register, HttpServletRequest request) {
		String passwd = ObjectUtil.string(register.getPasswd());
		passwd = PasswdEncode.getInstance().encode(passwd);
		register.setPasswd(passwd);

		WoUser user = this.userService.createUser(register);
		Login login = new Login();
		login.setStatus("ok");

		JWT jwt = new JWT(user.getId(), PubConstants.TOP_COM_ID,/* 新用户默认平台为主企业 */ user.getLoginName(), user.getAccountName());
		String accessToken = this.jwtTool.genToken(jwt);
		Token token = new Token();
		token.setAccessToken(accessToken);

		login.setToken(token);

		List<String> currentAuthority = this.authService.queryAuthorityButton(user.getId());
		login.setCurrentAuthority(currentAuthority);

		// 登录用户信息审计、token记录
		this.recLog(jwt, accessToken, request);

		return login;
	}

	public Login refreshToken(String refreshToken) {
		Login login = new Login();
		String newToken = this.userService.refreshToken(refreshToken);

		if (ObjectUtil.isBlank(newToken)) {
			login.setStatus("error");
		} else {
			Token token = new Token();
			token.setAccessToken(newToken);
			login.setStatus("ok");
			login.setToken(token);
		}

		return login;
	}
}
