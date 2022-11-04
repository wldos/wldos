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
 * 组织机构是体系结构上的一个节点，每个节点都有一个上级节点和若干个下级节点。组织机构是有级别的，其级别是其所在体系结构的层次
 * 深度，如在一个体系结构下最根的组织机构节点是1级，1级下面的组织机构节点是2级，2级下面的组织机构节点是3级……。组织机构有实机
 * 构与虚机构之分，实机构是真是存在的组织机构，而虚机构一般是为了收纳一些零散的组织机构或用户所建立的虚拟组织机构。实组织机构
 * 是有级别的，而且其下级组织机构的级别会加1；而虚机构是不占用级别的，其下级组织机构（实机构）会在其上级组织机构（实机构）的
 * 基础上加1。如1级的实机构“A”下有一个实机构“AA”和一个虚机构“Ab”，实机构“AA”的级别是2级，虚机构“Ab”是不占级别的，其下如果
 * 也有一个实机构“AbA”和一个虚机构“Abb”，那么实机构“AbA”是2级，而虚机构“Abb”不占级别，“Abb”下的实机构“AbbA”级别是2级，
 * 因为其上的两级都是虚机构都不占级别。
 * 组织机构拥有类型，1级组织机构拥有国标组织机构类型编码和名称，下级组织机构可能是不同类型的组织节点，可能是部门、岗位、角色
 * 或群组。不同类型的组织机构节点在组织体系结构的树形结构上编织成一棵组织树，这棵树的根是公司（租户），根节点是1级组织机构，次级
 * 根节点可以是部门、岗位、角色或群组，叶子节点是人员。
 *
 */
@Table
public class WoOrg {
	@Id
	private Long id;

	private String orgCode;

	private String orgName;

	private String orgLogo;

	private String orgType;

	private Long archId;

	private Long comId;

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

	/**
	 * 乐观锁
	 */
	@Version
	private Integer versions;

	public WoOrg() {
	}

	public WoOrg(Long id, String orgName) {
		this.id = id;
		this.orgName = orgName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}


	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgLogo() {
		return orgLogo;
	}

	public void setOrgLogo(String orgLogo) {
		this.orgLogo = orgLogo;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}


	public Long getArchId() {
		return archId;
	}

	public void setArchId(Long archId) {
		this.archId = archId;
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
