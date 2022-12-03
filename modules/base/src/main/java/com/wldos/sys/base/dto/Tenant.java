/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.dto;

import java.sql.Timestamp;

public class Tenant {

	private Long id;

	private String comCode;

	private String comName;

	private String comDesc;

	private Long parentId;

	private Long displayOrder;

	private String isValid;

	private Long createBy;

	private java.sql.Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private java.sql.Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	public Tenant() {
	}

	public Tenant(Long id, String comCode, String comName, String comDesc, Long parentId, Long displayOrder,
			String isValid, Long createBy, Timestamp createTime, String createIp, Long updateBy, Timestamp updateTime,
			String updateIp, String deleteFlag) {
		this.id = id;
		this.comCode = comCode;
		this.comName = comName;
		this.comDesc = comDesc;
		this.parentId = parentId;
		this.displayOrder = displayOrder;
		this.isValid = isValid;
		this.createBy = createBy;
		this.createTime = createTime;
		this.createIp = createIp;
		this.updateBy = updateBy;
		this.updateTime = updateTime;
		this.updateIp = updateIp;
		this.deleteFlag = deleteFlag;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComCode() {
		return comCode;
	}

	public void setComCode(String comCode) {
		this.comCode = comCode;
	}

	public String getComName() {
		return comName;
	}

	public void setComName(String comName) {
		this.comName = comName;
	}

	public String getComDesc() {
		return comDesc;
	}

	public void setComDesc(String comDesc) {
		this.comDesc = comDesc;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getIsValid() {
		return isValid;
	}

	public void setIsValid(String isValid) {
		this.isValid = isValid;
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

}
