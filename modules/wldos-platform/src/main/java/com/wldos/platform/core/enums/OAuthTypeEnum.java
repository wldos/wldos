/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

/**
 * @author 元悉宇宙
 * @date 2022/10/14
 * @version 1.0
 */
public enum OAuthTypeEnum {

	WeChat("微信", "wechat"),
	QQ("QQ", "qq"),
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