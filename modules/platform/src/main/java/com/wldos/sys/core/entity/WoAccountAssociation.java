/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 账号关联意味着被绑定的第三方账号可以以登录名的方式直接用来登录，密码和注册时密码相同。这与OAuth2.0社会化登录不同，社会化登录
 * 仅用于提供一种可信凭据，然后重新创建注册用户，是全新的用户。
 */
@Table
public class WoAccountAssociation {
	@Id
	private Long id;

	private Long userId;

	private String bindAccount;

	private String thirdDomain;

	private Long createBy;

	private java.sql.Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private java.sql.Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	/**
	 * 乐观锁
	 */
	@Version
	private Integer versions;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getBindAccount() {
		return bindAccount;
	}

	public void setBindAccount(String bindAccount) {
		this.bindAccount = bindAccount;
	}

	public String getThirdDomain() {
		return thirdDomain;
	}

	public void setThirdDomain(String thirdDomain) {
		this.thirdDomain = thirdDomain;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getCreateIp() {
		return createIp;
	}

	public void setCreateIp(String createIp) {
		this.createIp = createIp;
	}


	public Long getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}


	public java.sql.Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(java.sql.Timestamp updateTime) {
		this.updateTime = updateTime;
	}


	public String getUpdateIp() {
		return updateIp;
	}

	public void setUpdateIp(String updateIp) {
		this.updateIp = updateIp;
	}


	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}


	public Integer getVersions() {
		return versions;
	}

	public void setVersions(Integer versions) {
		this.versions = versions;
	}

}
