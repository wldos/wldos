/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.res;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.wldos.common.utils.ObjectUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 * 分页查询参数。
 * <p>
 * wldos-ui 列表请求约定：除 4 个公共参数（current、pageSize、sorter、filter）外，其余均为查询条件（扁平传递）。
 * 本类解析时从请求体取出上述 4 项，剩余 key-value 落入 {@link #getCondition()}。
 * </p>
 * <p>
 * 兼顾通用自定义参数与强类型：{@link #getCondition()} 为通用 Map；可选 {@link #setConditionBean(Object)} 携带强类型条件。
 * Dao 侧使用 {@link #getConditionAs(Class, Function)}：有 conditionBean 则直用，无则用 fromMap 从 condition 解析。
 * </p>
 * <p>
 * 通用 Map 下的性能优化：condition/filter 按需创建、预分配容量、空时返回不可变空 Map；写请用 pushParam/appendParam/pushFilter。
 * </p>
 *
 * @author wldos
 * @date 2021-04-16
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@ApiModel(description = "分页查询参数")
public class PageQuery {

	@ApiModelProperty(value = "当前页码", example = "1")
	private int current = 1;

	@ApiModelProperty(value = "每页条数", example = "10")
	private int pageSize = 10;

	@ApiModelProperty(value = "常规查询条件")
	private Map<String, Object> condition;

	/** 排序规则，结构固定：{ "字段名": "asc"|"desc" }，多列则多 key。如 {"createTime":"desc","id":"asc"} */
	@ApiModelProperty(value = "排序规则，如 {\"createTime\":\"desc\",\"id\":\"asc\"}")
	private Sort sorter;

	@ApiModelProperty(value = "表头筛选值集")
	private Map<String, List<Object>> filter;

	/**
	 * 可选强类型条件。Controller 在已绑定专用请求 DTO 时设置，Dao 通过 {@link #getConditionAs(Class, Function)} 使用，
	 * 可避免 from(Map) 解析，减少对象与 GC。未设置时仍使用 {@link #getCondition()} Map。
	 */
	private Object conditionBean;

	/** condition 常用 key 数量，用于预分配容量、减少 HashMap 扩容 */
	private static final int CONDITION_INITIAL_CAPACITY = 8;
	/** filter 常用 key 数量 */
	private static final int FILTER_INITIAL_CAPACITY = 4;

	/** 无参构造，供 Jackson 等反序列化使用。condition 延迟创建，无条件时零分配。 */
	public PageQuery() {
		this.condition = null;
		this.sorter = Sort.unsorted();
	}

	public PageQuery(Map<String, Object> params) {
		if (params == null) {
			this.condition = null;
			this.sorter = Sort.unsorted();
			return;
		}
		int cap = Math.max(CONDITION_INITIAL_CAPACITY, params.size());
		this.condition = new HashMap<>(cap);
		this.condition.putAll(params);
		String currentKey = "current";
		String pageSizeKey = "pageSize";
		String sorterKey = "sorter";
		String filterKey = "filter";
		// 分页参数
		if (params.get(currentKey) != null) {
			this.current = Integer.parseInt(params.get(currentKey).toString());
			if (this.current == 0)
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
				this.filter = filters.entrySet().stream().filter(f -> f.getValue() != null)
				.collect(Collectors.toMap(Map.Entry<String, Object>::getKey, s -> (List<Object>) s.getValue(), (k1, k2) -> k1));
			}
		}
		// 移除特殊查询设置
		this.condition.remove(currentKey);
		this.condition.remove(pageSizeKey);
		this.condition.remove(sorterKey);
		this.condition.remove(filterKey);
	}

	/**
	 * 解析 sorter 参数。结构固定：{ "字段名": "asc"|"desc" }（或 ascend/descend），多字段则多 key。
	 */
	private void sort(Map<String, Object> params, String sorterKey) {
		JSONObject sort = JSON.parseObject(ObjectUtils.string(params.get(sorterKey)));
		if (sort.isEmpty())
			this.sorter = Sort.unsorted();
		else {
			List<Order> orders = new ArrayList<>();
			sort.forEach((key, value) -> {
				Order order = new Order(
						ObjectUtils.string(value).toLowerCase().startsWith("desc") ? Direction.DESC : Direction.ASC,
						ObjectUtils.string(key));
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
		if (this.condition == null) {
			this.condition = new HashMap<>(CONDITION_INITIAL_CAPACITY);
		}
		this.condition.put(key, value);
	}

	/**
	 * 删除查询参数
	 *
	 * @param key 参数key
	 */
	public void removeParam(String key) {
		if (this.condition != null) {
			this.condition.remove(key);
		}
	}

	/**
	 * 追加过滤参数
	 *
	 * @param key 参数key
	 * @param value 参数过滤值集合
	 */
	public void pushFilter(String key, Object... value) {
		if (this.filter == null) {
			this.filter = new HashMap<>(FILTER_INITIAL_CAPACITY);
		}
		this.filter.put(key, Arrays.asList(value.clone()));
	}

	/**
	 * 追加过滤参数
	 *
	 * @param key 参数key
	 * @param value 参数过滤值集合
	 */
	public <T> void pushFilter(String key, List<T> value) {
		this.pushFilter(key, value.toArray());
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
		}
		else
			orders = this.sorter.get().collect(Collectors.toList());

		List<Order> ords = Arrays.stream(newOrders).filter(Objects::nonNull).filter(n -> n.length > 0).map(this::extractOrder).collect(Collectors.toList());
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

	/** Jackson 反序列化 sorter 时，前端传的是 Map 如 {"displayOrder":"ascend"}，需转换为 Sort */
	@JsonSetter("sorter")
	public void setSorterFromJson(Object val) {
		if (val == null) {
			this.sorter = Sort.unsorted();
			return;
		}
		if (val instanceof Sort) {
			this.sorter = (Sort) val;
			return;
		}
		if (val instanceof Map) {
			Map<?, ?> sortMap = (Map<?, ?>) val;
			if (sortMap.isEmpty()) {
				this.sorter = Sort.unsorted();
				return;
			}
			List<Order> orders = new ArrayList<>();
			sortMap.forEach((key, value) -> {
				String dir = ObjectUtils.string(value).toLowerCase();
				Order order = new Order(dir.startsWith("desc") ? Direction.DESC : Direction.ASC, ObjectUtils.string(key));
				orders.add(order);
			});
			this.sorter = Sort.by(orders);
			return;
		}
		this.sorter = Sort.unsorted();
	}

	public Map<String, List<Object>> getFilter() {
		return filter != null ? filter : Collections.emptyMap();
	}

	public void setFilter(Map<String, List<Object>> filter) {
		this.filter = filter;
	}

	public Map<String, Object> getCondition() {
		return condition != null ? condition : Collections.emptyMap();
	}

	public void setCondition(Map<String, Object> condition) {
		this.condition = condition;
	}

	public Object getConditionBean() {
		return conditionBean;
	}

	public void setConditionBean(Object conditionBean) {
		this.conditionBean = conditionBean;
	}

	/**
	 * 优先返回强类型 condition：若 {@link #conditionBean} 已设置且类型匹配则返回，否则用 fromMap 从 {@link #getCondition()} 解析。
	 * Dao 侧统一写：{@code pageQuery.getConditionAs(OrderAdminListCondition.class, OrderAdminListCondition::from)} 即可兼顾二者。
	 *
	 * @param type   强类型 class
	 * @param fromMap 当 conditionBean 未设置或类型不匹配时，用 condition Map 解析为 T 的函数
	 * @return 强类型条件，不会为 null（由 fromMap 保证）
	 */
	public <T> T getConditionAs(Class<T> type, Function<Map<String, Object>, T> fromMap) {
		if (conditionBean != null && type.isInstance(conditionBean)) {
			return type.cast(conditionBean);
		}
		return fromMap.apply(getCondition());
	}

	/**
	 * 追加参数
	 * @param key 参数key
	 * @param value 参数值
	 * @return PageQuery实例引用
	 */
	public PageQuery appendParam(String key, Object value) {
		if (this.condition == null) {
			this.condition = new HashMap<>(CONDITION_INITIAL_CAPACITY);
		}
		this.condition.put(key, value);
		return this;
	}
}