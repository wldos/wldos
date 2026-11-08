/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.controller;

import java.util.HashMap;
import java.util.Map;

import com.wldos.platform.auth.clickcaptcha.ClickCaptchaLocaleResolver;
import com.wldos.platform.auth.service.AuthCodeService;
import com.wldos.platform.auth.vo.CaptchaVO;
import com.wldos.platform.auth.vo.ClickCaptchaVerifyParams;
import com.wldos.platform.auth.vo.EmailCheckParams;
import com.wldos.platform.auth.vo.ForgetEmailAdmissionParams;
import com.wldos.framework.mvc.controller.NonEntityController;

import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
 * @author Yuanxi Universe
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
	public Result authCodeEmail(

			@ApiParam(value = "邮箱地址", required = true) @RequestParam Map<String, String> email) {

		boolean forgetScene = "forget".equals(ObjectUtils.string(email.get("scene")).trim());

		Long siteDomainId = forgetScene

				? this.resolveForgetSiteDomainId()

				: this.getDomainId();

		if (forgetScene) {

			if (siteDomainId == null) {

				return Result.error(ResultCode.VALIDATION_ERROR, "无法识别当前站点");

			}

			if (!this.service.verifyForgetEmailAdmit(

					email.get("admitToken"),

					email.get("email"),

					siteDomainId,

					this.getTenantId())) {

				return Result.error(ResultCode.VALIDATION_ERROR, "请先完成安全验证并确认邮箱");

			}

		}

		Map<String, String> map = this.service.genCodeEmail(

				email.get("email"), email.get("loginName"), siteDomainId, this.getTenantId());

		return Result.ok(map);

	}



	@ApiOperation(value = "找回密码流程能力", notes = "secondaryVerify=true 仅在有商业时为 true；否则仅邮箱准入+邮箱验证码")
	@GetMapping("forgetResetProfile")
	public Result forgetResetProfile() {

		Map<String, String> res = new HashMap<>();

		res.put("secondaryVerify", Boolean.toString(this.service.isForgetSecondaryVerifyEnabled()));

		return Result.ok(res);

	}


	@ApiOperation(value = "获取点选安全验证", notes = "仅商业二次验证启用时由前端调用；未启用时请求会失败")
	@GetMapping("clickChallenge")
	public Result clickChallenge(

			@ApiParam(value = "界面语言，如 zh-CN、zh-TW、en-US；缺省读 Accept-Language")

			@RequestParam(value = "locale", required = false) String locale,

			@RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

		String poolKey = ClickCaptchaLocaleResolver.resolve(locale, acceptLanguage);

		return Result.ok(this.service.genClickChallenge(poolKey));

	}


	@ApiOperation(value = "提交点选安全验证", notes = "点选正确后返回一次性发信凭证 graphPass")
	@PostMapping("clickVerify")
	public Result clickVerify(

			@ApiParam(value = "点选参数", required = true) @Valid @RequestBody ClickCaptchaVerifyParams params) {

		String graphPass = this.service.verifyClickChallenge(params);

		if (ObjectUtils.isBlank(graphPass)) {

			return Result.error(ResultCode.VALIDATION_ERROR, "验证失败，请重试");

		}

		Map<String, String> res = new HashMap<>();

		res.put("graphPass", graphPass);

		return Result.ok(res);

	}


	@ApiOperation(value = "找回密码邮箱准入", notes = "校验邮箱是否可在当前 Host 站点找回，不发信")
	@PostMapping("forgetEmailAdmission")
	public Result forgetEmailAdmission(

			@ApiParam(value = "准入参数", required = true) @Valid @RequestBody ForgetEmailAdmissionParams params) {

		Long siteDomainId = this.resolveForgetSiteDomainId();

		if (siteDomainId == null) {

			return Result.ok(this.service.admitForgetEmail(

					params.getEmail(), params.getGraphPass(), null, this.getTenantId()));

		}

		Map<String, String> res = this.service.admitForgetEmail(

				params.getEmail(),

				params.getGraphPass(),

				siteDomainId,

				this.getTenantId());

		return Result.ok(res);

	}


	@ApiOperation(value = "校验图形验证码", notes = "校验用户输入的图形验证码是否正确")
	@PostMapping("check")
	public Result checkCode(
			@ApiParam(value = "验证码参数", required = true) @Valid @RequestBody CaptchaVO captchaVO) {

		Map<String, String> res = new HashMap<>();

		String status = "status";

		Long siteDomainId = ObjectUtils.isBlank(captchaVO.getForgetProof())
				? this.getDomainId()
				: this.resolveForgetSiteDomainId();

		if (!this.service.checkCode(captchaVO, siteDomainId, this.getTenantId())) {

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
	public Result checkEmail(

			@ApiParam(value = "邮箱参数", required = true) @Valid @RequestBody EmailCheckParams email) {

		Map<String, String> res = new HashMap<>();

		Long siteDomainId = this.resolveForgetSiteDomainId();

		if (siteDomainId == null) {

			res.put("status", "error");

			res.put("hint", "邮箱不存在");

			return Result.ok(res);

		}

		if (ObjectUtils.isBlank(email.getForgetProof())

				|| !this.service.verifyForgetProofBinding(

						email.getForgetProof(),

						null,

						email.getEmail(),

						siteDomainId,

						this.getTenantId())) {

			res.put("status", "error");

			res.put("hint", "邮箱不存在");

			return Result.ok(res);

		}

		res = this.service.describeEmailForPasswordReset(

				email.getEmail(), siteDomainId, this.getTenantId());

		if (!"ok".equals(res.get("status"))) {

			getLog().warn("不存在的邮箱校验尝试：{}", email.getEmail());

			res.put("hint", "邮箱不存在");

		}

		return Result.ok(res);
	}


	private Long resolveForgetSiteDomainId() {
		Long domainId = this.getDomainId();
		if (domainId == null || domainId <= 0) {
			return null;
		}
		return domainId;
	}
}