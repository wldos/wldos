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

	private Long userId;

	private Long tenantId;

	private String id;

	private boolean isExpired;

	private Date startDate;

	private Date expireDate;

	public JWT() {}

	public JWT(Long userId, Long tenantId) {
		this.userId = userId;
		this.tenantId = tenantId;
		this.id = UUIDUtils.generateShortUuid();
		this.isExpired = false;
		this.startDate = null;
		this.expireDate = null;
	}

	public JWT(Long userId, Long tenantId, String tokenId, boolean isExpired, Date startDate, Date expireDate) {
		this.userId = userId;
		this.tenantId = tenantId;
		this.id = tokenId;
		this.isExpired = isExpired;
		this.startDate = startDate;
		this.expireDate = expireDate;
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
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
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

		return Objects.equals(userId, jwtInfo.userId);
	}

	@Override
	public int hashCode() {
		return userId.hashCode();
	}

	@Override
	public String toString() {
		return this.userId + "," + this.id + "," + this.startDate.getTime() +"," + this.expireDate.getTime() +"," + (this.isExpired?"已过期":"有效");
	}
}
