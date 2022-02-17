/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.res;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wldos.common.utils.ObjectUtils;

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
@SuppressWarnings("unchecked")
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
		String currentKey = "current";
		String pageSizeKey = "pageSize";
		String sorterKey = "sorter";
		String filterKey = "filter";
		//分页参数
		if (params.get(currentKey) != null) {
			this.current = Integer.parseInt(params.get(currentKey).toString());
			if ( this.current == 0 )
				this.current = 1;
		}
		if (params.get(pageSizeKey) != null) {
			this.pageSize = Integer.parseInt(params.get(pageSizeKey).toString());
		}

		if (params.get(sorterKey) != null) {
			this.sort(params, sorterKey);
		} else {
			this.sorter = Sort.unsorted();
		}

		if (params.get(filterKey) != null) {
			JSONObject filters = JSON.parseObject(ObjectUtils.string(params.get(filterKey)));
			if (filters != null && !filters.isEmpty()) {
				this.filter = filters.entrySet().stream().filter(f -> f.getValue() != null).collect(Collectors.toMap(Map.Entry<String, Object>::getKey, s -> (List<Object>) s.getValue(), (k1, k2) -> k1));
			}
		}

		// 移除特殊查询设置
		this.condition.remove(currentKey);
		this.condition.remove(pageSizeKey);
		this.condition.remove(sorterKey);
		this.condition.remove(filterKey);
	}

	private void sort(Map<String, Object> params, String sorterKey) {
		JSONObject sort = JSON.parseObject(ObjectUtils.string(params.get(sorterKey)));
		if (sort.isEmpty())
			this.sorter = Sort.unsorted();
		else {
			List<Order> orders = new ArrayList<>();
			sort.forEach((key, value) -> {
				Order order = new Order((ObjectUtils.string(value).toLowerCase().startsWith("desc") ? Direction.DESC : Direction.ASC), ObjectUtils.string(key));
				// this.sorter = Sort.by(order);
				orders.add(order);
			});
			this.sorter = Sort.by(orders);
		}
	}

	/**
	 * 追加查询参数
	 *
	 * @param key 参数key
	 * @param value 参数值
	 */
	public void pushParam(String key, Object value) {
		this.condition.put(key, value);
	}

	/**
	 * 删除查询参数
	 *
	 * @param key 参数key
	 */
	public void removeParam(String key) {
		this.condition.remove(key);
	}

	/**
	 * 追加过滤参数
	 *
	 * @param key 参数key
	 * @param value 参数过滤值集合
	 */
	public void pushFilter(String key, Object... value) {
		if (this.filter == null)
			this.filter = new HashMap<>();
		this.filter.put(key, Arrays.asList(value.clone()));
	}

	/**
	 * 追加过滤参数
	 *
	 * @param key 参数key
	 * @param value 参数过滤值集合
	 */
	public void pushFilter(String key, List<Object> value) {
		if (this.filter == null)
			this.filter = new HashMap<>();
		this.filter.put(key, value);
	}

	/**
	 * 追加排序字段
	 *
	 * @param newOrders 排序字段数组：{{"colName", "desc"},...}, colName可以是：foo、a.foo、a.foo | b.bar;
	 * desc可以是：desc、descend、asc、ascend，asc可以省略：{{"colName"}, ...}
	 */
	public void pushSort(String[][] newOrders) {
		List<Order> orders;
		if (this.sorter == null || this.sorter == Sort.unsorted()) {
			orders = new ArrayList<>();
		} else
			orders = this.sorter.get().collect(Collectors.toList());

		List<Order> ords = Arrays.stream(newOrders).filter(Objects::nonNull).filter(n -> n.length == 0).map(this::extractOrder).collect(Collectors.toList());
		orders.addAll(ords);
		this.sorter = Sort.by(orders);
	}

	private Order extractOrder(String[] newOrder) {
		return new Order(newOrder.length > 1 ? (ObjectUtils.string(newOrder[1]).toLowerCase().startsWith("desc") ? Direction.DESC : Direction.ASC) : Direction.ASC,
				ObjectUtils.string(newOrder[0]));
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
	 *  @param key 参数key
	 * @param value 参数值
	 * @return PageQuery实例引用
	 */
	public PageQuery appendParam(String key, Object value) {
		if (this.condition == null)
			this.condition = new HashMap<>();

		this.condition.put(key, value);

		return this;
	}
}