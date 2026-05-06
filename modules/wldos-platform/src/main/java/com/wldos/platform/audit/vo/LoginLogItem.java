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
 * 登录日志列表 VO（前端展示）。
 *
 * <p>字段集与 {@link com.wldos.platform.audit.entity.WoLoginLog} 完全对齐，
 * {@code id} 转 String 避免前端 {@code Number} 精度丢失（项目惯例）。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Getter
@Setter
public class LoginLogItem {

	/** 雪花 ID，前端用 String 承载避免精度丢失。 */
	private String id;

	private Long userId;

	private String loginAccount;

	private String eventType;

	private String oauthProvider;

	private String result;

	private String failReason;

	private String ip;

	private String userAgent;

	private Long domainId;

	private Long comId;

	private Timestamp occurAt;

	private Timestamp createdAt;
}
