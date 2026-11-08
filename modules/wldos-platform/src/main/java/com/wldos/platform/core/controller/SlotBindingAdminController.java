/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;

import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.core.service.SlotBindingService;
import com.wldos.platform.core.vo.SlotBindingBody;
import com.wldos.platform.core.vo.SlotBindingVO;
import com.wldos.platform.core.vo.SlotCatalogVO;

import io.github.wldos.common.res.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 插槽绑定管理：超管管全平台范围，租户管理员管本租户（服务端鉴权收敛）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Api(tags = "插槽绑定")
@RestController
@RequestMapping("admin/sys/slot")
public class SlotBindingAdminController extends NonEntityController<SlotBindingService> {

	@ApiOperation("插槽注册表（含当前租户/站点下是否已绑定）")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "comId", value = "租户id（超管可指定，缺省平台；租户管理员固定本租户）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "站点id（选定后按该站点视角标注绑定状态：站点级+租户级）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("catalog")
	public Result<List<SlotCatalogVO>> catalog(@RequestParam(value = "comId", required = false) Long comId,
			@RequestParam(value = "domainId", required = false) Long domainId) {
		return Result.ok(this.service.listCatalog(comId, domainId));
	}

	@ApiOperation("当前租户/站点下的插槽绑定清单")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "comId", value = "租户id（超管可指定，缺省平台；租户管理员固定本租户）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "站点id（选定后仅看该站点生效的绑定：站点级+租户级）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("bindings")
	public Result<List<SlotBindingVO>> bindings(@RequestParam(value = "comId", required = false) Long comId,
			@RequestParam(value = "domainId", required = false) Long domainId) {
		return Result.ok(this.service.listBindings(comId, domainId));
	}

	@ApiOperation("绑定插槽到目标分群/角色（同租户/站点同插槽重复绑定则改绑）")
	@PostMapping("bind")
	public Result<String> bind(@ApiParam("绑定参数") @RequestBody SlotBindingBody body) {
		this.service.bind(body);
		return Result.ok("绑定成功");
	}

	@ApiOperation("解绑插槽（恢复默认实现）")
	@PostMapping("unbind")
	public Result<String> unbind(@ApiParam("解绑参数") @RequestBody SlotBindingBody body) {
		this.service.unbind(body != null ? body.getId() : null);
		return Result.ok("已解绑");
	}
}
