/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.base.controller.NoRepoController;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.BookUnit;
import com.wldos.cms.vo.Product;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.PageQuery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品信息controller。
 *
 * @author 树悉猿
 * @date 2021/8/17
 * @version 1.0
 */
@RestController
public class ProductController extends NoRepoController {
	private final KCMSService kcmsService;

	public ProductController(KCMSService kcmsService) {
		this.kcmsService = kcmsService;
	}

	/**
	 * 付费内容详情信息
	 *
	 * @param pid 产品帖子id
	 * @return 详情信息
	 */
	@GetMapping("product-{pid:\\d+}.html")
	public Product productInfo(@PathVariable Long pid) {
		return this.kcmsService.productInfo(pid);
	}

	/**
	 * 查看某大类下的付费内容信息存档
	 *
	 * @param contentType 内容类型，用于隔离业务领域
	 * @return 按分类目录索引的存档列表页
	 */
	@GetMapping("product/{contentType}")
	public PageableResult<BookUnit> productArchives(@PathVariable String contentType, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryProductDomain(pageQuery);
	}

	/**
	 * 查看某目录下的付费内容信息存档
	 *
	 * @param contentType 内容类型，用于隔离业务领域
	 * @param slugCategory 分类目录别名
	 * @return 按分类目录索引的存档列表页
	 */
	@GetMapping("product/{contentType}/category/{slugCategory}")
	public PageableResult<BookUnit> productCategory(@PathVariable String contentType, @PathVariable String slugCategory, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryProductCategory(slugCategory, pageQuery);
	}

	/**
	 * 查看标签索引的内容存档
	 *
	 * @param contentType 内容类型，用于隔离业务领域
	 * @param xxTag 标签别名
	 * @return 按标签索引的存档列表页
	 */
	@GetMapping("product/{contentType}/tag/{xxTag}")
	public String productTag(@PathVariable String contentType, @PathVariable String xxTag) {

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
