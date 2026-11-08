/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.controller;

import java.util.Map;

import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.Result;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.audit.entity.WoSysLog;
import com.wldos.platform.audit.service.SysLogService;
import com.wldos.platform.audit.vo.SysLogItem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 系统日志管理端 controller。
 *
 * <pre>
 *   GET    /admin/sys/log/sys           分页列表（按 occur_at 倒序）
 *   GET    /admin/sys/log/sys/{id}      详情（含 metadata JSON）
 * </pre>
 *
 * <p>实现注意：与 {@code LoginLogAdminController} 同——继承 {@link NonEntityController} 而不是
 * {@code EntityController}，避免自动暴露 add / update / delete 通用端点，违反"一次写入不可改"的合规要求。
 *
 * @author Yuanxi Universe
 * @date 2025/05/04
 * @version 1.0
 */
@Api(tags = "系统日志（后台）")
@RestController
@RequestMapping("/admin/sys/log/sys")
public class SysLogAdminController extends NonEntityController<SysLogService> {

	@ApiOperation(value = "系统日志分页", notes = "按 occur_at 倒序；支持 sourceModule/eventType/severity/operatorUserId 及 metadata 内 requestUri/requestMethod/ip 过滤")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
			@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "20"),
			@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "sourceModule", value = "事件来源模块", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "eventType", value = "事件类型", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "severity", value = "严重级别（INFO/WARN/ERROR/CRITICAL）", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "operatorUserId", value = "触发者用户ID", dataTypeClass = Long.class, paramType = "query"),
			@ApiImplicitParam(name = "requestUri", value = "请求路径（metadata.uri 子串匹配）", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "requestMethod", value = "HTTP 方法（metadata.method 精确匹配）", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "ip", value = "客户端 IP（metadata.ip 前缀匹配）", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "requestHost", value = "请求 Host（metadata.host 子串）", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "userAgent", value = "User-Agent 子串", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "actorKind", value = "来源分类", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "comId", value = "目标租户 ID（超管 lens）", dataTypeClass = Long.class, paramType = "query"),
			@ApiImplicitParam(name = "domainId", value = "目标站点 ID（按站点收窄）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("")
	public PageData<SysLogItem> listQuery(@RequestParam Map<String, Object> params) {
		Long domainId = parseOptionalLongParam(params.get("domainId"));
		PageQuery pageQuery = new PageQuery(params);
		this.applyAuditLogListScope(pageQuery, WoSysLog.class);
		this.applySysLogTenantAdminUnionScope(pageQuery, domainId);
		return this.service.queryList(pageQuery);
	}

	private static Long parseOptionalLongParam(Object raw) {
		if (raw == null || raw.toString().trim().isEmpty()) {
			return null;
		}
		try {
			return Long.parseLong(raw.toString().trim());
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "系统日志详情", notes = "按 ID 返回单条")
	public Result<SysLogItem> detail(@PathVariable("id") Long id) {
		return Result.ok(this.service.detail(id));
	}
}
