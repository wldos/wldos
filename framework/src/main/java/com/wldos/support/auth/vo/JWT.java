/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.auth.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.wldos.common.utils.UUIDUtils;

@SuppressWarnings("unused")
public class JWT implements Serializable {

	private final Long userId;

	private final Long tenantId;

	private final Long domainId;

	private final String id;

	private final boolean isExpired;

	private final Date startDate;

	private final Date expireDate;

	public JWT(Long userId, Long tenantId, Long domainId) {
		this.userId = userId;
		this.tenantId = tenantId;
		this.domainId = domainId;
		this.id = UUIDUtils.generateShortUuid();
		this.isExpired = false;
		this.startDate = null;
		this.expireDate = null;
	}

	public JWT(Long userId, Long tenantId, Long domainId, String tokenId, boolean isExpired, Date startDate, Date expireDate) {
		this.userId = userId;
		this.tenantId = tenantId;
		this.domainId = domainId;
		this.id = tokenId;
		this.isExpired = isExpired;
		this.startDate = startDate;
		this.expireDate = expireDate;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public String getId() {
		return id;
	}

	public Long getDomainId() {
		return domainId;
	}

	public boolean getIsExpired() {
		return isExpired;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

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