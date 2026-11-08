/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.wldos.platform.auth.clickcaptcha.ClickCaptchaProvider;
import com.wldos.platform.auth.clickcaptcha.ClickCaptchaSupport;
import com.wldos.platform.auth.vo.CaptchaVO;
import com.wldos.platform.auth.vo.ClickCaptchaVerifyParams;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.framework.mvc.service.NonEntityService;
import io.github.wldos.common.enums.RedisKeyEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.common.utils.UUIDUtils;
import io.github.wldos.common.utils.captcha.VerifyCode;
import com.wldos.platform.core.service.MailService;
import com.wldos.platform.core.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 验证码服务。
 *
 * @author Yuanxi Universe
 * @date 2021/9/10
 * @version 1.0
 */
@Service
public class AuthCodeService extends NonEntityService {

	private static final String CAPTCHA_BOUND_EMAIL = "wldos:captcha:email:%s";

	private static final String CAPTCHA_RESET_MISMATCH = "wldos:captcha:reset-mismatch:%s";

	private static final String FORGET_PROOF = "wldos:forget:proof:%s";

	private static final String FORGET_EMAIL_ADMIT = "wldos:forget:email-admit:%s";

	private static final int EMAIL_CAPTCHA_TTL_MINUTES = 2;

	private static final int RESET_LOGIN_MISMATCH_MAX = 5;

	private static final int FORGET_EMAIL_ADMIT_TTL_MINUTES = 10;

	private final MailService mailService;

	private final UserService userService;

	private final LoginHostAdmissionPolicy loginHostAdmissionPolicy;

	private final ClickCaptchaProvider clickCaptchaProvider;

	public AuthCodeService(
			MailService mailService,
			UserService userService,
			LoginHostAdmissionPolicy loginHostAdmissionPolicy,
			ClickCaptchaProvider clickCaptchaProvider) {
		this.mailService = mailService;
		this.userService = userService;
		this.loginHostAdmissionPolicy = loginHostAdmissionPolicy;
		this.clickCaptchaProvider = clickCaptchaProvider;
	}

	/**
	 * 验证码校验
	 *
	 * @param captchaVO 验证码参数
	 * @return 是否通过
	 */
	public boolean checkCode(CaptchaVO captchaVO) {
		return this.checkCode(captchaVO, null, null);
	}

	public boolean checkCode(CaptchaVO captchaVO, Long requestDomainId, Long contextComId) {
		if (captchaVO == null) {
			return false;
		}
		if (StringUtils.hasText(captchaVO.getForgetProof())) {
			if (!this.verifyForgetProofBinding(
					captchaVO.getForgetProof(),
					captchaVO.getUuid(),
					null,
					requestDomainId,
					contextComId)) {
				return false;
			}
		}
		// 获取缓存中的验证码
		String authCode = this.getAuthCodeFromCache(captchaVO.getUuid());

		if (authCode == null) {
			return false;
		}
		// 判断验证码
		return authCode.toLowerCase().trim().equals(ObjectUtils.string(captchaVO.getCaptcha()).toLowerCase());
	}

	/**
	 * 验证码校验
	 *
	 * @param captcha 验证码
	 * @param uuid uuid
	 * @return 是否通过
	 */
	public boolean checkCode(String captcha, String uuid) {
		// 获取缓存中的验证码
		String authCode = this.getAuthCodeFromCache(uuid);
		if (authCode == null) {
			return false;
		}
		// 判断验证码
		return authCode.toLowerCase().trim().equals(ObjectUtils.string(captcha).toLowerCase());
	}

	/**
	 * 验证码校验, 仅用于当前包下
	 *
	 * @param verifyCode 验证码+uuid，约定：4位验证码开头
	 * @return 是否通过
	 */
	boolean checkCode(String verifyCode) {
		String captcha = verifyCode.substring(0, 4);
		String uuid = verifyCode.substring(4);
		return this.checkCode(captcha, uuid);
	}

	private String getAuthCodeFromCache(String uuid) {
		return ObjectUtils.string(this.cache.get(String.format(RedisKeyEnum.CAPTCHA.toString(), uuid)));
	}

	private String captchaKey(String uuid) {
		return String.format(RedisKeyEnum.CAPTCHA.toString(), uuid);
	}

	private String boundEmailKey(String uuid) {
		return String.format(CAPTCHA_BOUND_EMAIL, uuid);
	}

	private String resetMismatchKey(String uuid) {
		return String.format(CAPTCHA_RESET_MISMATCH, uuid);
	}

	private String normalizeEmail(String email) {
		return ObjectUtils.string(email).trim().toLowerCase(Locale.ROOT);
	}

	/**
	 * 找回密码专用：校验邮箱验证码，且 uuid 须为发往该邮箱的 code4email 会话。
	 */
	public boolean verifyEmailCaptchaForReset(String captcha, String uuid, String email) {
		if (ObjectUtils.isBlank(captcha) || ObjectUtils.isBlank(uuid)) {
			return false;
		}
		String authCode = this.getAuthCodeFromCache(uuid);
		if (authCode == null) {
			return false;
		}
		if (!authCode.toLowerCase().trim().equals(ObjectUtils.string(captcha).toLowerCase().trim())) {
			return false;
		}
		String bound = ObjectUtils.string(this.cache.get(this.boundEmailKey(uuid)));
		if (ObjectUtils.isBlank(bound)) {
			return false;
		}
		return bound.equals(this.normalizeEmail(email));
	}

	/**
	 * 登录名与邮箱不匹配时累计次数，超限作废该 uuid 下验证码。
	 *
	 * @return true 表示已作废验证码
	 */
	public boolean recordResetLoginNameMismatch(String uuid) {
		if (ObjectUtils.isBlank(uuid)) {
			return false;
		}
		String key = this.resetMismatchKey(uuid);
		int fail = 0;
		Object stored = this.cache.get(key);
		if (stored != null) {
			try {
				fail = Integer.parseInt(stored.toString());
			}
			catch (NumberFormatException ignored) {
				fail = 0;
			}
		}
		fail++;
		if (fail >= RESET_LOGIN_MISMATCH_MAX) {
			this.consumeEmailCaptcha(uuid);
			return true;
		}
		this.cache.set(key, String.valueOf(fail), EMAIL_CAPTCHA_TTL_MINUTES, TimeUnit.MINUTES);
		return false;
	}

	/**
	 * 作废邮箱找回密码验证码会话（仅 code4email 产生的 uuid）。
	 */
	public void consumeEmailCaptcha(String uuid) {
		if (ObjectUtils.isBlank(uuid)) {
			return;
		}
		this.cache.remove(this.captchaKey(uuid));
		this.cache.remove(this.boundEmailKey(uuid));
		this.cache.remove(this.resetMismatchKey(uuid));
	}

	public void consumeForgetProof(String proof) {
		if (ObjectUtils.isBlank(proof)) {
			return;
		}
		this.cache.remove(this.forgetProofKey(proof));
	}

	/**
	 * 校验发码凭证：须由当前 Host 下成功的 code4email 签发。
	 */
	public boolean verifyForgetProofBinding(
			String proof,
			String uuid,
			String email,
			Long requestDomainId,
			Long contextComId) {
		if (ObjectUtils.isBlank(proof)) {
			return false;
		}
		String raw = ObjectUtils.string(this.cache.get(this.forgetProofKey(proof)));
		if (ObjectUtils.isBlank(raw)) {
			return false;
		}
		String[] parts = raw.split("\\|", 4);
		if (parts.length < 4) {
			return false;
		}
		if (StringUtils.hasText(uuid) && !uuid.equals(parts[1])) {
			return false;
		}
		if (StringUtils.hasText(email) && !this.normalizeEmail(email).equals(parts[0])) {
			return false;
		}
		if (!this.forgetBindingMatchesSite(parts[2], parts[3], requestDomainId, contextComId)) {
			return false;
		}
		return true;
	}

	private String forgetProofKey(String proof) {
		return String.format(FORGET_PROOF, proof);
	}

	private String forgetEmailAdmitKey(String admitToken) {
		return String.format(FORGET_EMAIL_ADMIT, admitToken);
	}

	private void storeForgetProof(
			String proof,
			String normalizedEmail,
			String captchaUuid,
			Long requestDomainId,
			Long contextComId) {
		if (requestDomainId == null || requestDomainId <= 0) {
			throw new BaseException("找回密码须绑定有效站点");
		}
		long domainPart = requestDomainId;
		long comPart = contextComId != null && contextComId > 0 ? contextComId : 0L;
		StringBuilder payload = new StringBuilder(normalizedEmail);
		payload.append('|').append(captchaUuid);
		payload.append('|').append(domainPart);
		payload.append('|').append(comPart);
		this.cache.set(
				this.forgetProofKey(proof),
				payload.toString(),
				EMAIL_CAPTCHA_TTL_MINUTES,
				TimeUnit.MINUTES);
	}

	/**
	 * 生成验证码
	 *
	 * @return 验证码和uuid
	 */
	public Map<String, String> genCode() {
		// 验证码宽、高、色位: 120 40 1
		int width = 120;
		int height = 40;
		int colorBit = 1;

		VerifyCode verifyCode = VerifyCode.genCaptcha(width, height, colorBit);
		String uid = UUIDUtils.generateShortUuid();
		String text = verifyCode.getText();
		// 验证码有效期120秒
		this.cache.set(String.format(RedisKeyEnum.CAPTCHA.toString(), uid), text, 2, TimeUnit.MINUTES);

		String cap = verifyCode.outBase64();
		Map<String, String> map = new HashMap<>();
		map.put("authcode", cap);
		map.put("uuid", uid);

		return map;
	}

	/**
	 * 生成验证码
	 *
	 * @return 验证码和uuid
	 */
	public Map<String, String> genCodeMobile(String mobile) {

		String uid = UUIDUtils.generateShortUuid();
		String text = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		// 验证码有效期120秒
		this.cache.set(String.format(RedisKeyEnum.CAPTCHA.toString(), uid), text, 2, TimeUnit.MINUTES);

		// todo 发文本短信给待验证手机

		Map<String, String> map = new HashMap<>();
		map.put("uuid", uid);

		return map;
	}

	public Map<String, Object> genClickChallenge(String poolKey) {
		return this.clickCaptchaProvider.createChallenge(poolKey);
	}

	public String verifyClickChallenge(ClickCaptchaVerifyParams params) {
		return this.clickCaptchaProvider.verify(params);
	}

	public boolean isForgetSecondaryVerifyEnabled() {
		return this.clickCaptchaProvider.forgetPasswordRequiresSecondaryVerify();
	}

	/**
	 * 校验邮箱是否可在本站找回（不发信）。商业二次验证启用时须先提交 graphPass。
	 */
	public Map<String, String> admitForgetEmail(
			String email,
			String graphPass,
			Long requestDomainId,
			Long contextComId) {
		Map<String, String> map = new HashMap<>();
		if (requestDomainId == null || requestDomainId <= 0) {
			return this.forgetUserNotOnSiteResult();
		}
		if (this.isForgetSecondaryVerifyEnabled()) {
			if (!ClickCaptchaSupport.consumeGraphPass(this.cache, graphPass)) {
				map.put("status", "error");
				map.put("hint", "安全验证已失效，请重试");
				return map;
			}
		}
		String normalized = this.normalizeEmail(email);
		WoUser owner = this.findForgetOwner(normalized, requestDomainId, contextComId);
		if (owner == null) {
			return this.forgetUserNotOnSiteResult();
		}
		String admitToken = UUIDUtils.generateShortUuid();
		long domainPart = requestDomainId;
		long comPart = contextComId != null && contextComId > 0 ? contextComId : 0L;
		StringBuilder payload = new StringBuilder(normalized);
		payload.append('|').append(domainPart);
		payload.append('|').append(comPart);
		this.cache.set(
				this.forgetEmailAdmitKey(admitToken),
				payload.toString(),
				FORGET_EMAIL_ADMIT_TTL_MINUTES,
				TimeUnit.MINUTES);
		map.put("status", "ok");
		map.put("admitToken", admitToken);
		this.putLoginNameHint(map, owner, normalized);
		return map;
	}

	public boolean verifyForgetEmailAdmit(
			String admitToken,
			String email,
			Long requestDomainId,
			Long contextComId) {
		if (ObjectUtils.isBlank(admitToken)) {
			return false;
		}
		String raw = ObjectUtils.string(this.cache.get(this.forgetEmailAdmitKey(admitToken)));
		if (ObjectUtils.isBlank(raw)) {
			return false;
		}
		String[] parts = raw.split("\\|", 3);
		if (parts.length < 3) {
			return false;
		}
		if (!parts[0].equals(this.normalizeEmail(email))) {
			return false;
		}
		if (!this.forgetBindingMatchesSite(parts[1], parts[2], requestDomainId, contextComId)) {
			return false;
		}
		return true;
	}

	/**
	 * 找回密码发送邮箱验证码。账号登录名与邮箱不同时必须提交登录名，且登录名对应用户的绑定邮箱须与该邮箱一致，否则不发信。
	 *
	 * @param loginName 用户填写的登录名；登录名即邮箱时可为空
	 */
	public Map<String, String> genCodeEmail(String to, String loginName, Long requestDomainId, Long contextComId) {
		Map<String, String> map = new HashMap<>();
		if (requestDomainId == null || requestDomainId <= 0) {
			map.put("sent", "false");
			return map;
		}
		String uid = UUIDUtils.generateShortUuid();
		String normalized = this.normalizeEmail(to);

		WoUser owner = this.findForgetOwner(normalized, requestDomainId, contextComId);
		if (owner == null) {
			map.put("sent", "false");
			return map;
		}
		String inputLoginName = ObjectUtils.string(loginName).trim();
		if (!ObjectUtils.isBlank(inputLoginName)) {
			WoUser target = this.userService.findByLoginName(inputLoginName, requestDomainId);
			if (!this.isForgetResetTarget(target, normalized, requestDomainId, contextComId)) {
				map.put("sent", "false");
				map.put("hint", "登录名与邮箱不匹配");
				return map;
			}
			owner = target;
		}
		else if (!this.isSameEmail(owner.getLoginName(), normalized)) {
			map.put("sent", "false");
			map.put("hint", "请填写登录名");
			return map;
		}

		this.putLoginNameHint(map, owner, normalized);

		String text = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		this.cache.set(this.captchaKey(uid), text, EMAIL_CAPTCHA_TTL_MINUTES, TimeUnit.MINUTES);
		this.cache.set(this.boundEmailKey(uid), normalized, EMAIL_CAPTCHA_TTL_MINUTES, TimeUnit.MINUTES);

		String subject = this.getPlatDomain() + "邮件验证码";
		this.mailService.sendEmailText(to, subject, text, 1L, "0.0.0.0");

		String forgetProof = UUIDUtils.generateShortUuid();
		this.storeForgetProof(forgetProof, normalized, uid, requestDomainId, contextComId);

		map.put("uuid", uid);
		map.put("sent", "true");
		map.put("forgetProof", forgetProof);

		return map;
	}

	/**
	 * 校验邮箱是否已注册，并提示找回密码时登录名是否即为该邮箱。
	 */
	public Map<String, String> describeEmailForPasswordReset(String email, Long requestDomainId, Long contextComId) {
		Map<String, String> map = new HashMap<>();
		if (requestDomainId == null || requestDomainId <= 0) {
			return this.forgetUserNotOnSiteResult();
		}
		String normalized = this.normalizeEmail(email);
		WoUser owner = this.findForgetOwner(normalized, requestDomainId, contextComId);
		if (owner == null) {
			return this.forgetUserNotOnSiteResult();
		}
		map.put("status", "ok");
		this.putLoginNameHint(map, owner, normalized);
		return map;
	}

	/**
	 * 邮箱在库内可对应多个用户（不同租户可共用同一邮箱），只取在当前站点允许登录的那个。
	 */
	private WoUser findForgetOwner(String normalizedEmail, Long requestDomainId, Long contextComId) {
		for (WoUser candidate : this.userService.listNormalByEmail(normalizedEmail, requestDomainId)) {
			if (this.canResetPasswordOnCurrentHost(candidate, requestDomainId, contextComId)) {
				return candidate;
			}
		}
		return null;
	}

	private void putLoginNameHint(Map<String, String> map, WoUser owner, String normalizedEmail) {
		boolean same = this.isSameEmail(owner.getLoginName(), normalizedEmail);
		map.put("accountMatchesEmail", Boolean.toString(same));
		if (same) {
			map.put("loginName", normalizedEmail);
		}
	}

	private boolean isSameEmail(String a, String b) {
		return this.normalizeEmail(a).equals(this.normalizeEmail(b));
	}

	/**
	 * 重置密码目标校验：登录名对应用户的绑定邮箱须与验证邮箱一致，且该用户可在当前站点登录。
	 */
	public boolean isForgetResetTarget(WoUser user, String email, Long requestDomainId, Long contextComId) {
		return user != null
				&& !ObjectUtils.isBlank(email)
				&& this.isSameEmail(user.getEmail(), email)
				&& this.canResetPasswordOnCurrentHost(user, requestDomainId, contextComId);
	}

	private boolean canResetPasswordOnCurrentHost(WoUser user, Long requestDomainId, Long contextComId) {
		if (requestDomainId == null || requestDomainId <= 0) {
			return false;
		}
		if (user == null || user.getId() == null || user.getId() <= 0) {
			return false;
		}
		try {
			this.loginHostAdmissionPolicy.assertPasswordLoginAllowed(user.getId(), requestDomainId, contextComId);
			return true;
		}
		catch (BaseException ignored) {
			return false;
		}
	}

	private Map<String, String> forgetUserNotOnSiteResult() {
		Map<String, String> map = new HashMap<>();
		map.put("status", "error");
		map.put("hint", "邮箱不存在");
		return map;
	}

	private boolean forgetBindingMatchesSite(
			String storedDomainPart,
			String storedComPart,
			Long requestDomainId,
			Long contextComId) {
		if (ObjectUtils.isBlank(storedDomainPart)) {
			return false;
		}
		long boundDomain;
		try {
			boundDomain = Long.parseLong(storedDomainPart.trim());
		}
		catch (NumberFormatException ex) {
			return false;
		}
		if (boundDomain <= 0) {
			return false;
		}
		if (requestDomainId == null || requestDomainId <= 0 || !requestDomainId.equals(boundDomain)) {
			return false;
		}
		if (ObjectUtils.isBlank(storedComPart)) {
			return true;
		}
		long boundCom;
		try {
			boundCom = Long.parseLong(storedComPart.trim());
		}
		catch (NumberFormatException ex) {
			return true;
		}
		if (boundCom <= 0) {
			return true;
		}
		return contextComId != null && contextComId > 0 && contextComId.equals(boundCom);
	}
}
