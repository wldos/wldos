/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.PubUnit;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.support.cms.vo.Product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 产品信息controller。所有的作品以产品的形式展现，以发布内容的形式存储。产品可以线上交易和交付。
 *
 * @author 元悉宇宙
 * @date 2021/8/17
 * @version 1.0
 */
@Api(tags = "产品信息管理")
@RestController
public class ProductController extends NonEntityController<KCMSService> {

	/**
	 * 付费内容详情信息
	 *
	 * @param pid 产品发布内容id
	 * @return 详情信息
	 */
	@ApiOperation(value = "产品详情", notes = "根据ID查询产品详情")
	@GetMapping("product-{pid:\\d+}.html")
	public Product productInfo(@ApiParam(value = "产品ID", required = true) @PathVariable Long pid) {
		return this.service.productInfo(pid, false, this.getDomainId());
	}

	/**
	 * 预览产品
	 */
	@ApiOperation(value = "预览产品", notes = "预览模式查看产品")
	@GetMapping("product-{id:[0-9]+}/preview")
	public Product previewProduct(@ApiParam(value = "产品ID", required = true) @PathVariable Long id) {
		return this.service.productInfo(id, true, null);
	}

	/**
	 * 查看某大类下的付费内容信息存档
	 *
	 * @return 按分类目录索引的存档列表页
	 */
	@ApiOperation(value = "产品存档列表", notes = "查询产品存档列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "termTypeId", value = "分类ID", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("product")
	public PageableResult<PubUnit> productArchives(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryProductDomain(pageQuery);
	}

	/**
	 * 查看某目录下的付费内容信息存档
	 *
	 * @param slugCategory 分类目录别名
	 * @return 按分类目录索引的存档列表页
	 */
	@ApiOperation(value = "分类产品列表", notes = "查询某分类下的产品列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("product/category/{slugCategory}")
	public PageableResult<PubUnit> productCategory(@ApiParam(value = "分类别名", required = true) @PathVariable String slugCategory, 
			@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryProductCategory(slugCategory, pageQuery);
	}

	/**
	 * 查看标签索引的内容存档
	 *
	 * @param xxTag 标签别名
	 * @return 按标签索引的存档列表页
	 */
	@GetMapping("product/tag/{xxTag}")
	public Result productTag(@PathVariable String xxTag) {

		return Result.ok("");
	}

	/**
	 * 查看作者的作品存档
	 *
	 * @param xxAuthor 作者用户id
	 * @return 作者的内容存档页
	 */
	@GetMapping("product-author/{xxAuthor}")
	public Result productAuthor(@PathVariable String xxAuthor) {

		return Result.ok("");
	}
}
