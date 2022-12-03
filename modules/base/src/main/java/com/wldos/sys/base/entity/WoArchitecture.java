/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 体系结构是由一系列组织机构通过层次关系组成的一个树形结构。一个公司下可根据业务需求存在多个体系机构，如人事组织结构、办公组织
 * 结构、财务组织结构、项目组织结构等。体系结构可以存在上下级关系，这种关系仅限于展现形式，体系结构与体系结构之间没有权限继承，
 * 也就是说在授权管理中同一公司下的体系结构之间全部是扁平关系。
 */
@Table
public class WoArchitecture {
	@Id
	private Long id;

	private String archCode;

	private String archName;

	private String archDesc;

	private Long comId;

	private Long parentId;

	private Long displayOrder;

	private String isValid;

	private Long createBy;

	private java.sql.Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private String updateIp;

	private java.sql.Timestamp updateTime;

	private String deleteFlag;

	/**
	 * 乐观锁
	 */
	@Version
	private Integer versions;

	public WoArchitecture() {
	}

	public WoArchitecture(Long id, String archName) {
		this.id = id;
		this.archName = archName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getArchCode() {
		return archCode;
	}

	public void setArchCode(String archCode) {
		this.archCode = archCode;
	}


	public String getArchName() {
		return archName;
	}

	public void setArchName(String archName) {
		this.archName = archName;
	}

	public String getArchDesc() {
		return archDesc;
	}

	public void setArchDesc(String archDesc) {
		this.archDesc = archDesc;
	}

	public Long getComId() {
		return comId;
	}

	public void setComId(Long comId) {
		this.comId = comId;
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


	public String getUpdateIp() {
		return updateIp;
	}

	public void setUpdateIp(String updateIp) {
		this.updateIp = updateIp;
	}


	public java.sql.Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(java.sql.Timestamp updateTime) {
		this.updateTime = updateTime;
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
