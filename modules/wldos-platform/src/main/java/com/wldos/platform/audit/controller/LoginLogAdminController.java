/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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
import com.wldos.platform.audit.service.LoginLogService;
import com.wldos.platform.audit.vo.LoginLogItem;

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
 * 登录日志管理端 controller。
 *
 * <p>路径规划（位于"系统设置 / 安全审计"菜单下）：
 * <pre>
 *   GET    /admin/sys/log/login           分页列表（按 occur_at 倒序）
 *   GET    /admin/sys/log/login/{id}      详情
 * </pre>
 *
 * <p>不开放：
 * <ul>
 *   <li>写入接口（事件流不允许业务侧手填）；</li>
 *   <li>删除/编辑（合规要求一次写入不可改；归档清理走 schedule，非 controller 暴露）；</li>
 *   <li>CSV 导出（P0 先打通查询；后续阶段补，统一给操作日志/系统日志一起做）。</li>
 * </ul>
 *
 * <p>实现注意：<b>必须</b>继承 {@link NonEntityController} 而不是 {@code EntityController} —— 后者会
 * 自动暴露 {@code POST /add}、{@code POST /update}、{@code DELETE /delete} 等通用 CRUD 端点，
 * 与"一次写入不可改"的合规要求冲突。
 *
 * <p>权限：归属"系统管理员/安全审计"角色，与 {@code /admin/sys/*} 通行的鉴权策略一致。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Api(tags = "登录日志（后台）")
@RestController
@RequestMapping("/admin/sys/log/login")
public class LoginLogAdminController extends NonEntityController<LoginLogService> {

	@ApiOperation(value = "登录日志分页", notes = "按 occur_at 倒序；支持 user_id / login_account / event_type / result / ip 过滤")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
			@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "20"),
			@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式（默认 occurAt desc）", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "loginAccount", value = "登录账号关键字", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = Long.class, paramType = "query"),
			@ApiImplicitParam(name = "eventType", value = "事件类型（ACCOUNT/MOBILE/OAUTH/...）", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "result", value = "结果（SUCCESS/FAIL/...）", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "ip", value = "客户端 IP", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageData<LoginLogItem> list(@RequestParam Map<String, Object> params) {
		PageQuery pageQuery = new PageQuery(params);
		// 登录事件发生在鉴权前，部分请求可能拿不到业务域 header，domain_id 会落为默认域。
		// 这里避免对超级管理员强制域过滤，否则会出现“登录成功但列表查不到”的误判。
		// 非超级管理员仍按当前域隔离。
		// 权限分层：
		// - 超级管理员：可查看全量操作日志（不做租户/域隔离）
		// - 业务管理员（含租户管理员/域管理员）：按租户+域双隔离
		if (!this.isAdmin(this.getUserId())) {
			this.applyTenantFilter(pageQuery);
			this.applyDomainFilter(pageQuery);
		}
		return this.service.queryList(pageQuery);
	}

	@ApiOperation(value = "登录日志详情", notes = "按 ID 返回单条")
	@GetMapping("/{id}")
	public Result<LoginLogItem> detail(@PathVariable("id") Long id) {
		return Result.ok(this.service.detail(id));
	}
}
