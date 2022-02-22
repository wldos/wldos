/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wldos.base.controller.NoRepoController;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.sys.base.enums.ContModelTypeEnum;
import com.wldos.cms.enums.PrivacyLevelEnum;
import com.wldos.cms.service.InfoService;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.Info;
import com.wldos.cms.vo.Post;
import com.wldos.cms.vo.PostMeta;
import com.wldos.cms.vo.InfoUnit;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;
import com.wldos.support.storage.vo.FileInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供求信息controller。
 *
 * @author 树悉猿
 * @date 2022/01/05
 * @version 1.0
 */
@RestController
public class InfoController extends NoRepoController {
	@Value("${wldos.cms.content.maxLength}")
	private int maxLength;

	@Value("${wldos.cms.tag.maxTagNum}")
	private int maxTagNum;

	private final InfoService infoService;
	private final KCMSService kcmsService;

	public InfoController(InfoService infoService, KCMSService kcmsService) {
		this.infoService = infoService;
		this.kcmsService = kcmsService;
	}

	/**
	 * 信息详情
	 *
	 * @param pid 信息id
	 * @return 详情信息
	 */
	@GetMapping("info-{pid:\\d+}.html")
	public Info info(@PathVariable Long pid) {
		return this.infoService.infoDetail(pid);
	}

	/**
	 * 查看某大类下的存档(支持供求信息、作品)
	 *
	 * @param contentType 内容类型，用于隔离业务领域
	 * @return 按分类目录索引的存档列表页
	 */
	@GetMapping("info/{contentType}")
	public PageableResult<InfoUnit> infoArchives(@PathVariable String contentType, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.infoService.queryInfoDomain(pageQuery);
	}

	/**
	 * 查看某目录下的存档(支持供求信息、作品)
	 *
	 * @param contentType 内容类型，用于隔离业务领域
	 * @param slugCategory 分类目录别名
	 * @return 按分类目录索引的信息列表页
	 */
	@GetMapping("info/{contentType}/category/{slugCategory}")
	public PageableResult<InfoUnit> infoCategory(@PathVariable String contentType, @PathVariable String slugCategory, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.infoService.queryInfoCategory(slugCategory, pageQuery);
	}

	/**
	 * 查看标签索引的信息
	 *
	 * @param contentType 内容类型，用于隔离业务领域
	 * @param slugTag 标签别名
	 * @return 按标签索引的信息列表页
	 */
	@GetMapping("info/{contentType}/tag/{slugTag}")
	public PageableResult<InfoUnit> infoTag(@PathVariable String contentType, @PathVariable String slugTag, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.infoService.queryInfoTag(slugTag, pageQuery);
	}

	/**
	 * 查看作者的信息(支持供求信息、作品)
	 *
	 * @param userId 作者用户id
	 * @return 作者的内容存档页
	 */
	@GetMapping("info-author/{userId:[0-9]+}.html")
	public PageableResult<InfoUnit> infoAuthor(@PathVariable String userId, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("createBy", userId);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.infoService.queryInfoDomain(pageQuery);
	}

	/**
	 * 查看作者的信息(支持供求信息、作品)
	 *
	 * @param userId 作者用户id
	 * @return 作者的内容存档页
	 */
	@GetMapping("info/{contentType}/author/{userId:[0-9]+}.html")
	public PageableResult<InfoUnit> infoConTypeAuthor(@PathVariable String contentType, @PathVariable String userId, @RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("createBy", userId);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.infoService.queryInfoDomain(pageQuery);
	}

	/**
	 * 按分类发布信息
	 *
	 * @param json 信息
	 * @return ok
	 * @throws JsonProcessingException 处理异常
	 */
	@PostMapping("info/add")
	public String addContent(@RequestBody String json) throws JsonProcessingException {
		Post post = InfoUtil.extractPostInfo(json);
		if (ObjectUtils.isOutBoundsClearHtml(post.getPostContent(), this.maxLength))
			return this.resJson.ok("error", "内容超过一万字");
		// 检查分类是否归属同一个类型
		List<Long> termTypeIds = post.getTermTypeIds().stream().map(o -> Long.parseLong(o.getValue())).collect(Collectors.toList());
		if (!this.kcmsService.isSameContentType(termTypeIds))
			return this.resJson.ok("error", "不能超出创建时所选大类");
		// 检查标签
		if (post.getTagIds() != null && post.getTagIds().size() > this.maxTagNum) {
			return this.resJson.ok("error", "标签数超过限制：" + this.maxTagNum);
		}

		post.setComId(this.getTenantId()); // 带上租户id，实现数据隔离
		post.setDomainId(this.getDomainId());

		Long id = this.kcmsService.insertSelective(post, ContModelTypeEnum.INFO.toString(), this.getCurUserId(), this.getUserIp());
		return this.resJson.ok("id", id);
	}

	/**
	 * 更新作品或内容时预查询信息
	 *
	 * @param id 待更新的作品或内容
	 * @return 更新前元信息
	 */
	@GetMapping("info-{id:\\d+}")
	public PostMeta preUpdate(@PathVariable Long id) {
		return this.kcmsService.postInfo(id);
	}

	/**
	 * 更新发布的信息、作品或内容
	 *
	 * @param json 信息
	 * @return 成功
	 */
	@PostMapping("info/update")
	public String updateContent(@RequestBody String json) throws JsonProcessingException {
		Post post = InfoUtil.extractPostInfo(json);
		if (ObjectUtils.isOutBoundsClearHtml(post.getPostContent(), this.maxLength))
			return this.resJson.ok("error", "内容超过一万字");
		// 检查分类是否归属同一个类型
		List<Long> termTypeIds = post.getTermTypeIds().stream().map(o -> Long.parseLong(o.getValue())).collect(Collectors.toList());
		if (!this.kcmsService.isSameContentType(termTypeIds, post.getId()))
			return this.resJson.ok("error", "不能超出创建时所选大类");
		// 检查标签
		if (post.getTagIds() != null && post.getTagIds().size() > this.maxTagNum) {
			return this.resJson.ok("error", "标签数超过限制：" + this.maxTagNum);
		}

		this.kcmsService.update(post, this.getCurUserId(), this.getUserIp());
		return this.resJson.ok("ok");
	}

	/**
	 * 删除所发布的信息
	 *
	 * @param post 信息
	 * @return 成功
	 */
	@DeleteMapping("info/delete")
	public String deleteContent(@RequestBody Post post) {
		String res = this.kcmsService.delete(post);
		return this.resJson.ok(res);
	}

	/**
	 * 上传封面
	 *
	 * @param file 装饰类图片，需要设置alt等属性
	 * @return 附件信息
	 * @throws IOException io
	 */
	@PostMapping("info/upload")
	public Result uploadCover(@RequestParam("file") MultipartFile file) throws IOException {
		String width = this.request.getParameter("width");
		String height = this.request.getParameter("height");
		// 调用文件存储服务
		FileInfo fileInfo = this.store.storeFile(this.request, this.response, file, new int[] {
				ObjectUtils.isBlank(width) ? 532 : Integer.parseInt(width), ObjectUtils.isBlank(height) ? 320 : Integer.parseInt(height) });

		Map<String, Object> res = new HashMap<>();
		res.put("path", fileInfo.getPath());
		res.put("url", fileInfo.getUrl());
		return this.resJson.format(res);
	}

	/**
	 * 查看方式枚举
	 *
	 * @return 查看方式枚举值
	 */
	@GetMapping("info/enum/privacy")
	public List<Map<String, Object>> fetchEnumPrivacy() {
		return Arrays.stream(PrivacyLevelEnum.values()).map(item -> {
			Map<String, Object> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
	}
}
