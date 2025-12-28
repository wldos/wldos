/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import com.wldos.platform.auth.enums.PasswdStatusEnum;
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
import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.common.Constants;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.http.IpUtils;
import com.wldos.platform.core.dto.Tenant;
import com.wldos.platform.core.service.AuthService;
import com.wldos.platform.support.auth.LoginUtils;
import com.wldos.framework.support.auth.vo.JWT;
import com.wldos.framework.support.auth.vo.Token;
import com.wldos.platform.support.auth.vo.UserInfo;
import com.wldos.platform.support.plugins.extension.ExtensionPoint;
import com.wldos.platform.core.enums.SysOptionEnum;
import com.wldos.platform.core.enums.UserStatusEnum;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.service.MailService;
import com.wldos.platform.core.service.UserService;
import com.wldos.platform.core.vo.User;

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
 * @author 元悉宇宙
 * @date 2021/4/29
 * @version 1.0
 */
@RefreshScope
@Service
@SuppressWarnings({ "all" })
@Transactional(rollbackFor = Exception.class)
public class LoginAuthService extends NonEntityService {
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
	 * @return 登录实例
	 */
	public Login login(String domain, Long domainId, Long comId, LoginAuthParams loginAuthParams, HttpServletRequest request) {
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

		// 记录失败次数，key: ip, value: count, 当count大于设定值时 拒绝该用户的登录
		String ip = IpUtils.getClientIp(request);
		String keyIp = "limit-" + domain + ip; // 加域名前缀
		String keyUser = keyIp + "-" + loginAuthParams.getUsername();
		int count = 0;
		int countIp = 0;
		Object o = null;

		if ((o = this.cache.get(keyIp)) != null) {
			countIp = Integer.parseInt(o.toString());
		}
		if (countIp >= 9) { // 默认9次重试机会,屏蔽这个ip
			user.setStatus("error");
			user.setNews("失败次数过多，已锁定当前ip");
			user.setType(loginAuthParams.getType());
			getLog().warn("失败次数过多，已锁定当前ip: {}", ip);
			return user;
		}

		if ((o = this.cache.get(keyUser)) != null) {
			count = Integer.parseInt(o.toString());
		}
		if (count >= 3) { // 默认3次重试机会
			user.setStatus("error");
			user.setNews("失败次数超过3次，已锁定，请稍后再试");
			user.setType(loginAuthParams.getType());
			getLog().warn("user locked: {} ip: {}", loginAuthParams.getUsername(), ip);

			return user;
		}

		UserInfo userInfo = this.validateLogin(loginAuthParams);
		if (ObjectUtils.isBlank(userInfo)) {
			this.cache.set(keyUser, ++count, 30, TimeUnit.MINUTES);
			this.cache.set(keyIp, ++countIp, 30, TimeUnit.MINUTES);
			return null;
		}

		// 在限制次数内登录成功，解除计数，防止累计封锁ip，仅封锁同ip的错误的用户
		this.cache.remove(keyUser);
		this.cache.remove(keyIp);

		user.setStatus("ok");

		// 使用统一的Token生成方法
		int tokenTimeoutMinutes = loginAuthParams.isAutoLogin() ? 60 * 24 * 15 : this.jwtTool.getTokenTimeout();
		Token token = this.jwtTool.genToken(userInfo.getId(), userInfo.getTenantId(), domainId, tokenTimeoutMinutes);
		//token.setRefreshToken(refreshToken); // 不再使用刷新token，使用访问token加超时机制就可以实现刷新机制
		user.setToken(token);

		// 邮箱激活状态检查
		if (this.isEmailAction) {
			WoUser woUser = this.userService.findByLoginName(loginAuthParams.getUsername());
			if (UserStatusEnum.notActive.getValue().equals(woUser.getStatus())) {
				user.setStatus("notActive");
				user.setNews("您的账号需要邮箱激活！");
				user.setType(loginAuthParams.getType());
			}
		}

		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, comId, userInfo.getId());
		user.setCurrentAuthority(currentAuthority);

		// 登录用户信息审计、token记录
		this.jwtTool.recLog(domain, token, request);
		// todo 1.预埋hook测试
		this.wsHook.doInvoke(ExtensionPoint.USER_LOGIN.getCode(), userInfo.getUsername());

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

	private UserInfo validateLogin(LoginAuthParams loginAuthParams) {
		UserInfo userInfo = new UserInfo();
		WoUser woUser = this.userService.findByLoginName(loginAuthParams.getUsername());
		if (woUser == null) {
			return null;
		}

		if (loginUtils.verifyRSA(loginAuthParams.getPassword(), woUser.getPasswd())) {
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

	@Value("${app.register.passwd.maxLength}")
	private Integer passwdMaxLenth;

	@Value("${app.register.username.forbidden}")
	private String forbbidenName;

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

		// 1.非法用户名校验，杜绝常用用户
		if ((","+this.forbbidenName+",").indexOf(","+register.getNickname()+",") > -1) {
			login.setStatus("error");
			login.setNews("此昵称禁用，请使用其他名称"); // 敏感名称屏蔽
			return login;
		}

		String passwdPlain = ObjectUtils.string(register.getPasswd());

		// 2.密码长度校验，拒绝长密码攻击
		if (passwdPlain.length() > this.passwdMaxLenth) {
			login.setStatus("error");
			login.setNews("密码超长，建议不要超过20位"); // 防止注册机
			return login;
		}

		// 3.密码强度校验，不允许弱密码
		PasswdStatusEnum status = PasswdStatusCheck.check(passwdPlain);
		if (status.getStatus().equals(PasswdStatusEnum.POOR.getStatus())) {
			login.setStatus("error");
			login.setNews("请设置8位以上密码（字母、数字、特殊字符的至少两种组合）"); // 等保要求
			return login;
		}

		String passwd = loginUtils.encode(passwdPlain);
		register.setPasswd(passwd);

		WoUser user = this.userService.createUser(domainId, register, this.isEmailAction);

		// 更新密码强度
		this.userService.updatePasswdStatus(passwdPlain, user.getId());

		login.setStatus("ok");

		// 使用统一的Token生成方法，新用户默认平台为主企业
		Token token = this.jwtTool.genToken(user.getId(), Constants.TOP_COM_ID, domainId, this.jwtTool.getTokenTimeout());

		login.setToken(token);

		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, Constants.TOP_COM_ID, user.getId());
		login.setCurrentAuthority(currentAuthority);

		// 发激活邮件到注册邮箱，受全局配置开关控制，邮箱注册激活开关，用id、loginName、createTime创建verify token
		if (this.isEmailAction) {
			String actUrl = this.userService.getDomainUrlById(domainId) + "/user/active/verify=" + user.getId();
			String activeEmail = "<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/>"
					+ "<title>账户激活</title></head><body>"
					+ "您好，这是验证邮件，请点击下面的链接完成验证，</body></html>"
					+ "<a href=\"" + actUrl + "\" target=\"_blank\">激活账号" + actUrl + "</a><br/>"
					+ "如果以上链接无法点击，请将它复制到您的浏览器地址栏中进入访问，该链接24小时内有效。";
			String subject = this.getPlatDomain() + "账号激活邮件";
			this.mailService.sendMailHtml(register.getLoginName(), subject, activeEmail, user.getId(), user.getRegisterIp());
		}

		// 登录用户信息审计、token记录
		this.jwtTool.recLog(domain, token, request);

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
				this.userService.update(user, false);

				WoOrg orgUnActive = this.userService.queryUserOrg(SysOptionEnum.UN_ACTIVE_GROUP.getKey());
				WoOrg orgActive = this.userService.queryUserOrg(SysOptionEnum.DEFAULT_GROUP.getKey());
				// 切换用户组织
				String res = this.userService.updateUserOrg(uId, orgUnActive.getId(), orgActive.getId());

				if ("invalid".equals(res)) {
					login.setNews("激活已失效");
					login.setStatus("error");
					return login;
				}

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
			this.getLog().warn("非法或冗余操作：用户激活{}，domain: {} userId: {}", login.getNews(), domain, active.getVerify());
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

		// 2.密码长度校验，拒绝长密码攻击
		if (passwd.length() > this.passwdMaxLenth) {
			login.setStatus("error");
			login.setNews("密码超长，建议不要超过20位"); // 防止注册机
			return login;
		}

		// 3.密码强度校验，不允许弱密码
		PasswdStatusEnum status = PasswdStatusCheck.check(passwd);
		if (status.getStatus().equals(PasswdStatusEnum.POOR.getStatus())) {
			login.setStatus("error");
			login.setNews("请设置8位以上密码（字母、数字、特殊字符的至少两种组合）"); // 等保要求
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
		// 2.密码长度校验，拒绝长密码攻击
		if (passwd.length() > this.passwdMaxLenth) {
			login.setStatus("error");
			login.setNews("密码超长，建议不要超过20位"); // 防止注册机
			return login;
		}

		// 3.密码强度校验，不允许弱密码
		PasswdStatusEnum status = PasswdStatusCheck.check(passwd);
		if (status.getStatus().equals(PasswdStatusEnum.POOR.getStatus())) {
			login.setStatus("error");
			login.setNews("请设置8位以上密码（字母、数字、特殊字符的至少两种组合）"); // 等保要求
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

	public String passwdStatusCheck(String passwd) {
		return PasswdStatusCheck.check(passwd).getStatus();
	}
}