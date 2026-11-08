/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.enums;

import io.github.wldos.common.enums.BaseEnum;

/**
 * 入驻场景编码回退枚举（Term {@code provision_scenario} 未 seed 时供字典与校验使用）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public enum ProvisionScenarioCodeEnum implements BaseEnum {

	STANDARD("标准 SaaS", "standard"),
	CMS_PORTAL("内容门户", "cms-portal"),
	FULL_MIRROR("完整镜像", "full-mirror"),
	TRIAL_PLATFORM("线上体验", "trial_platform");

	private final String label;

	private final String value;

	ProvisionScenarioCodeEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public String getValue() {
		return this.value;
	}
}
