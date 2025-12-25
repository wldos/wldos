/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.term.enums;

/**
 * 分类法枚举值。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
public enum TermTypeEnum {
	CATEGORY("category", "内容分类", "用于内容（文章、信息等）的分类体系，支持树形结构", "tree", "1", "1", "FolderOutlined", 1, true),
	TAG("tag", "标签", "用于内容的标签体系，扁平结构，不支持层级", "flat", "1", "0", "TagOutlined", 2, true),
	PLUGIN("plugin", "插件分类", "用于插件的分类体系，支持树形结构", "tree", "1", "1", "AppstoreOutlined", 3, true);

	private final String code;
	private final String name;
	private final String description;
	private final String structureType; // tree: 树形, flat: 扁平
	private final String supportSort; // 1: 支持, 0: 不支持
	private final String supportHierarchy; // 1: 支持, 0: 不支持
	private final String icon;
	private final Integer sortOrder;
	private final Boolean isSystem; // 是否系统类型（不可删除）

	TermTypeEnum(String code, String name, String description, String structureType, 
	             String supportSort, String supportHierarchy, String icon, Integer sortOrder, Boolean isSystem) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.structureType = structureType;
		this.supportSort = supportSort;
		this.supportHierarchy = supportHierarchy;
		this.icon = icon;
		this.sortOrder = sortOrder;
		this.isSystem = isSystem;
	}

	@Override
	public String toString() {
		return this.code;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getStructureType() {
		return structureType;
	}

	public String getSupportSort() {
		return supportSort;
	}

	public String getSupportHierarchy() {
		return supportHierarchy;
	}

	public String getIcon() {
		return icon;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	/**
	 * 根据编码获取枚举
	 *
	 * @param code 类型编码
	 * @return 枚举值，不存在返回null
	 */
	public static TermTypeEnum getByCode(String code) {
		if (code == null) {
			return null;
		}
		for (TermTypeEnum type : values()) {
			if (type.code.equals(code)) {
				return type;
			}
		}
		return null;
	}
}
