/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 缓存键值枚举值。
 *
 * @Title RedisKeyEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum RedisKeyEnum {
	/**	wldos系统：资源权限树 */
	wldosAuth("wldos:auth"),
	/** 年谱系统：资源权限树 */
	bookAuth("book:auth"),
	/** wldos系统：组织机构树 */
	wldosOrg("wldos:org"),
	/** 年谱分类树 */
	bookType("book:type"),
	/** wldos系统：token */
	wldosToken("wldos:token"),
	/** 验证码 */
	captcha("wldos:captcha:%s");

	private String cacheKey;

	RedisKeyEnum(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String toString() {
		return this.cacheKey;
	}
}
