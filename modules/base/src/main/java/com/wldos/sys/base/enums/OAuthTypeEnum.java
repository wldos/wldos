/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

import java.util.Arrays;

/**
 * OAuth服务商类型枚举值。
 *
 * @author 树悉猿
 * @date 2022/10/14
 * @version 1.0
 */
public enum OAuthTypeEnum {
	/** 微信 */
	WeChat("微信", "wechat"),
	/** qq */
	QQ("QQ", "qq"),
	/** 微博 */
	WeiBo("微博", "weibo");

	private final String label;

	private final String value;

	OAuthTypeEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	/**
	 * 从枚举值匹配项检查是否存在该值
	 *
	 * @param key 待匹配的key值
	 * @return 是否存在
	 */
	public static boolean match(String key) {
		for (OAuthTypeEnum o : OAuthTypeEnum.values()) {
			if (o.getValue().equals(key))
				return true;
		}

		return false;
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
