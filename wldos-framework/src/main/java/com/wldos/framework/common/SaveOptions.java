/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.common;

/**
 * 保存选项配置类，用于统一 CRUD 方法的参数配置。
 * 
 * 替代多个布尔参数，提供更清晰的 API。
 *
 * @author 元悉宇宙
 * @date 2025-01-03
 * @version 1.0
 */
public class SaveOptions {
	
	/**
	 * 是否自动填充公共字段（createTime, updateTime, createBy, updateBy 等）
	 */
	private boolean autoFill = true;
	
	/**
	 * 是否写入空值（true: 写入所有字段，false: 只写入非空字段）
	 */
	private boolean includeNullValues = false;
	
	/**
	 * 是否合并 null 值（true: 更新时从数据库读取旧值填充 null 字段）
	 */
	private boolean mergeNullValues = false;
	
	/**
	 * 默认选项（推荐使用）
	 * - 自动填充公共字段
	 * - 只写入非空字段
	 * - 不合并 null 值
	 */
	public static SaveOptions defaults() {
		return new SaveOptions();
	}
	
	/**
	 * 数据导入选项
	 * - 不自动填充公共字段
	 * - 只写入非空字段
	 * - 不合并 null 值
	 */
	public static SaveOptions forImport() {
		return new SaveOptions()
			.setAutoFill(false)
			.setIncludeNullValues(false)
			.setMergeNullValues(false);
	}
	
	/**
	 * 写入所有字段选项
	 * - 自动填充公共字段
	 * - 写入所有字段（包括空值）
	 * - 不合并 null 值
	 */
	public static SaveOptions includeAllFields() {
		return new SaveOptions()
			.setAutoFill(true)
			.setIncludeNullValues(true)
			.setMergeNullValues(false);
	}
	
	/**
	 * 合并 null 值选项
	 * - 自动填充公共字段
	 * - 只写入非空字段
	 * - 合并 null 值（更新时从数据库读取旧值填充）
	 */
	public static SaveOptions mergeNulls() {
		return new SaveOptions()
			.setAutoFill(true)
			.setIncludeNullValues(false)
			.setMergeNullValues(true);
	}
	
	/**
	 * 自定义选项（Builder 模式）
	 */
	public static SaveOptions custom() {
		return new SaveOptions();
	}
	
	// Getters and Setters (支持链式调用)
	
	public boolean isAutoFill() {
		return autoFill;
	}
	
	public SaveOptions setAutoFill(boolean autoFill) {
		this.autoFill = autoFill;
		return this;
	}
	
	public boolean isIncludeNullValues() {
		return includeNullValues;
	}
	
	public SaveOptions setIncludeNullValues(boolean includeNullValues) {
		this.includeNullValues = includeNullValues;
		return this;
	}
	
	public boolean isMergeNullValues() {
		return mergeNullValues;
	}
	
	public SaveOptions setMergeNullValues(boolean mergeNullValues) {
		this.mergeNullValues = mergeNullValues;
		return this;
	}
	
	// 私有构造函数，强制使用静态工厂方法
	private SaveOptions() {
	}
}

