/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 平台保留用户身份相关枚举值。身份是社会属性，客观存在、社会公认。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum UserIdentityEnum {

	/** 政府单位 */
	ORG("org"),
	/** 企业 */
	COM("com"),
	/** 社会团体 */
	SOCIAL("social"),
	/** 中介 */
	MIDDLE("middle"),
	// 职业类身份：老师、音乐人、作家、财经专家……
	/** 公务员 */
	SERVANT("servant");

	private final String identityName;

	UserIdentityEnum(String identityName) {
		this.identityName = identityName;
	}

	@Override
	public String toString() {
		return this.identityName;
	}
}
