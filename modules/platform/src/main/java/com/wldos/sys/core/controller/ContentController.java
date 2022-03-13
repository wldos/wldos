/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.base.controller.RepoController;
import com.wldos.sys.base.service.ContentService;
import com.wldos.sys.base.entity.KModelContent;
import com.wldos.common.vo.SelectOption;
import com.wldos.common.vo.ValueEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内容模型controller。
 *
 * @author 树悉猿
 * @date 2021/6/12
 * @version 1.0
 */
@RequestMapping("content")
@RestController
public class ContentController extends RepoController<ContentService, KModelContent> {
	@Override
	protected void postAdd(KModelContent org) {
		this.service.refreshContent();
	}

	@Override
	protected void postUpdate(KModelContent org) {
		this.service.refreshContent();
	}

	@Override
	protected void postDelete(KModelContent org) {
		this.service.refreshContent();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.service.refreshContent();
	}

	/**
	 * 查询所有内容模型下拉列表
	 * [{label: '', value: 'id'}, ...]
	 *
	 * @return 内容模型下拉列表
	 */
	@GetMapping("select/id")
	public List<SelectOption> contIdSelectList() {
		List<KModelContent> contents = this.service.queryAllContent();

		return contents.parallelStream().map(t -> new SelectOption(t.getContentName(), t.getId().toString())).collect(Collectors.toList());
	}

	/**
	 * 查询所有内容模型下拉列表
	 * [{label: '', value: 'code'}, ...]
	 *
	 * @return 内容模型下拉列表
	 */
	@GetMapping("select/code")
	public List<SelectOption> contCodeSelectList() {
		List<KModelContent> contents = this.service.queryAllContent();

		return contents.parallelStream().map(t -> new SelectOption(t.getContentName(), t.getContentCode())).collect(Collectors.toList());
	}

	/**
	 * 查询所有内容模型值枚举
	 * {key: {text: '', value: ''}, ...}
	 *
	 * @return 内容模型值枚举
	 */
	@GetMapping("enum")
	public Map<String, ValueEnum> categoryValueEnum() {
		List<KModelContent> contents = this.service.queryAllContent();

		return contents.parallelStream().collect(
				Collectors.toMap(KModelContent::getContentCode, t -> new ValueEnum(t.getContentName(), t.getContentCode())));
	}
}
