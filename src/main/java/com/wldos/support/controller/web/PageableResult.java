/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.controller.web;

import java.util.List;

/**
 * 带分页的响应
 * @param <Entity>
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

	PageableResult<Entity> total(int total) {
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
		long total;

		int current;

		int pageSize;

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
