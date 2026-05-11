/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.platform.config.PropertiesReader;
import io.github.wldos.common.res.Result;
import io.github.wldos.framework.support.audit.ISystemLogger;
import io.github.wldos.framework.support.audit.SystemEvent;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.core.service.OptionsNoRepoService;
import io.github.wldos.platform.support.system.entity.WoOptions;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 系统配置项controller。
 *
 * @author Yuanxi Universe
 * @date 2021/7/14
 * @version 1.0
 */
@Api(tags = "系统配置项")
@Slf4j
@RestController
@RequestMapping("system/options")
public class OptionsController extends NonEntityController<OptionsNoRepoService> {

	/** 单条系统事件里 keys 列表的最大展开数；超过则在 metadata 里截断。 */
	private static final int MAX_KEY_NAMES_IN_META = 50;

	private final PropertiesReader propertiesReader;

	/**
	 * 系统日志（运维事件）写入器；可空（裁剪部署不开 audit 内核也能跑）。
	 *
	 * <p>注意：本类的 {@code @OpLog} 已经把"谁改的、改了什么 key"完整落到 {@code wo_op_log}（值会按
	 * {@code sensitiveFields} 自动脱敏）。补 systemLogger 的目的不是重复记录，而是让运维在
	 * 系统日志页用 {@code source_module=config event_type=OPTIONS_BATCH_UPDATE} 一眼看到"系统的什么
	 * 配置在被频繁修改"，无需在 wo_op_log 海量记录中翻找。两条日志职责互补。
	 */
	@Autowired(required = false)
	private ISystemLogger systemLogger;

	public OptionsController(PropertiesReader propertiesReader) {
		this.propertiesReader = propertiesReader;
	}

	@ApiOperation(value = "自动加载配置", notes = "获取所有自动加载的系统配置，返回配置项的键值对")
	@ApiResponses({
		@ApiResponse(code = 200, message = "成功", response = Map.class, responseContainer = "Map")
	})
	@GetMapping("")
	public Map<String, String> fetchAutoReloadOptions() {
		List<WoOptions> allSysOptions = this.service.getSystemOptions();
		return allSysOptions.stream().collect(Collectors.toMap(WoOptions::getOptionKey, WoOptions::getOptionValue, (k1, k2) -> k1));
	}

	@ApiOperation(value = "配置系统选项", notes = "配置系统选项，参数为键值对形式，如：{\"key1\":\"value1\",\"key2\":\"value2\"}")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "配置项键值对", value = "系统配置项，格式为键值对，键为配置项key，值为配置项value", dataTypeClass = Map.class, paramType = "body", required = true, example = "{\"wldos_system_name\":\"WLDOS系统\",\"wldos_system_version\":\"2.0.0\"}")
	})
	@ApiResponses({
		@ApiResponse(code = 200, message = "配置成功", response = Result.class)
	})
	@PostMapping("config")
	@OpLog(action = "配置系统选项", resourceType = "option",
			sensitiveFields = {"password", "passwd", "token", "secret", "captcha", "appSecret", "clientSecret",
					"smtp.password", "redis.password", "db.password"})
	public Result configSysOptions(@RequestBody Map<String, Object> config) {
		Map<String, String> propertyMap = this.service.configSysOptions(config);

		this.propertiesReader.dynSetPropsSrc(propertyMap);

		recordBatchUpdateEvent(config);

		return Result.ok("ok");
	}

	/**
	 * 把"批量配置变更"聚合成一条系统事件——前端常一次提交几十个 key，绝不能每个 key 一条事件灌爆 wo_sys_log。
	 *
	 * <p>实施细节：
	 * <ul>
	 *   <li>{@code keys} 仅展开前 {@value #MAX_KEY_NAMES_IN_META} 个，剩余以 {@code "... +N more"} 收尾；</li>
	 *   <li>**只记 key 名不记 value**——敏感配置（password / token / secret / 私钥）值不应进 wo_sys_log；
	 *       而值的脱敏副本由 {@code @OpLog.sensitiveFields} 在操作日志侧完成（双链路职责互补）；</li>
	 *   <li>失败路径不写 INFO 事件——@OpLog AOP 已经会把异常通过 {@code result_code != OK} 自动记到 wo_op_log。</li>
	 * </ul>
	 */
	private void recordBatchUpdateEvent(Map<String, Object> config) {
		if (this.systemLogger == null || config == null || config.isEmpty()) {
			return;
		}
		try {
			List<String> sortedKeys = new ArrayList<>(config.keySet());
			Collections.sort(sortedKeys);
			int total = sortedKeys.size();
			int show = Math.min(total, MAX_KEY_NAMES_IN_META);

			StringBuilder keysJson = new StringBuilder(64 + show * 32);
			keysJson.append('[');
			for (int i = 0; i < show; i++) {
				if (i > 0) keysJson.append(',');
				keysJson.append('"').append(safeJson(sortedKeys.get(i))).append('"');
			}
			keysJson.append(']');

			String suffix = total > show ? (" ... +" + (total - show) + " more") : "";
			String message = "批量更新系统配置: 共 " + total + " 项" + suffix;

			this.systemLogger.recordAsync(SystemEvent
					.info("config", "OPTIONS_BATCH_UPDATE", message)
					.withMetadata("{\"count\":" + total + ",\"keys\":" + keysJson + "}")
					.withOperator(this.getUserId())
					.withTenant(this.getDomainId(), this.getTenantId()));
		}
		catch (Exception ignored) {
			// 审计旁路必须吞异常，绝不让自己反过来影响主请求链路
		}
	}

	private static String safeJson(String s) {
		if (s == null) return "";
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	/**
	 * 刷新所有自动加载配置
	 */
	@ApiOperation(value = "刷新配置", notes = "刷新所有自动加载配置")
	@ApiResponses({
		@ApiResponse(code = 200, message = "刷新成功", response = Result.class)
	})
	@GetMapping("refresh")
	public Result refresh() {
		this.propertiesReader.reLoadDBPropsSrc();

		return Result.ok("ok");
	}
}