/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;
import java.util.Map;

import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.service.OptionsService;
import io.github.wldos.framework.support.audit.ISystemLogger;
import io.github.wldos.framework.support.audit.SystemEvent;
import io.github.wldos.platform.support.system.entity.WoOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 系统配置相关controller。
 *
 * <p>系统配置变更属于"运维事件"语义——除了由基类 {@code @OpLog} 自动落操作日志（"谁改的"），
 * 还要再落一条 {@link SystemEvent}（"系统的什么配置发生了变化"），便于运维筛 wo_sys_log
 * 一处看到所有配置漂移历史，无需在 wo_op_log 海量记录中翻找。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "系统配置管理（后台）")
@RestController
@RequestMapping("admin/sys/options")
public class OptionsAdminController extends EntityController<OptionsService, WoOptions> {

	@Autowired(required = false)
	private ISystemLogger systemLogger;

	@ApiOperation(value = "所有系统配置", notes = "获取所有系统配置")
	@GetMapping("")
	public List<WoOptions> fetchAllOptions() {
		return this.service.findAll();
	}

	/**
	 * 覆写仅为补系统日志：基类 {@code @OpLog} 已经自动记录了操作日志（包含改动人/IP/请求体），
	 * 这里再写一条 {@link SystemEvent} 让运维能在系统日志页一眼看到"哪个配置项被新增"。
	 */
	@Override
	@PostMapping("add")
	public String addEntity(@RequestBody WoOptions entity) {
		String result = super.addEntity(entity);
		recordConfigEvent("OPTIONS_ADD", entity, null);
		return result;
	}

	@Override
	@PostMapping("update")
	public String updateEntity(@RequestBody WoOptions entity) {
		// 取旧值快照（snapshot）：写系统日志时把"改前/改后"都带上。
		// 一次额外 PK 查询，无 IO 风险——且本接口频率极低（管理员手动操作）。
		WoOptions before = entity.getId() == null ? null : this.service.findById(entity.getId());
		String result = super.updateEntity(entity);
		recordConfigEvent("OPTIONS_UPDATE", entity, before);
		return result;
	}

	@Override
	public String removeEntitiesByIds(@RequestBody Map<String, Object> jsonObject) {
		String result = super.removeEntitiesByIds(jsonObject);
		if (systemLogger == null) {
			return result;
		}
		Object ids = jsonObject == null ? null : jsonObject.get("ids");
		systemLogger.recordAsync(SystemEvent
				.warn("config", "OPTIONS_DELETE_BATCH", "批量删除系统配置 ids=" + ids)
				.withMetadata(toJsonMetadata("ids", ids))
				.withOperator(this.getUserId())
				.withTenant(this.getDomainId(), this.getTenantId()));
		return result;
	}

	/**
	 * 写一条配置变更的系统事件；{@code before} 为 null 表示"新增"场景，否则把改前/改后值都带上。
	 *
	 * <p>对密码/secret 等敏感配置项的脱敏由 {@code @OpLog.sensitiveFields} 在操作日志侧统一处理；
	 * 系统日志这里只反映"哪个 key 被改了"，{@code optionValue} 即使敏感，也只在 metadata 里出现，
	 * 不会进 wo_op_log 的 paramsSummary（双链路职责互补）。
	 */
	private void recordConfigEvent(String eventType, WoOptions after, WoOptions before) {
		if (systemLogger == null || after == null) {
			return;
		}
		String key = after.getOptionKey();
		String name = after.getOptionName();
		StringBuilder meta = new StringBuilder(128);
		meta.append("{\"key\":\"").append(safeJson(key)).append("\"")
				.append(",\"name\":\"").append(safeJson(name)).append("\"");
		if (before != null) {
			meta.append(",\"oldValue\":\"").append(safeJson(before.getOptionValue())).append("\"");
		}
		meta.append(",\"newValue\":\"").append(safeJson(after.getOptionValue())).append("\"}");

		systemLogger.recordAsync(SystemEvent
				.info("config", eventType, "系统配置项[" + (name == null ? key : name) + "]" + (before == null ? "新增" : "更新"))
				.withMetadata(meta.toString())
				.withOperator(this.getUserId())
				.withTenant(this.getDomainId(), this.getTenantId()));
	}

	private static String toJsonMetadata(String k, Object v) {
		return "{\"" + k + "\":\"" + safeJson(v == null ? "" : v.toString()) + "\"}";
	}

	private static String safeJson(String s) {
		if (s == null) return "";
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

}
