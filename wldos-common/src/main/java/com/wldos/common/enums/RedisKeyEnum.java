/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.enums;

/**
 * 缓存键值枚举值。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum RedisKeyEnum {
	/** wldos系统：超级管理员 */
	WLDOS_ADMIN("wldos:admin"),
	/** wldos系统：可信者 */
	WLDOS_TRUST("wldos:trust"),
	/** wldos系统：租户管理员 */
	WLDOS_TENANT_ADMIN("wldos:tenant:admin"),
	/**    wldos系统：资源权限树 */
	WLDOS_AUTH("wldos:auth"),
	/** CMS系统：资源权限树 */
	WLDOS_ORG("wldos:org"),
	/** wldos系统：token */
	WLDOS_TOKEN("wldos:token"),
	/** 多域系统 */
	WLDOS_DOMAIN("wldos:domain"),
	/** 多域系统资源 */
	WLDOS_DOMAIN_RES("wldos:dom:res"),
	/** 内容发布类型 */
	WLDOS_PUB_TYPE("wldos:pub:type"),
	/** 分类目录，某个行业门类的具体分类法 */
	WLDOS_TERM("wldos:term"),
	/** 分类目录扁平树状结构 */
	WLDOS_TERM_TREE("wldos:term:tree"),
	/** 分类目录多级树状结构 */
	WLDOS_TERM_TREE_LAYER("wldos:term:layer"),
	/** 标签 */
	WLDOS_TAG("wldos:tag"),
	/** 标签项 */
	WLDOS_TAG_OPT("wldos:tag:option"),
	/** 验证码 */
	CAPTCHA("wldos:captcha:%s"),
	/** 状态码 */
	STATE("wldos:state:%s");

	private final String cacheKey;

	RedisKeyEnum(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String toString() {
		return this.cacheKey;
	}
}