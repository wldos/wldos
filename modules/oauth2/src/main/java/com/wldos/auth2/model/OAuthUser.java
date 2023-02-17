/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth2.model;

/**
 * OAuth登录用户信息。
 *
 * @author 树悉猿
 * @date 2022/10/21
 * @version 1.0
 */
public interface OAuthUser {

	String getOpenId();

	String getNickname();

	String getSex();

	String getProvince();

	String getCity();

	default String getCountry() {return null;}

	String getHeadImgUrl();

	default String[] getPrivilege() {
		return null;
	}

	String getUnionId();

	String getOauthType();

	void setOauthType(String authType);
}