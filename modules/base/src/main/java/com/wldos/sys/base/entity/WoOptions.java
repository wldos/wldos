/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 系统配置选项。
 *
 * @author 树悉猿
 * @date 2021/7/13
 * @version 1.0
 */
@Table
public class WoOptions {
	@Id
	private Long id;

	private String optionName;

	private String optionKey;

	private String optionValue;

	private String description;

	private String appType;

	public WoOptions() {

	}

	private WoOptions(Long id, String optionKey, String optionValue) {
		this.id = id;
		this.optionKey = optionKey;
		this.optionValue = optionValue;
	}

	public static WoOptions of(Long id, String key, String value) {
		return new WoOptions(id, key, value);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public String getOptionKey() {
		return optionKey;
	}

	public void setOptionKey(String optionKey) {
		this.optionKey = optionKey;
	}

	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}
}
