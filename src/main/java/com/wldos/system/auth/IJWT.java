/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.auth;

import java.util.Date;

public interface IJWT {
	/**
	 * 获取用户ID
	 * @return
	 */
	Long getUserId();

	/**
	 * 获取用户主企业id(租户)
	 *
	 * @return
	 */
	Long getTenantId();

	/**
	 * 获取账号
	 * @return
	 */
	String getLoginName();


	/**
	 * 获取账号名称
	 * @return
	 */
	String getAccountName();

	/**
	 * tokenId
	 * @return
	 */
	String getId();

	/**
	 * 超期时间
	 *
	 * @return
	 */
	boolean getIsExpired();

	String key_jwt_sub = "subject"; // userId

	String key_jwt_id = "id"; // tokenId

	String key_jwt_login = "loginName";

	String key_jwt_name = "accountName";

	String key_jwt_tenant = "tenantId";
}
