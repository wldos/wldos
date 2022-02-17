/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.wldos.base.Base;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.UUIDUtils;
import com.wldos.common.utils.captcha.VerifyCode;
import com.wldos.auth.vo.CaptchaVO;
import com.wldos.common.enums.RedisKeyEnum;

import org.springframework.stereotype.Service;

/**
 * 验证码服务。
 *
 * @author 树悉猿
 * @date 2021/9/10
 * @version 1.0
 */
@Service
public class AuthCodeService extends Base {

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
}
