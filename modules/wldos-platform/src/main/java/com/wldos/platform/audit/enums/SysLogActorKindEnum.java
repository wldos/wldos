/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.enums;

import io.github.wldos.common.enums.BaseEnum;

/**
 * 网关 edge 拒绝类事件 metadata.actorKind 取值；非全站系统日志通用维度。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public enum SysLogActorKindEnum implements BaseEnum {

	LEGITIMATE_CRAWLER("LEGITIMATE_CRAWLER", "正常爬虫"),
	SCAN_BOT("SCAN_BOT", "扫描机器人"),
	SUSPICIOUS_BROWSER("SUSPICIOUS_BROWSER", "疑似人为扫站"),
	AUTH_ABUSE("AUTH_ABUSE", "认证滥用"),
	AUTH_FLOOD("AUTH_FLOOD", "真实接口高频试探"),
	AUTH_SESSION("AUTH_SESSION", "登录态失效"),
	UNKNOWN("UNKNOWN", "未分类");

	private final String value;
	private final String label;

	SysLogActorKindEnum(String value, String label) {
		this.value = value;
		this.label = label;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
