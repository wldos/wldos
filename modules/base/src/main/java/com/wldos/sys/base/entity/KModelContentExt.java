/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 内容扩展属性，即可选属性，表示基于内容模型定义的个性定制字段，用于新建内容时的可选属性和内容信息展示时的动态属性。
 * 基于此模型实现扩展字段的增删改查，即动态表单。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
@Table
public class KModelContentExt {

	@Id
	private Long id;

	private String metaKey;

	private String metaName;

	private String metaDesc;

	private String dataType;

	private String enumValue;

	private Long contentId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMetaKey() {
		return metaKey;
	}

	public void setMetaKey(String metaKey) {
		this.metaKey = metaKey;
	}

	public String getMetaName() {
		return metaName;
	}

	public void setMetaName(String metaName) {
		this.metaName = metaName;
	}

	public String getMetaDesc() {
		return metaDesc;
	}

	public void setMetaDesc(String metaDesc) {
		this.metaDesc = metaDesc;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(String enumValue) {
		this.enumValue = enumValue;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}
}
