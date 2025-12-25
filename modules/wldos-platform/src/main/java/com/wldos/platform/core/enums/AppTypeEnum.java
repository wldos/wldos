/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

import com.wldos.common.Constants;

/**
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public enum AppTypeEnum { // 重命名语义：管理类型，作为应用可见范围/管理边界（AppScope）

	PLATFORM("系统", Constants.ENUM_TYPE_APP_PLAT), // 系统应用 是平台官方实现应用
	APP("公共", "app"), // 公共应用（APP）是面向所有用户开放的通用应用，可以在应用市场购买
	PRIVATE("私有", "private"); // 私有应用 是租户或组织内私有的应用，组织外不可见

	private final String label;

	private final String value;

	AppTypeEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{label: '" + this.label + "', value: '" + this.value + "'}";
	}
}