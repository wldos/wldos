/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.wldos.support.util.UUIDUtils;

@SuppressWarnings("unused")
public class JWT implements Serializable, IJwt {
	private String loginName;

	private Long userId;

	private Long tenantId;

	private String accountName;

	private String id;

	private boolean isExpired;

	public JWT() {}

	public JWT(Long userId, Long tenantId, String loginName, String accountName) {
		this.userId = userId;
		this.tenantId = tenantId;
		this.loginName = loginName;
		this.accountName = accountName;
		this.id = UUIDUtils.generateShortUuid();
		this.isExpired = false;
	}

	public JWT(Long userId, Long tenantId, String loginName, String accountName, String tokenId, boolean isExpired) {
		this.userId = userId;
		this.tenantId = tenantId;
		this.loginName = loginName;
		this.accountName = accountName;
		this.id = tokenId;
		this.isExpired = isExpired;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Override
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Override
	public String getId() {
		return id;
	}


	public void setId(String tokenId) {
		this.id = tokenId;
	}

	@Override
	public boolean getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		JWT jwtInfo = (JWT) o;

		if (!Objects.equals(loginName, jwtInfo.loginName)) {
			return false;
		}
		return Objects.equals(userId, jwtInfo.userId);

	}

	@Override
	public int hashCode() {
		int result = loginName != null ? loginName.hashCode() : 0;
		result = 31 * result + (userId != null ? userId.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return this.loginName + "," + this.accountName + "," + this.userId +
				"," + this.id + "," + (this.isExpired?"已过期":"有效");
	}
}
