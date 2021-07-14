/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 * 分页查询参数。
 *
 * @author wldos
 * @date 2021-04-16
 * @version 1.0
 */
public class PageQuery {

	// 当前页码
	private int current = 1;

	// 每页条数
	private int pageSize = 10;

	// 常规查询条件
	private Map<String, Object> condition;

	// 排序规则
	private Sort sorter;

	// 表头筛选值集
	private Map<String, List<Object>> filter;

	public PageQuery(Map<String, Object> params) {
		this.condition = new HashMap<>();
		this.condition.putAll(params);
		//分页参数
		if (params.get("current") != null) {
			this.current = Integer.parseInt(params.get("current").toString());
			if ( this.current == 0 )
				this.current = 1;
		}
		if (params.get("pageSize") != null) {
			this.pageSize = Integer.parseInt(params.get("pageSize").toString());
		}

		if (params.get("sorter") != null) {
			JSONObject sort = JSONObject.parseObject(ObjectUtil.string(params.get("sorter")));
			if (sort.isEmpty())
				this.sorter = Sort.unsorted();
			else {
				List<Order> orders = new ArrayList<Order>();
				sort.entrySet().stream().forEach(item -> {
					Order order = new Order((ObjectUtil.string(item.getValue()).toLowerCase().startsWith("desc") ? Direction.DESC : Direction.ASC), ObjectUtil.string(item.getKey()));
					Sort.by(order);
					orders.add(order);
				});
				this.sorter = Sort.by(orders);
			}
		} else {
			this.sorter = Sort.unsorted();
		}

		if (params.get("filter") != null) {
			JSONObject filters = JSONObject.parseObject(ObjectUtil.string(params.get("filter")));
			if (filters != null && !filters.isEmpty()) {
				this.filter = filters.entrySet().stream().filter(f -> f.getValue() != null).collect(Collectors.toMap(Map.Entry<String, Object>::getKey, s -> (List<Object>) s.getValue(), (k1, k2) -> k1));
			}
		}

		// 移除特殊查询设置
		this.condition.remove("current");
		this.condition.remove("pageSize");
		this.condition.remove("sorter");
		this.condition.remove("filter");
	}

	public void pushParam(String key, Object value) {
		this.condition.put(key, value);
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int page) {
		this.current = page;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int size) {
		this.pageSize = size;
	}

	public Sort getSorter() {
		return sorter;
	}

	public void setSorter(Sort sorter) {
		this.sorter = sorter;
	}

	public Map<String, List<Object>> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, List<Object>> filter) {
		this.filter = filter;
	}

	public Map<String, Object> getCondition() {
		return condition;
	}

	public void setCondition(Map<String, Object> condition) {
		this.condition = condition;
	}

	/**
	 * 追加参数
	 *
	 * @param key
	 * @param value
	 */
	public void appendParam(String key, Object value) {
		if (this.condition == null)
			this.condition = new HashMap<>();

		this.condition.put(key, value);
	}
}
