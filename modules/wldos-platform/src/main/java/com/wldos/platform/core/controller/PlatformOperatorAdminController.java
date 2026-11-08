/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.controller;

import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.framework.mvc.service.impl.NullService;
import com.wldos.platform.core.service.PlatformOperatorSettingsService;
import com.wldos.platform.core.vo.PlatformOperatorContextVO;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 平台运营 scope 只读接口（M2/M5）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Api(tags = "平台运营")
@RestController
@RequestMapping("admin/sys/platform-operator")
public class PlatformOperatorAdminController extends NonEntityController<NullService> {

	private final PlatformOperatorSettingsService platformOperatorSettingsService;

	public PlatformOperatorAdminController(PlatformOperatorSettingsService platformOperatorSettingsService) {
		this.platformOperatorSettingsService = platformOperatorSettingsService;
	}

	@ApiOperation(value = "平台运营 scope", notes = "返回 operator com 与主站 domain，供「平台运营」菜单固定工具栏 scope")
	@GetMapping("context")
	public Result<PlatformOperatorContextVO> context() {
		if (!this.platformOperatorSettingsService.canParticipateInPlatformOperatorDataPlane(this.getUserId())) {
			throw new BaseException("无权查看平台运营 scope", ResultCode.FORBIDDEN.getCode());
		}
		return Result.ok(this.platformOperatorSettingsService.buildContext());
	}
}
