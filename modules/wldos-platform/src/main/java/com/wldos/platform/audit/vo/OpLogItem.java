/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.vo;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * 操作日志列表 VO（前端展示）。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Getter
@Setter
public class OpLogItem {

	private String id;

	private Long userId;

	private Long virtualUserId;

	private String userName;

	private Long domainId;

	private Long comId;

	private String module;

	private String action;

	private String resourceType;

	/**
	 * 资源类型中文展示名（如 {@code "domain" → "域名"}、{@code "user_profile" → "个人资料"}）。
	 *
	 * <p>不入库,由 {@code OpLogService} 启动时聚合各模块 {@code IOpLogContextResolver#resourceTypeLabels()}
	 * 字典缓存为总表,查询返回时按 {@link #resourceType} 一对一回填;字典里没登记的 type 留 {@code null},
	 * 前端按"无 label 时退化展示原 type"处理,不会报错。
	 */
	private String resourceTypeLabel;

	private String resourceId;

	/**
	 * 资源名（查询时由 {@code OpLogService} 通过 {@code IOpLogContextResolver} SPI 反查回填）。
	 *
	 * <p>不入库，不占 {@code wo_op_log} 列：审计表只持久化 {@code resourceType + resourceId}
	 * 这两个稳定主键；展示侧的人话名称由各业务模块自行解析，业务命名变更不污染审计原始数据。
	 *
	 * <p>解析失败/资源已被删除/无对应解析器时为 {@code null}，前端展示退化为只显示 {@code resourceId}。
	 */
	private String resourceName;

	private String requestPath;

	private String httpMethod;

	private String paramsSummary;

	private String resultCode;

	private String errorMessage;

	private Integer durationMs;

	private String ip;

	private String userAgent;

	private Timestamp occurAt;

	private Timestamp createdAt;
}
