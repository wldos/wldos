/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.res;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.wldos.common.utils.ObjectUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 强类型分页查询参数：4 个公共参数 + 泛型 T 表示该接口的查询条件（含筛选、排序可放在 T 内或沿用 sorter/filter）。
 * <p>
 * wldos-ui 列表请求约定：请求体为 current、pageSize、sorter、filter + 其余扁平字段；本类用 {@link #getParams()} 承载「其余」的强类型。
 * Controller 入参使用 {@code PagedQuery<OrderListParams>}，Service/Dao 直接使用 {@link #getParams()}，无需 Map 解析。
 * </p>
 * <p>
 * T 建议为接口专用的请求 DTO，字段与前端传的查询条件 key 一致；T 上可加 {@code @JsonIgnoreProperties(ignoreUnknown = true)} 并忽略 "current","pageSize","sorter","filter" 以兼容多余 key。
 * </p>
 *
 * 泛型 F：filter 类型。filter 参数可包含**多个筛选**（多列表头筛选），结构为 { "列名": [ 选中值数组 ], ... }。
 * F 为强类型时，建议 DTO 内为每个筛选项定义一字段（如 List&lt;String&gt; statusFilter、List&lt;Long&gt; categoryIdFilter）；兼容时 F = Map&lt;String, List&lt;Object&gt;&gt;。
 *
 * @param <T> 本接口的强类型查询参数（condition）
 * @param <F> 本接口的强类型筛选（filter），避免 Map+List；兼容时 F = Map&lt;String, List&lt;Object&gt;&gt;
 * @author wldos
 */
@ApiModel(description = "强类型分页查询参数：公共分页字段 + 泛型查询条件与筛选")
public class PageParams<T, F> {

	@ApiModelProperty(value = "当前页码", example = "1")
	private int current = 1;

	@ApiModelProperty(value = "每页条数", example = "10")
	private int pageSize = 10;

	/** 排序规则，结构固定：{ "字段名": "asc"|"desc" }，多列则多 key */
	@ApiModelProperty(value = "排序规则，如 {\"createTime\":\"desc\"}")
	private Sort sorter = Sort.unsorted();

	@ApiModelProperty(value = "表头筛选值集，可包含多个筛选：{ \"列名\": [ 选中值 ], ... }。强类型 F 时为本接口的筛选 DTO，每列对应一字段（如 statusFilter、categoryIdFilter）")
	private F filter;

	/**
	 * 强类型查询参数：除 current、pageSize、sorter、filter 外的全部请求字段反序列化到 T。
	 * 使用 @JsonUnwrapped 使前端扁平传参（与 4 个公共参数同级）直接绑定到 T。
	 */
	@JsonUnwrapped
	@ApiModelProperty(value = "本接口的查询条件（强类型），与公共参数同级扁平传递")
	private T params;

	public PageParams() {
	}

	public PageParams(T params) {
		this.params = params;
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

	public Sort getSorter() {
		return sorter != null ? sorter : Sort.unsorted();
	}

	public void setSorter(Sort sorter) {
		this.sorter = sorter;
	}

	/** Jackson 反序列化 sorter 时，前端传 Map 如 {"createTime":"desc"}，需转换为 Sort */
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
				orders.add(new Order(dir.startsWith("desc") ? Direction.DESC : Direction.ASC, ObjectUtils.string(key)));
			});
			this.sorter = Sort.by(orders);
			return;
		}
		this.sorter = Sort.unsorted();
	}

	public F getFilter() {
		return filter;
	}

	public void setFilter(F filter) {
		this.filter = filter;
	}

	/** 当 F 为 Map 时，返回该 filter；否则返回空 Map，便于 toPageQuery 等兼容写法 */
	@SuppressWarnings("unchecked")
	public Map<String, List<Object>> getFilterAsMap() {
		if (filter instanceof Map) {
			return (Map<String, List<Object>>) filter;
		}
		return Collections.emptyMap();
	}

	public T getParams() {
		return params;
	}

	public void setParams(T params) {
		this.params = params;
	}

	/**
	 * 转为现有 {@link PageQuery}，便于与仍使用 PageQuery 的 Service/Dao 兼容。
	 * 将 getParams() 转为 condition Map 需要业务侧提供 T → Map 的转换；此处仅拷贝 current、pageSize、sorter、filter。
	 */
	public PageQuery toPageQuery() {
		PageQuery q = new PageQuery();
		q.setCurrent(getCurrent());
		q.setPageSize(getPageSize());
		q.setSorter(getSorter());
		q.setFilter(getFilterAsMap());
		if (params != null) {
			q.setConditionBean(params);
		}
		return q;
	}

	/**
	 * 仅查询条件强类型、filter 仍为 Map 时的便捷用法，避免写 {@code PageParams<T, Map<String, List<Object>>>}。
	 * 用法：{@code PageParams.WithMapFilter<OrderListParams>}。
	 */
	public static class WithMapFilter<T> extends PageParams<T, Map<String, List<Object>>> {
	}
}
