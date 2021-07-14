/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.wldos.support.controller.NoRepoController;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.UUIDUtils;
import com.wldos.support.util.captcha.VerifyCode;
import com.wldos.system.auth.param.Captcha;
import com.wldos.system.sysenum.RedisKeyEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码相关控制器。
 *
 * @Title CaptchaController
 * @Package com.wldos.system.auth.controller
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/29
 * @Version 1.0
 */
@RestController
@RequestMapping("authcode")
public class AuthcodeController extends NoRepoController {

	@GetMapping("code")
	public String authCode() {
		// 验证码宽、高、色位: 120 40 1
		int width = 120, height = 40, colorBit = 1;
		VerifyCode verifyCode = VerifyCode.genCaptcha(width, height, colorBit);
		String uid = UUIDUtils.generateShortUuid();
		String text = verifyCode.getText();
		// 验证码有效期120秒
		this.cache.set(String.format(RedisKeyEnum.captcha.toString(), uid), text, 2, TimeUnit.MINUTES);
		log.info("验证码文本=" + text + " uid= " + uid);
		String cap = verifyCode.outBase64();
		Map<String, String> map = new HashMap<String, String>();
		map.put("authcode", cap);
		map.put("uuid", uid);

		return resJson.ok(map);
	}

	@PostMapping("check")
	public String checkCode(@RequestBody Captcha captcha) {
		Map<String, String> res = new HashMap<>();
		if (captcha == null) {
			res.put("status", "error");
			return resJson.ok(res);
		}
		String authCode = null;
		// 获取缓存中的验证码
		authCode = ObjectUtil.string(this.cache.get(String.format(RedisKeyEnum.captcha.toString(), captcha.getUuid())));
		log.debug("Redis/Cache取验证码: " + authCode + " 用户输入：" + ObjectUtil.string(captcha.getCaptcha()));

		if (authCode == null) {
			res.put("status", "error");
			return resJson.ok(res);
		}
		// 判断验证码
		if (!authCode.toLowerCase().trim().equals(ObjectUtil.string(captcha.getCaptcha()).toLowerCase())) {
			res.put("status", "error");
			return resJson.ok(res);
		}

		res.put("status", "ok");
		return resJson.ok(res);
	}
}
