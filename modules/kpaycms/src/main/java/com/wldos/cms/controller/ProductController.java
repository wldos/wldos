/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.base.NoRepoController;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.PubUnit;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.support.cms.vo.Product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品信息controller。所有的作品以产品的形式展现，以发布内容的形式存储。产品可以线上交易和交付。
 *
 * @author 树悉猿
 * @date 2021/8/17
 * @version 1.0
 */
@RestController
public class ProductController extends NoRepoController<KCMSService> {

	/**
	 * 付费内容详情信息
	 *
	 * @param pid 产品发布内容id
	 * @return 详情信息
	 */
	@GetMapping("product-{pid:\\d+}.html")
	public Product productInfo(@PathVariable Long pid) {
		return this.service.productInfo(pid, false, this.getDomainId());
	}

	/**
	 * 预览产品
	 */
	@GetMapping("product-{id:[0-9]+}/preview")
	public Product previewProduct(@PathVariable Long id) {
		return this.service.productInfo(id, true, null);
	}

	/**
	 * 查看某大类下的付费内容信息存档
	 *
	 * @return 按分类目录索引的存档列表页
	 */
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
	@GetMapping("product/category/{slugCategory}")
	public PageableResult<PubUnit> productCategory(@PathVariable String slugCategory, @RequestParam Map<String, Object> params) {
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
	public String productTag(@PathVariable String xxTag) {

		return this.resJson.ok("");
	}

	/**
	 * 查看作者的作品存档
	 *
	 * @param xxAuthor 作者用户id
	 * @return 作者的内容存档页
	 */
	@GetMapping("product-author/{xxAuthor}")
	public String productAuthor(@PathVariable String xxAuthor) {

		return this.resJson.ok("");
	}
}
