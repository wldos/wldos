/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.system.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统配置选项。
 *
 * @author 元悉宇宙
 * @date 2021/7/13
 * @version 1.0
 */
@Table
@Getter
@Setter
public class WoOptions {
	@Id
	private Long id;

	private String optionName;

	private String optionKey;

	private String optionValue;

	private String description;

	private String optionType;

	private String appCode;

	public WoOptions() {}

	private WoOptions(Long id, String optionKey, String optionValue) {
		this.id = id;
		this.optionKey = optionKey;
		this.optionValue = optionValue;
	}

	public static WoOptions of(Long id, String key, String value) {
		return new WoOptions(id, key, value);
	}

	public static WoOptions of(Long id, String optionKey, String optionValue, String optionType, String appCode) {
		return new WoOptions(id, optionKey, optionValue, optionType, appCode);
	}

	private WoOptions(Long id, String optionKey, String optionValue, String optionType, String appCode) {
		this.id = id;
		this.optionKey = optionKey;
		this.optionValue = optionValue;
		this.optionType = optionType;
		this.appCode = appCode;
	}
}
