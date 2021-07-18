/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 缓存键值枚举值。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum RedisKeyEnum {
	/**	wldos系统：资源权限树 */
	WLDOS_AUTH("wldos:auth"),
	/** CMS系统：资源权限树 */
	WLDOS_ORG("wldos:org"),
	/** wldos系统：token */
	WLDOS_TOKEN("wldos:token"),
	/** 验证码 */
	CAPTCHA("wldos:captcha:%s");

	private final String cacheKey;

	RedisKeyEnum(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String toString() {
		return this.cacheKey;
	}
}
