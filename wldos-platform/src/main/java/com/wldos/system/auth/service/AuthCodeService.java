/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.system.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.wldos.support.Base;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.UUIDUtils;
import com.wldos.support.util.captcha.VerifyCode;
import com.wldos.system.auth.vo.CaptchaVO;
import com.wldos.system.enums.RedisKeyEnum;

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

	public boolean checkCode(CaptchaVO captchaVO) {
		if (captchaVO == null)
			return false;

		String authCode = this.getAuthCodeFromCache(captchaVO.getUuid());

		if (authCode == null) {
			return false;
		}

		return authCode.toLowerCase().trim().equals(ObjectUtil.string(captchaVO.getCaptcha()).toLowerCase());
	}

	public boolean checkCode(String captcha, String uuid) {

		String authCode = this.getAuthCodeFromCache(uuid);
		if (authCode == null) {
			return false;
		}

		return authCode.toLowerCase().trim().equals(ObjectUtil.string(captcha).toLowerCase());
	}

	boolean checkCode(String verifyCode) {
		String captcha = verifyCode.substring(0, 4);
		String uuid = verifyCode.substring(4);
		return this.checkCode(captcha, uuid);
	}

	private String getAuthCodeFromCache(String uuid) {
		return ObjectUtil.string(this.cache.get(String.format(RedisKeyEnum.CAPTCHA.toString(), uuid)));
	}

	public Map<String, String> genCode() {

		int width = 120;
		int height = 40;
		int colorBit = 1;

		VerifyCode verifyCode = VerifyCode.genCaptcha(width, height, colorBit);
		String uid = UUIDUtils.generateShortUuid();
		String text = verifyCode.getText();

		this.cache.set(String.format(RedisKeyEnum.CAPTCHA.toString(), uid), text, 2, TimeUnit.MINUTES);

		String cap = verifyCode.outBase64();
		Map<String, String> map = new HashMap<>();
		map.put("authcode", cap);
		map.put("uuid", uid);

		return map;
	}
}
