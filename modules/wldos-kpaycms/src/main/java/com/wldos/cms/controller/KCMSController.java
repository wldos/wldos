/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.Article;
import com.wldos.cms.vo.PubUnit;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.common.vo.SelectOption;
import com.wldos.platform.support.cms.vo.RouteParams;
import com.wldos.platform.support.cms.vo.SeoCrumbs;
import com.wldos.platform.core.enums.PubTypeEnum;

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
 * 知识内容管理系统前端响应controller。
 * 默认用户端不按多租隔离查询内容，只按分站（业务分类绑定分站，如果分类是区域，则是地区分站）查询。
 *
 * @author 元悉宇宙
 * @date 2021/6/12
 * @version 1.0
 */
@Api(tags = "知识内容管理系统")
@RestController
public class KCMSController extends NonEntityController<KCMSService> {

	/**
	 * 首页跨行业查询
	 *
	 * @param params 请求参数
	 * @return 分页数据
	 */
	@ApiOperation(value = "首页跨行业查询", notes = "查询首页内容列表，支持分页、排序、筛选等")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "pubType", value = "发布类型", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "termTypeId", value = "分类ID", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "tagId", value = "标签ID", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("")
	public PageableResult<PubUnit> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryWorksList(pageQuery);
	}

	/**
	 * 某页面侧边栏数据查询
	 *
	 * @param pageName 页面名称
	 * @return 侧边栏数据
	 */
	@GetMapping("cms/listSideCar/{pageName}")
	public PageableResult<PubUnit> listSideCar(@PathVariable String pageName) throws JsonProcessingException {
		// 根据页面名称获取配置的侧边栏参数
		Map<String, Object> params = this.service.readParamsSideCar(pageName);
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryWorksList(pageQuery);
	}

	/**
	 * 查询当前域下指定发布类型的所有业务对象(支持作品(单结构：图文、视频、音频、图片，复合结构：文集、系列音视频、谱籍)和信息)
	 *
	 * @param pubType 发布类型
	 * @return 按分类目录索引的存档列表页
	 */
	@ApiOperation(value = "发布类型存档列表", notes = "查询指定发布类型的内容列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "termTypeId", value = "分类ID", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "tagId", value = "标签ID", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("archives-all/{pubType}")
	public PageableResult<PubUnit> archivesPubType(@PathVariable String pubType, @RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubType", pubType);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryWorksList(pageQuery);
	}

	/**
	 * 查询当前域下某类型的业务对象，默认输出所有类型（有明确体裁模板的内容），不包括页面这种强自定义类型。
	 *
	 * @return 按分类目录索引的存档列表页
	 */
	@ApiOperation(value = "存档列表", notes = "查询当前域下的内容存档列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "pubType", value = "发布类型", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "termTypeId", value = "分类ID", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("archives")
	public PageableResult<PubUnit> archivesIndustry(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryArchivesDomain(pageQuery);
	}

	/**
	 * 查看目录下的内容存档
	 *
	 * @param slugCategory 分类目录别名
	 * @return 按分类目录索引的存档列表页
	 */
	@ApiOperation(value = "分类存档列表", notes = "查询指定分类下的内容列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("archives/category/{slugCategory}")
	public PageableResult<PubUnit> archivesCategory(@PathVariable String slugCategory, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryArchivesCategory(slugCategory, pageQuery);
	}

	/**
	 * 查看标签索引的内容存档
	 *
	 * @param slugTag 标签别名
	 * @return 按标签索引的存档列表页
	 */
	@ApiOperation(value = "标签存档列表", notes = "查询指定标签下的内容列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("archives/tag/{slugTag}")
	public PageableResult<PubUnit> archivesTag(@PathVariable String slugTag, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryArchivesTag(slugTag, pageQuery);
	}

	/**
	 * 查看作者的作品存档(主类型作品)
	 *
	 * @param userId 作者用户id
	 * @return 作者的内容存档页
	 */
	@ApiOperation(value = "作者作品存档", notes = "查询指定作者的作品列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("archives-author/{userId:[0-9]+}.html")
	public PageableResult<PubUnit> archivesAuthor(@PathVariable String userId, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("createBy", userId);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryArchivesDomain(pageQuery);
	}

	/**
	 * 指定id查看内容，检查付费设置，域隔离
	 *
	 * @param id 内容id
	 * @return 当前内容
	 */
	@ApiOperation(value = "内容详情", notes = "根据ID查询内容详情")
	@GetMapping("archives-{id:[0-9]+}.html")
	public Article archivesId(@ApiParam(value = "内容ID", required = true) @PathVariable Long id) {
		return this.service.queryArticle(id, false, this.getDomainId());
	}

	/**
	 * 预览内容，后端预览不加域隔离
	 *
	 * @param id 内容id
	 * @return 当前内容
	 */
	@ApiOperation(value = "预览内容", notes = "预览模式查看内容")
	@GetMapping("archives-{id:[0-9]+}/preview")
	public Article previewArchive(@ApiParam(value = "内容ID", required = true) @PathVariable Long id) {
		return this.service.queryArticle(id, true, null);
	}

	/**
	 * 指定别名查看内容，检查付费设置
	 *
	 * @param contAlias 内容别名
	 */
	@ApiOperation(value = "内容详情（别名）", notes = "根据别名查询内容详情")
	@GetMapping("/archive-{contAlias}")
	public Article archivesId(@ApiParam(value = "内容别名", required = true) @PathVariable String contAlias) {
		return this.service.queryContentByPubName(contAlias, this.getDomainId());
	}

	/**
	 * 用户喜欢的内容列表
	 *
	 * @param userId 作者用户id
	 * @return 作者的内容存档页
	 */
	@ApiOperation(value = "用户喜欢的内容列表", notes = "查询用户喜欢的内容列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("archives-like/{userId:[0-9]+}.html")
	public PageableResult<PubUnit> archivesLike(@PathVariable String userId, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("userId", userId);
		pageQuery.pushParam("likes", "1");
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryArchivesUserDomain(this.getDomainId(), pageQuery);
	}

	/**
	 * 用户关注(收藏)的内容列表
	 *
	 * @param userId 作者用户id
	 * @return 作者的内容存档页
	 */
	@ApiOperation(value = "用户收藏的内容列表", notes = "查询用户收藏的内容列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("archives-star/{userId:[0-9]+}.html")
	public PageableResult<PubUnit> archivesStar(@PathVariable String userId, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("userId", userId);
		pageQuery.pushParam("stars", "1");
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryArchivesUserDomain(this.getDomainId(), pageQuery);
	}

	/**
	 * 查询某分类法下指定分类的分类列表,分类法包含category、tag等，可以理解为分类类型。
	 *
	 * @param cteType 分类法，分类类型，常见的是category目录、tag标签。
	 * @param parentId 顶级分类的parentId为0
	 * @return 分类项列表
	 */
	@GetMapping("category/{cteType}/{parentId:\\d+}")
	public Result categoryList(@PathVariable String cteType, @PathVariable Long parentId) {
		return Result.ok("");
	}

	/**
	 * 查根据作品id查询作品下的章节内容，供阅读。@todo 需要结合作品购买状态输出内容，展现侧需要考虑实现pdf或svg阅读
	 *
	 * @param bookId 作品id
	 * @return 按作品id索引的存档列表页
	 */
	@ApiOperation(value = "作品章节列表", notes = "查询作品下的章节列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("book-{bookId:[0-9]+}.html")
	public PageableResult<PubUnit> queryBookChapter(@PathVariable Long bookId, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		pageQuery.pushParam("pubStatus", PubStatusEnum.INHERIT.toString()); // 对于子类型来说继承就是发布
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryBookChapter(bookId, pageQuery);
	}

	/**
	 * 读取作品的元素,指定id查看内容，检查付费设置
	 *
	 * @param id 元素内容id
	 * @return 作品片段
	 */
	@ApiOperation(value = "元素详情", notes = "根据ID查询作品元素详情")
	@GetMapping("element-{id:[0-9]+}.html")
	public Article elementRead(@ApiParam(value = "元素ID", required = true) @PathVariable Long id) {
		return this.service.readElement(id, false);
	}

	/**
	 * 预览元素
	 *
	 * @param id 内容id
	 */
	@ApiOperation(value = "预览元素", notes = "预览模式查看作品元素")
	@GetMapping("element-{id:[0-9]+}/preview")
	public Article previewElement(@ApiParam(value = "元素ID", required = true) @PathVariable Long id) {
		return this.service.readElement(id, true);
	}

	/**
	 * 下载文件
	 *
	 * @param fid 文件id
	 * @return 文件
	 */
	@GetMapping("cms/down/{fid}")
	public Result fileDownload(@PathVariable Long fid) {
		return Result.ok("");
	}

	/**
	 * 多媒体浏览
	 *
	 * @param mid 媒体id
	 * @return 多媒体视频流
	 */
	@GetMapping("cms/multi/{mid}")
	public Result multimedia(@PathVariable Long mid) {
		return Result.ok("");
	}

	/**
	 * 根据模板类型和分类法(分类目录、标签等)获取tdk和面包屑数据
	 *
	 * @param params 领域分类参数
	 * @return tdk和面包屑数据
	 */
	@ApiOperation(value = "SEO面包屑", notes = "获取SEO和面包屑数据")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "tempType", value = "模板类型", dataTypeClass = String.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "slugTerm", value = "分类或标签别名", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("cms/seoCrumbs")
	public SeoCrumbs genSeoCrumbs(@RequestParam Map<String, String> params) {
		RouteParams routeParams = RouteParams.of(params.get("tempType"), params.get("slugTerm"));

		return this.service.genSeoCrumbs(routeParams);
	}

	/**
	 * 发布状态枚举
	 *
	 * @return 枚举列表
	 */
	@ApiOperation(value = "发布状态枚举", notes = "获取发布状态枚举列表")
	@GetMapping("enum/select/pubStatus")
	public Result fetchEnumPubStatus() {
		return Result.ok(PubStatusEnum.values());
	}

	/**
	 * 发布类型枚举
	 */
	@ApiOperation(value = "发布类型枚举", notes = "获取发布类型枚举列表")
	@GetMapping("enum/select/pubType")
	public List<SelectOption> fetchEnumPubType() {
		return Arrays.stream(PubTypeEnum.values()).filter(PubTypeEnum::isMainType)
		.map(item -> SelectOption.of(item.getLabel(), item.getName())).collect(Collectors.toList());
	}
}
