/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.wldos.auth.vo.CaptchaVO;
import com.wldos.framework.service.NoRepoService;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.UUIDUtils;
import com.wldos.common.utils.captcha.VerifyCode;
import com.wldos.sys.core.service.MailService;
import com.wldos.sys.core.service.UserService;

import org.springframework.stereotype.Service;

/**
 * 验证码服务。
 *
 * @author 元悉宇宙
 * @date 2021/9/10
 * @version 1.0
 */
@Service
public class AuthCodeService extends NoRepoService {

	private final MailService mailService;

	private final UserService userService;

	public AuthCodeService(MailService mailService, UserService userService) {
		this.mailService = mailService;
		this.userService = userService;
	}

	/**
	 * 验证码校验
	 *
	 * @param captchaVO 验证码参数
	 * @return 是否通过
	 */
	public boolean checkCode(CaptchaVO captchaVO) {
		if (captchaVO == null)
			return false;
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

	/**
	 * 生成验证码
	 *
	 * @return 验证码和uuid
	 */
	public Map<String, String> genCodeEmail(String to) {
		Map<String, String> map = new HashMap<>();
		String uid = UUIDUtils.generateShortUuid();

		// 验证邮箱是否存在，如果不存在说明非法邮箱，不发送验证码
		boolean isExists = this.userService.existsByEmail(to);
		if (!isExists) {
			map.put("uuid", uid);
			return map;
		}

		String text = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		// 验证码有效期120秒
		this.cache.set(String.format(RedisKeyEnum.CAPTCHA.toString(), uid), text, 2, TimeUnit.MINUTES);

		// 发文本短信给待验证邮件
		String subject = this.wldosDomain + "邮件验证码";
		this.mailService.sendEmailText(to, subject, text, 1L, "0.0.0.0");

		map.put("uuid", uid);

		return map;
	}

	/**
	 * 检查是否存在此邮箱的用户
	 *
	 * @param email 邮箱
	 * @return 是否
	 */
	public boolean checkEmail(String email) {
		return this.userService.existsByEmail(email);
	}
}
