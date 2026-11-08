/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.vo;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统日志列表 VO（前端展示）。
 *
 * @author Yuanxi Universe
 * @date 2025/05/04
 * @version 1.0
 */
@Getter
@Setter
public class SysLogItem {

	private String id;

	private String sourceModule;

	private String eventType;

	private String severity;

	private String message;

	private String metadata;

	/** 从 metadata JSON 解析的客户端 IP，列表展示用。 */
	private String ip;

	/** 从 metadata 解析的请求路径。 */
	private String requestUri;

	/** 协议 + 主机（含非 80/443 端口）+ 路径组成的完整 URL，列表展示用。 */
	private String requestUrl;

	/** 从 metadata 解析的 HTTP 方法。 */
	private String requestMethod;

	/** 请求 Host。 */
	private String requestHost;

	/** User-Agent。 */
	private String userAgent;

	/** Referer。 */
	private String referer;

	/** 来源分类 actorKind。 */
	private String actorKind;

	/** 聚合次数 metadata.count。 */
	private Integer aggregateCount;

	private Long operatorUserId;

	private Long domainId;

	private Long comId;

	private Timestamp occurAt;

	private Timestamp createdAt;
}
