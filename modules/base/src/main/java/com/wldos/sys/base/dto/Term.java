/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.dto;

/**
 * 分类项。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
public class Term {
	private Long id;

	private String name;

	private String slug;

	private String infoFlag;

	private Long displayOrder;

	private String isValid;

	private Long termTypeId;

	private String classType;

	private String description;

	private Long parentId;

	private Long count;

	public Term() {
	}

	private Term(Long id, Long termTypeId, String classType, long parentId, String name, String slug) {
		this.id = id;
		this.termTypeId = termTypeId;
		this.classType = classType;
		this.parentId = parentId;
		this.name = name;
		this.slug = slug;
	}

	public static Term of(Long id, Long termTypeId, String classType, long parentId, String name, String slug) {
		return new Term(id, termTypeId, classType, parentId, name, slug);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getInfoFlag() {
		return this.infoFlag;
	}

	public void setInfoFlag(String infoFlag) {
		this.infoFlag = infoFlag;
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

	public Long getTermTypeId() {
		return termTypeId;
	}

	public void setTermTypeId(Long termTypeId) {
		this.termTypeId = termTypeId;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
