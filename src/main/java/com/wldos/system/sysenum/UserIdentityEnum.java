/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 平台保留用户身份相关枚举值。
 *
 * @Title UserIdentityEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum UserIdentityEnum {

	/** 政府单位 */
	org("org"),
	/** 企业 */
	com("com"),
	/** 社会团体 */
	social("social"),
	/** 中介 */
	middle("middle"),
	/** 公务员 */
	servant("servant");

	private String identityName;

	UserIdentityEnum(String identityName) {
		this.identityName = identityName;
	}

	@Override
	public String toString() {
		return this.identityName;
	}
}
