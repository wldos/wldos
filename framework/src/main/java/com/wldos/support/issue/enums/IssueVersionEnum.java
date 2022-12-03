/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.enums;

import com.wldos.support.issue.IssueConstants;

/**
 * license版本枚举。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum IssueVersionEnum {
	/** wldos免费版(试用版)：开发测试体验，用户数受限，有广告，无支撑服务，免费更新，不提供底层闭源代码 */
	Free("社区版", IssueConstants.DEFAULT_VERSION),
	/** wldos标准版(小微企业、个人版)：基础站点，单租户，最多10个分站域名，无广告，一年质保，免费更新，不提供底层代码，定价：799/包年，3999（终身正版） */
	Standard("标准版", "Standard"),
	/** wldos旗舰版(云企业版)：SaaS平台，多租户、多域，终身质保，免费更新，提供部分底层代码，定价：8万（终身正版），1500/包年，30万/3年（省级授权再分发），300万/3年（全国授权再分发），1000万/3年（全球授权再分发） */
	Ultimate("旗舰版", "Ultimate");

	private final String label;

	private final String value;

	IssueVersionEnum(String label, String value) {
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
