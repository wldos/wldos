/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.cms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wldos.framework.controller.NoRepoController;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.Article;
import com.wldos.cms.vo.PubUnit;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.common.vo.SelectOption;
import com.wldos.support.cms.vo.RouteParams;
import com.wldos.support.cms.vo.SeoCrumbs;
import com.wldos.sys.base.enums.PubTypeEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 知识内容管理系统前端响应controller。
 * 默认用户端不按多租隔离查询内容，只按分站（业务分类绑定分站，如果分类是区域，则是地区分站）查询。
 *
 * @author 树悉猿
 * @date 2021/6/12
 * @version 1.0
 */
@RestController
public class KCMSController extends NoRepoController<KCMSService> {

	/**
	 * 首页跨行业查询
	 *
	 * @param params 请求参数
	 * @return 分页数据
	 */
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
	@GetMapping("archives-{id:[0-9]+}.html")
	public Article archivesId(@PathVariable Long id) {
		return this.service.queryArticle(id, false, this.getDomainId());
	}

	/**
	 * 预览内容，后端预览不加域隔离
	 *
	 * @param id 内容id
	 * @return 当前内容
	 */
	@GetMapping("archives-{id:[0-9]+}/preview")
	public Article previewArchive(@PathVariable Long id) {
		return this.service.queryArticle(id, true, null);
	}

	/**
	 * 指定别名查看内容，检查付费设置
	 *
	 * @param contAlias 内容别名
	 */
	@GetMapping("/archive-{contAlias}")
	public Article archivesId(@PathVariable String contAlias) {
		return this.service.queryContentByPubName(contAlias, this.getDomainId());
	}

	/**
	 * 用户喜欢的内容列表
	 *
	 * @param userId 作者用户id
	 * @return 作者的内容存档页
	 */
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
	public String categoryList(@PathVariable String cteType, @PathVariable Long parentId) {
		return this.resJson.ok("");
	}

	/**
	 * 查根据作品id查询作品下的章节内容，供阅读。@todo 需要结合作品购买状态输出内容，展现侧需要考虑实现pdf或svg阅读
	 *
	 * @param bookId 作品id
	 * @return 按作品id索引的存档列表页
	 */
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
	@GetMapping("element-{id:[0-9]+}.html")
	public Article elementRead(@PathVariable Long id) {
		return this.service.readElement(id, false);
	}

	/**
	 * 预览元素
	 *
	 * @param id 内容id
	 */
	@GetMapping("element-{id:[0-9]+}/preview")
	public Article previewElement(@PathVariable Long id) {
		return this.service.readElement(id, true);
	}

	/**
	 * 下载文件
	 *
	 * @param fid 文件id
	 * @return 文件
	 */
	@GetMapping("cms/down/{fid}")
	public String fileDownload(@PathVariable Long fid) {
		return this.resJson.ok("");
	}

	/**
	 * 多媒体浏览
	 *
	 * @param mid 媒体id
	 * @return 多媒体视频流
	 */
	@GetMapping("cms/multi/{mid}")
	public String multimedia(@PathVariable Long mid) {
		return this.resJson.ok("");
	}

	/**
	 * 根据模板类型和分类法(分类目录、标签等)获取tdk和面包屑数据
	 *
	 * @param params 领域分类参数
	 * @return tdk和面包屑数据
	 */
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
	@GetMapping("enum/select/pubStatus")
	public Result fetchEnumPubStatus() {
		return this.resJson.format(PubStatusEnum.values());
	}

	/**
	 * 发布类型枚举
	 */
	@GetMapping("enum/select/pubType")
	public List<SelectOption> fetchEnumPubType() {
		return Arrays.stream(PubTypeEnum.values()).filter(PubTypeEnum::isMainType).map(item -> SelectOption.of(item.getLabel(), item.getName())).collect(Collectors.toList());
	}
}
