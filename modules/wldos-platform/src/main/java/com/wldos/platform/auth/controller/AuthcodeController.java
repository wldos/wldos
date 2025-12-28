/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.controller;

import java.util.HashMap;
import java.util.Map;

import com.wldos.platform.auth.service.AuthCodeService;
import com.wldos.platform.auth.vo.CaptchaVO;
import com.wldos.platform.auth.vo.PasswdResetParams;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.common.res.Result;
import com.wldos.common.res.ResultCode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 验证码相关控制器。
 *
 * @author 元悉宇宙
 * @date 2021/4/29
 * @version 1.0
 */
@Api(tags = "验证码管理", description = "验证码生成、校验相关API")
@RestController
@RequestMapping("authcode")
public class AuthcodeController extends NonEntityController<AuthCodeService> {

	@ApiOperation(value = "获取图形验证码", notes = "生成图形验证码，返回验证码图片base64和UUID")
	@GetMapping("code")
	public Result authCode() {
		Map<String, String> map = this.service.genCode();
		return Result.ok(map);
	}

	@ApiOperation(value = "获取手机验证码", notes = "向指定手机号发送验证码短信")
	@GetMapping("code4mobile")
	public Result authCodeMobile(@ApiParam(value = "手机号", required = true) @RequestParam Map<String, String> mobile) {
		Map<String, String> map = this.service.genCodeMobile(mobile.get("mobile"));
		return Result.ok(map);
	}

	@ApiOperation(value = "获取邮箱验证码", notes = "向指定邮箱发送验证码邮件")
	@GetMapping("code4email")
	public Result authCodeEmail(@ApiParam(value = "邮箱地址", required = true) @RequestParam Map<String, String> email) {
		Map<String, String> map = this.service.genCodeEmail(email.get("email"));
		return Result.ok(map);
	}

	@ApiOperation(value = "校验图形验证码", notes = "校验用户输入的图形验证码是否正确")
	@PostMapping("check")
	public Result checkCode(@ApiParam(value = "验证码参数", required = true) @Valid @RequestBody CaptchaVO captchaVO) {
		Map<String, String> res = new HashMap<>();
		String status = "status";
		if (!this.service.checkCode(captchaVO)) {
			res.put(status, "error");
			return Result.error(ResultCode.VALIDATION_ERROR, "验证码错误或过期");
		}
		else {
			res.put(status, "ok");
			return Result.ok(res);
		}
	}

	@ApiOperation(value = "校验邮箱是否存在", notes = "校验指定邮箱是否已注册")
	@PostMapping("checkEmail")
	public Result checkEmail(@ApiParam(value = "邮箱参数", required = true) @Valid @RequestBody PasswdResetParams email) {
		Map<String, String> res = new HashMap<>();
		String status = "status";
		boolean isExists = this.service.checkEmail(email.getEmail());

		if (!isExists) {
			getLog().warn("不存在的邮箱校验尝试：{}", email);
			res.put(status, "error");
			return Result.error(ResultCode.NOT_FOUND, "邮箱不存在");
		}
		else {
			res.put(status, "ok");
			return Result.ok(res);
		}
	}
}
