/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.res;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 带分页参数的响应消息模板。
 *
 * @author 树悉猿
 * @date 2021/4/29
 * @version 1.0
 */
public class PageableResult<Entity> extends Result {

	PageableData<Entity> data;

	public PageableResult(long total, List<Entity> rows) {
		this.setMessage("ok");
		this.data = new PageableData<Entity>(total, rows);
	}

	public PageableResult(long total, int current, int pageSize, List<Entity> rows) {
		this.setMessage("ok");
		this.data = new PageableData<Entity>(total, current, pageSize, rows);
	}

	public PageableResult() {
		this.data = new PageableData<Entity>();
	}

	public PageableResult<Entity> setDataRows(List<Entity> rows) {
		this.data.setRows(rows);
		return this;
	}

	PageableResult<Entity> total(long total) {
		this.data.setTotal(total);
		return this;
	}

	PageableResult<Entity> total(List<Entity> rows) {
		this.data.setRows(rows);
		return this;
	}

	public PageableData<Entity> getData() {
		return data;
	}

	public void setData(PageableData<Entity> data) {
		this.data = data;
	}

	public class PageableData<Entity> {
		@JsonSerialize(using = Long2JsonSerializer.class)
		long total;

		int current = 1;

		int pageSize = 10;

		List<Entity> rows;

		public PageableData(long total, List<Entity> rows) {
			this.total = total;
			this.rows = rows;
		}

		public PageableData(long total, int current, int pageSize,List<Entity> rows) {
			this.total = total;
			this.current = current;
			this.pageSize = pageSize;
			this.rows = rows;
		}

		public PageableData() {
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

		public List<Entity> getRows() {
			return rows;
		}

		public void setRows(List<Entity> rows) {
			this.rows = rows;
		}
	}
}
