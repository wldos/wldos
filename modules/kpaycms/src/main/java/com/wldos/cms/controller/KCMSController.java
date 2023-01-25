/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wldos.base.controller.NoRepoController;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.Article;
import com.wldos.cms.vo.BookUnit;
import com.wldos.cms.vo.PubUnit;
import com.wldos.cms.vo.SeoCrumbs;
import com.wldos.cms.vo.TypeDomainTerm;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;

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
public class KCMSController extends NoRepoController {

	private final KCMSService kcmsService;

	public KCMSController(KCMSService kcmsService) {
		this.kcmsService = kcmsService;
	}

	/**
	 * 首页跨行业查询
	 * @todo 应该根据参数类型 判断返回的vo类型，分别支持产品、信息、存档的跨行查询
	 *
	 * @param params 请求参数
	 * @return 分页数据
	 */
	@GetMapping("/")
	public PageableResult<BookUnit> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryContentList(pageQuery);
	}

	/**
	 * 某页面侧边栏数据查询
	 *
	 * @param pageName 页面名称
	 * @return 侧边栏数据
	 */
	@GetMapping("cms/listSideCar/{pageName}")
	public PageableResult<BookUnit> listSideCar(@PathVariable String pageName) throws JsonProcessingException {
		// 根据页面名称获取配置的侧边栏参数
		Map<String, Object> params = this.kcmsService.readParamsSideCar(pageName);
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryContentList(pageQuery);
	}

	/**
	 * 查询当前域下某类型的业务对象
	 *
	 * @param industryType 行业门类，用于隔离业务领域
	 * @return 按分类目录索引的存档列表页
	 */
	@GetMapping("archives/{industryType}")
	public PageableResult<PubUnit> archives(@PathVariable String industryType, @RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("industryType", industryType);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryArchivesDomain(pageQuery);
	}

	/**
	 * 查看目录下的内容存档，行业门类不同，展示模板不同，需要的信息不同（付费内容、图文）
	 *
	 * @param industryType 行业门类，用于隔离业务领域
	 * @param slugCategory 分类目录别名
	 * @return 按分类目录索引的存档列表页
	 */
	@GetMapping("archives/{industryType}/category/{slugCategory}")
	public PageableResult<PubUnit> archivesCategory(@PathVariable String industryType, @PathVariable String slugCategory, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("industryType", industryType);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryArchivesCategory(slugCategory, pageQuery);
	}

	/**
	 * 查看标签索引的内容存档
	 *
	 * @param industryType 行业门类，用于隔离业务领域
	 * @param slugTag 标签别名
	 * @return 按标签索引的存档列表页
	 */
	@GetMapping("archives/{industryType}/tag/{slugTag}")
	public PageableResult<PubUnit> archivesTag(@PathVariable String industryType, @PathVariable String slugTag, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("industryType", industryType);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryArchivesTag(slugTag, pageQuery);
	}

	/**
	 * 查看作者的作品存档(单篇的文章、作品等)，与之对应的是查看作者的作品集见<code>ProductController</code>.
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

		return this.kcmsService.queryArchivesDomain(pageQuery);
	}

	/**
	 * 查看作者的作品存档(单篇的文章、作品等)，与之对应的是查看作者的作品集见<code>ProductController</code>.
	 *
	 * @param userId 作者用户id
	 * @return 作者的内容存档页
	 */
	@GetMapping("archives/{industryType}/author/{userId:[0-9]+}.html")
	public PageableResult<PubUnit> archivesAuthorByContType(@PathVariable String industryType, @PathVariable String userId, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("industryType", industryType);
		pageQuery.pushParam("createBy", userId);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryArchivesDomain(pageQuery);
	}

	/**
	 * 查根据作品id查询作品下的章节内容，供阅读。@todo 需要结合作品购买状态输出内容，展现侧需要考虑实现pdf或svg阅读
	 *
	 * @param industryType 内容领域
	 * @param bookId 作品id
	 * @return 按作品id索引的存档列表页
	 */
	@GetMapping("book/{industryType}/{bookId:[0-9]+}.html")
	public PageableResult<PubUnit> queryBookChapter(@PathVariable String industryType, @PathVariable Long bookId, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("industryType", industryType);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryBookChapter(bookId, pageQuery);
	}

	/**
	 * 指定id查看内容，检查付费设置
	 *
	 * @param id 内容id
	 * @return 当前内容
	 */
	@GetMapping("archives-{id:[0-9]+}.html")
	public Article archivesId(@PathVariable Long id) {

		return this.kcmsService.queryArticle(id);
	}

	/**
	 * 指定别名查看内容，检查付费设置
	 *
	 * @param contAlias 内容别名
	 * @return 别名对应的内容
	 */
	@GetMapping("/archive/{contAlias}")
	public String archivesId(@PathVariable String contAlias) {

		return this.resJson.ok("");
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

		return this.kcmsService.queryArchivesUserDomain(this.getDomainId(), pageQuery);
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

		return this.kcmsService.queryArchivesUserDomain(this.getDomainId(), pageQuery);
	}

	/**
	 * 查看页面，页面是通过驱动页面模板+配置内容渲染的独立网页
	 *
	 * @param pageAlias 页面别名
	 * @return 页面内容
	 */
	@GetMapping("/page/{pageAlias}")
	public String page(@PathVariable String pageAlias) {

		return this.resJson.ok("");
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
	 * 文集阅读
	 *
	 * @param cid 内容id
	 * @return 作品片段
	 */
	@GetMapping("cms/read/{cid}")
	public String collectRead(@PathVariable Long cid) {
		return this.resJson.ok("");
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
	 * 根据内容领域和分类法(分类目录、标签等)获取tdk和面包屑数据
	 *
	 * @param params 领域分类参数
	 * @return tdk和面包屑数据
	 */
	@GetMapping("cms/seoCrumbs")
	public SeoCrumbs genSeoCrumbs(@RequestParam Map<String, String> params) {
		TypeDomainTerm tdt = new TypeDomainTerm();
		tdt.setIndustryType(params.get("industryType"));
		tdt.setSlugTerm(params.get("slugTerm"));
		tdt.setTempType(params.get("tempType"));

		return this.kcmsService.genSeoCrumbs(tdt);
	}
}
