/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base.entity;

import java.sql.Timestamp;

/**
 * 抽象实体，用于定义公共字段对应的公共属性。
 *
 * @author 树悉猿
 * @date 2021-02-11
 * @version V1.0
 */
public class BaseEntity {
	private Long id;

	private Long createBy;

	private java.sql.Timestamp createTime;

	private String createIp;

	private final Long updateBy;

	private final java.sql.Timestamp updateTime;

	private final String updateIp;

	private String deleteFlag;

	private String isValid;

	private Integer versions;


	public BaseEntity(Long updateBy, Timestamp updateTime, String updateIp) {
		this.updateBy = updateBy;
		this.updateTime = updateTime;
		this.updateIp = updateIp;
	}

	public BaseEntity(Long id, Long createBy, Timestamp createTime, String createIp, Long updateBy, Timestamp updateTime,
			String updateIp, String deleteFlag, String isValid, Integer versions) {
		this.id = id;
		this.createBy = createBy;
		this.createTime = createTime;
		this.createIp = createIp;
		this.updateBy = updateBy;
		this.updateTime = updateTime;
		this.updateIp = updateIp;
		this.deleteFlag = deleteFlag;
		this.isValid = isValid;
		this.versions = versions;
	}

	public Long getId() {
		return id;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public String getCreateIp() {
		return createIp;
	}

	public Long getUpdateBy() {
		return updateBy;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public String getUpdateIp() {
		return updateIp;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public String getIsValid() {
		return isValid;
	}

	public Integer getVersions() {
		return versions;
	}
}
