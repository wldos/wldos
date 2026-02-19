/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.common;

/**
 * 保存选项配置类，用于 BaseDao.saveOrUpdate(entity, options) 等方法的参数配置。
 * <p>
 * 提供更灵活的可选保存行为，更符合实际场景：数据导入（不自动填充）、部分更新（只写非空）、
 * 全量覆盖（写空值）、合并 null（更新时用库中旧值填充）等，替代多个布尔参数，API 更清晰。
 * 使用方式：静态工厂如 {@code SaveOptions.mergeNulls()}；自定义选项可用 Builder：
 * {@code SaveOptions.builder().autoFill(false).includeNullValues(true).build()}，或 {@code SaveOptions.custom().setAutoFill(false)...}。
 *
 * @author 元悉宇宙
 * @date 2025-01-03
 * @version 1.0
 * @see com.wldos.framework.mvc.dao.BaseDao#saveOrUpdate(Object, SaveOptions)
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
	 * 自定义选项（链式 setter，与 builder() 二选一即可）
	 */
	public static SaveOptions custom() {
		return new SaveOptions();
	}

	/**
	 * Builder 入口，用于流式构建自定义选项。
	 * 示例：{@code SaveOptions.builder().autoFill(false).includeNullValues(true).build()}
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder 模式实现类，用于流式构建 SaveOptions。
	 */
	public static final class Builder {
		private boolean autoFill = true;
		private boolean includeNullValues = false;
		private boolean mergeNullValues = false;

		private Builder() {
		}

		public Builder autoFill(boolean autoFill) {
			this.autoFill = autoFill;
			return this;
		}

		public Builder includeNullValues(boolean includeNullValues) {
			this.includeNullValues = includeNullValues;
			return this;
		}

		public Builder mergeNullValues(boolean mergeNullValues) {
			this.mergeNullValues = mergeNullValues;
			return this;
		}

		public SaveOptions build() {
			SaveOptions opts = new SaveOptions();
			opts.autoFill = this.autoFill;
			opts.includeNullValues = this.includeNullValues;
			opts.mergeNullValues = this.mergeNullValues;
			return opts;
		}
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

