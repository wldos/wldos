/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.controller;

import java.util.HashMap;
import java.util.Map;

import com.wldos.auth.service.AuthCodeService;
import com.wldos.auth.vo.PasswdResetParams;
import com.wldos.base.NoRepoController;
import com.wldos.common.res.Result;
import com.wldos.auth.vo.CaptchaVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public class AuthcodeController extends NoRepoController<AuthCodeService> {

	@GetMapping("code")
	public Result authCode() {
		Map<String, String> map = this.service.genCode();

		return resJson.format(map);
	}

	@GetMapping("code4mobile")
	public Result authCodeMobile(@RequestParam Map<String, String> mobile) {
		Map<String, String> map = this.service.genCodeMobile(mobile.get("mobile"));

		return resJson.format(map);
	}

	@GetMapping("code4email")
	public Result authCodeEmail(@RequestParam Map<String, String> email) {

		Map<String, String> map = this.service.genCodeEmail(email.get("email"));

		return resJson.format(map);
	}

	@PostMapping("check")
	public Result checkCode(@RequestBody CaptchaVO captchaVO) {
		Map<String, String> res = new HashMap<>();
		String status = "status";
		if (!this.service.checkCode(captchaVO)) {
			res.put(status, "error");
		}
		else
			res.put(status, "ok");

		return resJson.format(res);
	}

	@PostMapping("checkEmail")
	public Result checkEmail(@RequestBody PasswdResetParams email) {
		Map<String, String> res = new HashMap<>();
		String status = "status";
		boolean isExists = this.service.checkEmail(email.getEmail());

		if (!isExists) {
			getLog().info("不存在的邮箱校验尝试：{}", email);
			res.put(status, "error");
		}
		else
			res.put(status, "ok");

		return resJson.format(res);
	}
}
