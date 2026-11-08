/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.github.wldos.common.enums.BaseEnum;

/**
 * k_pubs.visibility_scope 可发现性取值。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@JsonFormat(shape = Shape.OBJECT)
public enum PubVisibilityScopeEnum implements BaseEnum {

	PUBLIC_LISTED("前台可发现", "PUBLIC_LISTED"),

	UNLISTED("仅链接访问（不参与列表/搜索）", "UNLISTED"),

	INTERNAL_ONLY("仅内部可见", "INTERNAL_ONLY");

	private final String label;

	private final String value;

	PubVisibilityScopeEnum(String label, String value) {
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
