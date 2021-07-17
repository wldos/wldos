/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth;

public interface IJwt {
	/**
	 * 获取用户ID
	 *
	 * @return userId
	 */
	Long getUserId();

	/**
	 * 获取用户主企业id(租户)
	 *
	 * @return tenantId
	 */
	Long getTenantId();

	/**
	 * 获取账号
	 *
	 * @return loginName
	 */
	String getLoginName();


	/**
	 * 获取账号名称
	 *
	 * @return nickName
	 */
	String getAccountName();

	/**
	 * tokenId
	 *
	 * @return tokenId
	 */
	String getId();

	/**
	 * 超期时间
	 *
	 * @return isExpired
	 */
	boolean getIsExpired();

	String KEY_JWT_LOGIN = "loginName";

	String KEY_JWT_NAME = "accountName";

	String KEY_JWT_TENANT = "tenantId";
}
