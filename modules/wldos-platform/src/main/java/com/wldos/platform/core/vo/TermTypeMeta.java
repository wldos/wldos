/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

/**
 * 分类类型元数据 VO
 *
 * @author 元悉宇宙
 * @date 2025/12/07
 * @version 1.0
 */
public class TermTypeMeta {
	private String code;
	private String name;
	private String description;
	private String structureType; // tree: 树形, flat: 扁平
	private String supportSort; // 1: 支持, 0: 不支持
	private String supportHierarchy; // 1: 支持, 0: 不支持
	private String icon;
	private Integer sortOrder;
	private Boolean isSystem; // 是否系统类型（不可删除）
	private Boolean existsInDb; // 是否在数据库中存在

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStructureType() {
		return structureType;
	}

	public void setStructureType(String structureType) {
		this.structureType = structureType;
	}

	public String getSupportSort() {
		return supportSort;
	}

	public void setSupportSort(String supportSort) {
		this.supportSort = supportSort;
	}

	public String getSupportHierarchy() {
		return supportHierarchy;
	}

	public void setSupportHierarchy(String supportHierarchy) {
		this.supportHierarchy = supportHierarchy;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}

	public Boolean getExistsInDb() {
		return existsInDb;
	}

	public void setExistsInDb(Boolean existsInDb) {
		this.existsInDb = existsInDb;
	}
}

