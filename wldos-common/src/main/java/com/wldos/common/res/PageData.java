/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.res;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 分页数据结构：total、current、pageSize、rows
 * 作为 Result 的 data 使用，由 resJson.format 自动包装为 Result&lt;PageData&lt;T&gt;&gt;
 *
 * @author 元悉宇宙
 * @date 2021/4/29
 * @version 1.0
 */
@ApiModel(description = "分页数据结构")
public class PageData<E> {

	@ApiModelProperty(value = "总记录数", example = "100")
	@JsonSerialize(using = Long2JsonSerializer.class)
	long total;

	@ApiModelProperty(value = "当前页码", example = "1")
	int current = 1;

	@ApiModelProperty(value = "每页条数", example = "10")
	int pageSize = 10;

	@ApiModelProperty(value = "当前页数据列表")
	List<E> rows;

	public PageData(long total, List<E> rows) {
		this.total = total;
		this.rows = rows;
	}

	public PageData(long total, int current, int pageSize, List<E> rows) {
		this.total = total;
		this.current = current;
		this.pageSize = pageSize;
		this.rows = rows;
	}

	public PageData() {
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<E> getRows() {
		return rows;
	}

	public void setRows(List<E> rows) {
		this.rows = rows;
	}
}
