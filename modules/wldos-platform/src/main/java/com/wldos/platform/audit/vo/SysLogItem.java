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
 * @date 2026/05/04
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

	private Long operatorUserId;

	private Long domainId;

	private Long comId;

	private Timestamp occurAt;

	private Timestamp createdAt;
}
