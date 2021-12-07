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

import com.wldos.support.util.DateUtils;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PasswdEncode;
import com.wldos.support.Constants;
import com.wldos.support.util.encrypt.AesEncryptUtils;
import com.wldos.system.auth.JWT;
import com.wldos.system.auth.JWTTool;
import com.wldos.system.auth.vo.MobileModifyParams;
import com.wldos.system.auth.vo.PasswdModifyParams;
import com.wldos.system.auth.dto.Tenant;
import com.wldos.system.auth.vo.LoginAuthParams;
import com.wldos.system.auth.vo.Login;
import com.wldos.system.auth.vo.Register;
import com.wldos.system.auth.vo.Token;
import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.service.AuthService;
import com.wldos.system.core.service.UserService;
import com.wldos.system.vo.User;
import com.wldos.system.core.entity.WoUser;
import org.apache.commons.codec.binary.Hex;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

/**
 *
 * @author 树悉猿
 * @date 2021/4/29
 * @version 1.0
 */
@Service
public class LoginAuthService {
	private final BeanCopier userCopier = BeanCopier.create(WoUser.class, UserInfo.class, false);

	private final JWTTool jwtTool;

	private final AuthService authService;

	private final UserService userService;

	private final AuthCodeService authCodeService;

	public LoginAuthService(JWTTool jwtTool, AuthService authService, UserService userService, AuthCodeService authCodeService) {
		this.jwtTool = jwtTool;
		this.authService = authService;
		this.userService = userService;
		this.authCodeService = authCodeService;
	}

	public Login login(String domain, Long comId, LoginAuthParams loginAuthParams, HttpServletRequest request, String hexKeyCode) {
		Login user = new Login();

		String verifyCode = loginAuthParams.getVerifyCode();
		if (ObjectUtil.isBlank(verifyCode)) {
			user.setStatus("error");
			user.setNews("请输入验证码");
			user.setType(loginAuthParams.getType());
			return user;
		}else {
			if (!this.authCodeService.checkCode(verifyCode)) {
				user.setStatus("error");
				user.setNews("验证码错误");
				user.setType(loginAuthParams.getType());
				return user;
			}
		}

		UserInfo userInfo = this.validateLogin(loginAuthParams, hexKeyCode);
		if (ObjectUtil.isBlank(userInfo)) {
			return null;
		}

		user.setStatus("ok");

		JWT jwt = new JWT(userInfo.getId(), userInfo.getTenantId());
		String accessToken = this.jwtTool.genToken(jwt);

		Token token = new Token();
		token.setAccessToken(accessToken);
		token.setRefresh(this.jwtTool.getRefreshTime());

		user.setToken(token);

		List<String> currentAuthority = this.authService.queryAuthorityButton(domain, comId, userInfo.getId());
		user.setCurrentAuthority(currentAuthority);


		this.jwtTool.recLog(domain, jwt, request);

		return user;
	}

	private UserInfo validateLogin(LoginAuthParams loginAuthParams, String hexKeyCode) {
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
			this.userCopier.copy(woUser, userInfo, null);
			userInfo.setUsername(woUser.getLoginName());
			Tenant tenant = this.userService.queryTenantInfoByTAdminId(userInfo.getId());
				userInfo.setTenantId(tenant.getId());
			return userInfo;
		}
		return null;
	}

	public User logout(String token, String domain) {
		this.jwtTool.delToken(domain, token);
		return this.userService.queryGuest(domain);
	}

	private String enCryHexKey(String username, String hexKeyCode) {
		String date = DateUtils.getDay(new Date());
		String firstName = username.charAt(0) + "";
		String secondName = username.charAt(1) + "";
		String key = firstName + hexKeyCode + secondName + date;

		return Hex.encodeHexString(key.getBytes(StandardCharsets.UTF_8));
	}

	public Login register(String domain, Register register, HttpServletRequest request) {

		Login login = new Login();

		String verifyCode = register.getVerifyCode();
		if (ObjectUtil.isBlank(verifyCode)) {
			login.setStatus("error");
			login.setNews("请输入验证码");
			return login;
		}else {
			if (!this.authCodeService.checkCode(verifyCode)) {
				login.setStatus("error");
				login.setNews("验证码错误");
				return login;
			}
		}

		if (this.userService.existsByLoginName(register.getLoginName())) {
			login.setStatus("error");
			login.setNews("注册邮箱已存在");
			return login;
		}

		String passwdPlain = ObjectUtil.string(register.getPasswd());

		String passwd = PasswdEncode.getInstance().encode(passwdPlain);
		register.setPasswd(passwd);

		WoUser user = this.userService.createUser(domain, register);

		this.userService.updatePasswdStatus(passwdPlain, user.getId());

		login.setStatus("ok");

		JWT jwt = new JWT(user.getId(), Constants.TOP_COM_ID);
		String accessToken = this.jwtTool.genToken(jwt);
		Token token = new Token();
		token.setAccessToken(accessToken);

		login.setToken(token);

		List<String> currentAuthority = this.authService.queryAuthorityButton(domain, Constants.TOP_COM_ID, user.getId());
		login.setCurrentAuthority(currentAuthority);

		this.jwtTool.recLog(domain, jwt, request);

		return login;
	}

	public Login changePasswd(PasswdModifyParams passwdModifyParams, String hexKeyCode) {
		Login login = new Login();

		boolean userInfo = this.validateOldPasswd(passwdModifyParams, hexKeyCode);
		if (!userInfo) {
			login.setStatus("error");
			login.setNews("原密码错误");
			return login;
		}

		String passwd = passwdModifyParams.getPassword();
		String confirm = passwdModifyParams.getConfirm();
		if (ObjectUtil.isBlank(passwd) || !passwd.equals(confirm)) {
			login.setStatus("error");
			login.setNews("新密码与确认密码不一致");
			return login;
		}

		this.userService.updatePasswdStatus(passwd, passwdModifyParams.getId());

		passwd = PasswdEncode.getInstance().encode(passwd);
		passwdModifyParams.setPassword(passwd);
		this.userService.updateUser(passwdModifyParams);

		login.setStatus("ok");

		return login;
	}

	private boolean validateOldPasswd(PasswdModifyParams passwdModifyParams, String hexKeyCode) {
		WoUser woUser = this.userService.findById(passwdModifyParams.getId());
		if (woUser == null) {
			return false;
		}
		String passwdEnc = woUser.getPasswd();
		String passwdInput = "";
		try {
			String aesKey = enCryHexKey(woUser.getLoginName(), hexKeyCode);
			passwdInput = AesEncryptUtils.aesDecrypt(passwdModifyParams.getOldPasswd(), aesKey);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return PasswdEncode.getInstance().matches(passwdInput, passwdEnc);
	}

	public Login changeMobile(MobileModifyParams mobileModifyParams) {
		return this.userService.changeMobile(mobileModifyParams);
	}

	public Login changePasswd4admin(PasswdModifyParams passwdModifyParams) {
		Login login = new Login();

		String passwd = passwdModifyParams.getPassword();
		String confirm = passwdModifyParams.getConfirm();
		if (ObjectUtil.isBlank(passwd) || !passwd.equals(confirm)) {
			login.setStatus("error");
			login.setNews("新密码与确认密码不一致");
			return login;
		}

		this.userService.updatePasswdStatus(passwd, passwdModifyParams.getId());

		passwd = PasswdEncode.getInstance().encode(passwd);
		passwdModifyParams.setPassword(passwd);
		this.userService.updateUser(passwdModifyParams);
		login.setStatus("ok");

		return login;
	}

	public Login addUser4admin(String domain, Register register) {

		Login login = new Login();

		if (this.userService.existsByLoginName(register.getLoginName())) {
			login.setStatus("error");
			login.setNews("注册邮箱已存在");
			return login;
		}

		String passwdPlain = ObjectUtil.string(register.getPasswd());

		String passwd = PasswdEncode.getInstance().encode(passwdPlain);
		register.setPasswd(passwd);

		WoUser user = this.userService.createUser(domain, register);

		this.userService.updatePasswdStatus(passwdPlain, user.getId());

		login.setStatus("ok");

		return login;
	}
}
