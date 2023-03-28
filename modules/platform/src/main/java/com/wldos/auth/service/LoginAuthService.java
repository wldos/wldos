/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.wldos.auth.vo.ActiveParams;
import com.wldos.auth.vo.BakEmailModifyParams;
import com.wldos.auth.vo.Login;
import com.wldos.auth.vo.LoginAuthParams;
import com.wldos.auth.vo.MFAModifyParams;
import com.wldos.auth.vo.MobileModifyParams;
import com.wldos.auth.vo.PasswdModifyParams;
import com.wldos.auth.vo.PasswdResetParams;
import com.wldos.auth.vo.Register;
import com.wldos.auth.vo.SecQuestModifyParams;
import com.wldos.base.NoRepoService;
import com.wldos.common.Constants;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.auth.LoginUtils;
import com.wldos.support.auth.vo.JWT;
import com.wldos.support.auth.vo.Token;
import com.wldos.support.auth.vo.UserInfo;
import com.wldos.sys.base.dto.Tenant;
import com.wldos.sys.base.enums.SysOptionEnum;
import com.wldos.sys.base.service.AuthService;
import com.wldos.sys.core.entity.WoOrg;
import com.wldos.sys.core.entity.WoUser;
import com.wldos.sys.core.enums.UserStatusEnum;
import com.wldos.sys.core.service.MailService;
import com.wldos.sys.core.service.UserService;
import com.wldos.sys.core.vo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 登录相关认证、授权服务。
 *
 * @author 树悉猿
 * @date 2021/4/29
 * @version 1.0
 */
@RefreshScope
@Service
@SuppressWarnings({ "all" })
@Transactional(rollbackFor = Exception.class)
public class LoginAuthService extends NoRepoService {
	private final BeanCopier userCopier = BeanCopier.create(WoUser.class, UserInfo.class, false);

	/** 是否开启邮箱注册激活开关，开启后注册用户需要从邮箱激活 */
	@Value("${wldos_platform_user_register_emailaction:true}")
	protected boolean isEmailAction;

	@Autowired
	@Lazy
	@Qualifier("commonOperation")
	private LoginUtils loginUtils;

	private final AuthService authService;

	private final UserService userService;

	private final AuthCodeService authCodeService;

	private final MailService mailService;

	public LoginAuthService(AuthService authService, UserService userService, AuthCodeService authCodeService, MailService mailService) {
		this.authService = authService;
		this.userService = userService;
		this.authCodeService = authCodeService;
		this.mailService = mailService;
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

		// 邮箱激活状态检查
		if (this.isEmailAction) {
			WoUser woUser = this.userService.findByLoginName(loginAuthParams.getUsername());
			if (UserStatusEnum.notActive.getValue().equals(woUser.getStatus())) {
				user.setStatus("notActive");
				user.setNews("您的账号需要邮箱激活！");
				user.setType(loginAuthParams.getType());

				return user;
			}
		}

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

		WoUser user = this.userService.createUser(domainId, register, this.isEmailAction);

		// 更新密码强度
		this.userService.updatePasswdStatus(passwdPlain, user.getId());

		login.setStatus("ok");

		JWT jwt = new JWT(user.getId(), Constants.TOP_COM_ID/* 新用户默认平台为主企业 */, domainId);
		String accessToken = this.jwtTool.genToken(jwt);
		Token token = new Token(accessToken);

		login.setToken(token);

		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, Constants.TOP_COM_ID, user.getId());
		login.setCurrentAuthority(currentAuthority);

		// 发激活邮件到注册邮箱，受全局配置开关控制，邮箱注册激活开关，用id、loginName、createTime创建verify token
		if (this.isEmailAction) {
			String actUrl = this.reqProtocol + "://" + this.userService.getDomainUrlById(domainId) + "/user/active/verify=" + user.getId();
			String activeEmail = "<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/>"
					+ "<title>账户激活</title></head><body>"
					+ "您好，这是验证邮件，请点击下面的链接完成验证，</body></html>"
					+ "<a href=\"" + actUrl + "\" target=\"_blank\">激活账号</a>" + actUrl + "<br/>"
					+ "如果以上链接无法点击，请将它复制到您的浏览器地址栏中进入访问，该链接24小时内有效。";
			String subject = this.wldosDomain + "账号激活邮件";
			this.mailService.sendMailHtml(register.getLoginName(), subject, activeEmail, user.getId(), user.getRegisterIp());
		}

		// 登录用户信息审计、token记录
		this.jwtTool.recLog(domain, jwt, request);

		return login;
	}

	/**
	 * 邮件激活用户账号
	 *
	 * @param domain 域名
	 * @param active 激活信息
	 * @param request 请求
	 * @return 激活结果
	 */
	public Login active(String domain, ActiveParams active, HttpServletRequest request) {
		// 验证id是否合法：存在状态为待激活的记录
		Long uId = Long.parseLong(active.getVerify());
		boolean exists = this.userService.existsById(uId);

		Login login = new Login();

		// 更新状态，激活用户
		if (exists) {
			try {
				WoUser user = new WoUser();
				user.setId(uId);
				user.setStatus(UserStatusEnum.normal.getValue());
				this.userService.update(user);

				WoOrg orgUnActive = this.userService.queryUserOrg(SysOptionEnum.UN_ACTIVE_GROUP.getKey());
				WoOrg orgActive = this.userService.queryUserOrg(SysOptionEnum.DEFAULT_GROUP.getKey());
				// 切换用户组织
				this.userService.updateUserOrg(uId, orgUnActive.getId(), orgActive.getId());

				login.setNews("激活成功");
				login.setStatus("ok");

				// 激活用户信息审计、token记录
				this.getLog().info("用户激活{}，domain: {} userId: {}", login.getNews(), domain, active.getVerify());
			}
			catch (Exception e) {
				login.setNews("激活失败");
				login.setStatus("error");
				throw new RuntimeException(e);
			}
		}
		else {
			login.setNews("无效操作");
			login.setStatus("failure");
			this.getLog().info("非法或冗余操作：用户激活{}，domain: {} userId: {}", login.getNews(), domain, active.getVerify());
		}

		return login;
	}

	/**
	 * 重置密码
	 *
	 * @param passwdModifyParams 密码修改参数
	 * @param hexKeyCode 16进制加密key
	 * @return 修改信息
	 */
	public Login resetPasswd(PasswdResetParams resetParams) {
		Login login = new Login();
		// 验证是否合法重置操作：验证验证码是否正确（有效期内）
		boolean isValid = this.authCodeService.checkCode(resetParams.getCaptcha(), resetParams.getUuid());

		if (!isValid) {
			login.setStatus("error");
			login.setNews("验证码失效，请重试");
			return login;
		}
		// 验证用户名和鉴权的邮箱是否绑定关系
		boolean isExists = this.userService.existsByEmailAndLoginName(resetParams.getEmail(), resetParams.getLoginName());
		if (!isExists) {
			login.setStatus("error");
			login.setNews("使用了与邮箱不匹配的用户名");
			return login;
		}
		// 验证新密码与确认密码是否一致
		String passwd = resetParams.getPassword();
		String confirm = resetParams.getConfirm();
		if (ObjectUtils.isBlank(passwd) || !passwd.equals(confirm)) {
			login.setStatus("error");
			login.setNews("新密码与确认密码不一致");
			return login;
		}
		// 更新密码强度
		WoUser user = this.userService.findByLoginName(resetParams.getLoginName());
		this.userService.updatePasswdStatus(passwd, user.getId());
		// 保存新密码
		passwd = loginUtils.encode(passwd);
		PasswdModifyParams modifyParams = new PasswdModifyParams();
		modifyParams.setPassword(passwd);
		modifyParams.setId(user.getId());
		this.userService.updateUser(modifyParams);

		login.setStatus("ok");

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
	 * @param params 密码手机修改参数
	 * @return 修改结果
	 */
	public Login changeMobile(MobileModifyParams params) {
		return this.userService.changeMobile(params);
	}

	/**
	 * 修改密保手机
	 *
	 * @param params 密码手机修改参数
	 * @return 修改结果
	 */
	public Login changeSecQuest(SecQuestModifyParams params) {
		return this.userService.changeSecQuest(params);
	}

	/**
	 * 修改备用邮箱
	 *
	 * @param params 备用邮箱修改参数
	 * @return 修改结果
	 */
	public Login changeBakEmail(BakEmailModifyParams params) {
		return this.userService.changeBakEmail(params);
	}

	/**
	 * 修改MFA设备
	 *
	 * @param params MFA设备修改
	 * @return 修改结果
	 */
	public Login changeMFA(MFAModifyParams params) {
		return this.userService.changeMFA(params);
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

		WoUser user = this.userService.createUser(domainId, register, isEmailAction);

		// 更新密码强度
		this.userService.updatePasswdStatus(passwdPlain, user.getId());

		login.setStatus("ok");

		return login;
	}
}