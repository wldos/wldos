/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.base.controller.RepoController;
import com.wldos.sys.base.service.IndustryService;
import com.wldos.sys.base.entity.KModelIndustry;
import com.wldos.common.vo.SelectOption;
import com.wldos.common.vo.ValueEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 行业门类controller。
 *
 * @author 树悉猿
 * @date 2021/6/12
 * @version 1.0
 */
@RequestMapping("industry")
@RestController
public class IndustryController extends RepoController<IndustryService, KModelIndustry> {
	@Override
	protected void postAdd(KModelIndustry org) {
		this.service.refreshIndustry();
	}

	@Override
	protected void postUpdate(KModelIndustry org) {
		this.service.refreshIndustry();
	}

	@Override
	protected void postDelete(KModelIndustry org) {
		this.service.refreshIndustry();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.service.refreshIndustry();
	}

	/**
	 * 查询所有行业门类下拉列表
	 * [{label: '', value: 'id'}, ...]
	 *
	 * @return 行业门类下拉列表
	 */
	@GetMapping("select/id")
	public List<SelectOption> industryIdSelectList() {
		List<KModelIndustry> industries = this.service.queryAllIndustry();

		return industries.parallelStream().map(t -> new SelectOption(t.getIndustryName(), t.getId().toString())).collect(Collectors.toList());
	}

	/**
	 * 查询所有行业门类下拉列表
	 * [{label: '', value: 'code'}, ...]
	 *
	 * @return 行业门类下拉列表
	 */
	@GetMapping("select/code")
	public List<SelectOption> industryCodeSelectList() {
		List<KModelIndustry> industries = this.service.queryAllIndustry();

		return industries.parallelStream().map(t -> new SelectOption(t.getIndustryName(), t.getIndustryCode())).collect(Collectors.toList());
	}

	/**
	 * 查询所有行业门类值枚举
	 * {key: {text: '', value: ''}, ...}
	 *
	 * @return 行业门类值枚举
	 */
	@GetMapping("enum")
	public Map<String, ValueEnum> industryValueEnum() {
		List<KModelIndustry> industries = this.service.queryAllIndustry();

		return industries.parallelStream().collect(
				Collectors.toMap(KModelIndustry::getIndustryCode, t -> new ValueEnum(t.getIndustryName(), t.getIndustryCode())));
	}

	/**
	 * 查询所有行业门类值枚举,以id作为key
	 * {key: {text: '', value: ''}, ...}
	 *
	 * @return 行业门类值枚举
	 */
	@GetMapping("enumId")
	public Map<Long, ValueEnum> industryValueIdEnum() {
		List<KModelIndustry> industries = this.service.queryAllIndustry();

		return industries.parallelStream().collect(
				Collectors.toMap(KModelIndustry::getId, t -> new ValueEnum(t.getIndustryName(), t.getIndustryCode())));
	}
}
