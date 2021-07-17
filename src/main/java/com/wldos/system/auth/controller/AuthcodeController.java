/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 *Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.wldos.support.controller.NoRepoController;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.UUIDUtils;
import com.wldos.support.util.captcha.VerifyCode;
import com.wldos.system.auth.vo.CaptchaVO;
import com.wldos.system.enums.RedisKeyEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码相关控制器。
 *
 * @author 树悉猿
 * @date 2021/4/29
 * @version 1.0
 */
@RestController
@RequestMapping("authcode")
public class AuthcodeController extends NoRepoController {

	@GetMapping("code")
	public String authCode() {
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

		return resJson.ok(map);
	}

	@PostMapping("check")
	public String checkCode(@RequestBody CaptchaVO captchaVO) {
		Map<String, String> res = new HashMap<>();
		String status = "status";
		String error = "error";
		if (captchaVO == null) {
			res.put(status, error);
			return resJson.ok(res);
		}
		String authCode = null;
		// 获取缓存中的验证码
		authCode = ObjectUtil.string(this.cache.get(String.format(RedisKeyEnum.CAPTCHA.toString(), captchaVO.getUuid())));

		if (authCode == null) {
			res.put(status, error);
			return resJson.ok(res);
		}
		// 判断验证码
		if (!authCode.toLowerCase().trim().equals(ObjectUtil.string(captchaVO.getCaptcha()).toLowerCase())) {
			res.put(status, error);
			return resJson.ok(res);
		}

		res.put(status, "ok");
		return resJson.ok(res);
	}
}
