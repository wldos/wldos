/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wldos.common.res.ResultCode;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.cms.enums.PrivacyLevelEnum;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.service.InfoService;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.InfoUnit;
import com.wldos.cms.vo.Pub;
import com.wldos.cms.vo.PubMeta;
import com.wldos.common.Constants;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.platform.support.cms.vo.Info;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.vo.SelectOption;
import com.wldos.framework.support.storage.vo.FileInfo;
import com.wldos.platform.core.enums.PubTypeEnum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 供求信息controller。信息不能线上交易，只能线下交易。
 *
 * @author 元悉宇宙
 * @date 2022/01/05
 * @version 1.0
 */
@Api(tags = "供求信息管理")
@RefreshScope
@RestController
public class InfoController extends NonEntityController<InfoService> {
	@Value("${wldos_cms_content_maxLength:53610}")
	private int maxLength;

	@Value("${wldos_cms_tag_maxTagNum:5}")
	private int maxTagNum;

	private final KCMSService kcmsService;

	public InfoController(KCMSService kcmsService) {
		this.kcmsService = kcmsService;
	}

	/**
	 * 信息详情
	 *
	 * @param pid 信息id
	 * @return 详情信息
	 */
	@ApiOperation(value = "信息详情", notes = "根据ID查询信息详情")
	@GetMapping("info-{pid:\\d+}.html")
	public Info info(@PathVariable Long pid) {
		return this.service.infoDetail(pid, false, this.getDomainId());
	}

	/**
	 * 查看信息（预览模式）
	 *
	 * @param id 信息id
	 * @return 当前信息
	 */
	@ApiOperation(value = "预览信息", notes = "预览模式查看信息")
	@GetMapping("info-{id:[0-9]+}/preview")
	public Info previewInfo(@PathVariable Long id) {
		return this.service.infoDetail(id, true, null);
	}

	/**
	 * 查看当前域下所有信息
	 *
	 * @return 按分类目录索引的信息存档列表页
	 */
	@ApiOperation(value = "信息存档列表", notes = "查询当前域下所有信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "termTypeId", value = "分类ID", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "tagId", value = "标签ID", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("info")
	public PageableResult<InfoUnit> infoArchives(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryInfoDomain(pageQuery);
	}

	/**
	 * 查看某目录下的存档
	 *
	 * @param slugCategory 分类目录别名
	 * @return 按分类目录索引的信息列表页
	 */
	@ApiOperation(value = "分类信息列表", notes = "查询某分类目录下的信息列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("info/category/{slugCategory}")
	public PageableResult<InfoUnit> infoCategory(@ApiParam(value = "分类别名", required = true) @PathVariable String slugCategory, 
			@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryInfoCategory(slugCategory, pageQuery);
	}

	/**
	 * 查看标签索引的信息
	 *
	 * @param slugTag 标签别名
	 * @return 按标签索引的信息列表页
	 */
	@ApiOperation(value = "标签信息列表", notes = "查询某标签下的信息列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("info/tag/{slugTag}")
	public PageableResult<InfoUnit> infoTag(@ApiParam(value = "标签别名", required = true) @PathVariable String slugTag, 
			@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryInfoTag(slugTag, pageQuery);
	}

	/**
	 * 查看作者的信息或个人中心的作品列表(支持供求信息、作品)
	 *
	 * @param userId 作者用户id
	 * @return 作者的内容存档页
	 */
	@ApiOperation(value = "作者信息列表", notes = "查询作者的信息列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("info-author/{userId:[0-9]+}.html")
	public PageableResult<InfoUnit> infoAuthor(@ApiParam(value = "用户ID", required = true) @PathVariable String userId, 
			@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("createBy", userId);
		pageQuery.pushParam("parentId", Constants.TOP_PUB_ID); // 查询作者的主类型发布：信息或其他作品
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.service.queryInfoDomain(pageQuery);
	}

	@Value("${wldos_cms_tag_tagLength:30}")
	private int tagLength;

	/**
	 * 按分类发布信息
	 *
	 * @param json 信息
	 * @return ok
	 * @throws JsonProcessingException 处理异常
	 */
	@ApiOperation(value = "发布信息", notes = "按分类发布新的信息")
	@PostMapping("info/add")
	public Result addContent(@ApiParam(value = "信息JSON", required = true) @RequestBody String json) throws JsonProcessingException {
		Pub pub = InfoUtil.extractPubInfo(json);
		if (ObjectUtils.isOutBoundsClearHtml(pub.getPubContent(), this.maxLength))
			return Result.error(ResultCode.VALIDATION_ERROR, "内容超长，最多1万字");
		// 检查分类是否归属同一个类型
		List<SelectOption> typeIds = pub.getTermTypeIds();
		if (typeIds != null) {
			List<Long> termTypeIds = typeIds.stream().map(o -> Long.parseLong(o.getValue())).collect(Collectors.toList());
			if (!this.kcmsService.isValidTerm(termTypeIds))
				return Result.error(ResultCode.VALIDATION_ERROR, "使用了不可识别的分类数据");
		}
		// 检查标签
		List<String> tags = pub.getTagIds();
		if (tags != null) {
			if (pub.getTagIds().size() > this.maxTagNum) {
				return Result.error(ResultCode.VALIDATION_ERROR, "标签数超过限制：" + this.maxTagNum);
			}
			if (tags.stream().anyMatch(n -> ObjectUtils.isOutBounds(n, this.tagLength))) {

				return Result.error(ResultCode.VALIDATION_ERROR, "标签超长");
			}
		}

		pub.setComId(this.getTenantId()); // 带上租户id，实现数据隔离
		pub.setDomainId(this.getDomainId());

		Long id = this.kcmsService.insertSelective(pub, PubTypeEnum.INFO.getName(), this.getUserId(), this.getUserIp(), this.getDomainId());
		Map<String, Object> data = new HashMap<>();
		data.put("id", id);
		return Result.ok(data);
	}

	/**
	 * 更新作品或内容时预查询信息
	 *
	 * @param id 待更新的作品或内容
	 * @return 更新前元信息
	 */
	@ApiOperation(value = "预查询信息", notes = "更新前预查询信息元数据")
	@GetMapping("info-{id:\\d+}")
	public PubMeta preUpdate(@PathVariable Long id) {
		return this.kcmsService.pubInfo(id, this.getDomainId());
	}

	/**
	 * 更新发布的信息、作品或内容
	 *
	 * @param json 信息
	 * @return 成功
	 */
	@ApiOperation(value = "更新信息", notes = "更新已发布的信息")
	@PostMapping("info/update")
	public Result updateContent(@ApiParam(value = "信息JSON", required = true) @RequestBody String json) throws JsonProcessingException {
		Pub pub = InfoUtil.extractPubInfo(json);
		if (ObjectUtils.isOutBoundsClearHtml(pub.getPubContent(), this.maxLength))
			return Result.error(ResultCode.VALIDATION_ERROR, "内容超长，最多1万字");
		// 检查分类是否归属同一个类型
		List<SelectOption> typeIds = pub.getTermTypeIds();
		if (typeIds != null) {
			List<Long> termTypeIds = typeIds.stream().map(o -> Long.parseLong(o.getValue())).collect(Collectors.toList());
			if (!this.kcmsService.isValidTerm(termTypeIds))
				return Result.error(ResultCode.VALIDATION_ERROR, "使用了不可识别的分类数据");
		}
		// 检查标签
		List<String> tags = pub.getTagIds();
		if (tags != null) {
			if (pub.getTagIds().size() > this.maxTagNum) {
				return Result.error(ResultCode.VALIDATION_ERROR, "标签数超过限制：" + this.maxTagNum);
			}
			if (tags.stream().anyMatch(n -> ObjectUtils.isOutBounds(n, this.tagLength))) {

				return Result.error(ResultCode.VALIDATION_ERROR, "标签超长");
			}
		}
		// 类型转换检查，暂定单体、复合类型不能互转
		if (null != pub.getPubType() && !this.kcmsService.isSamePubTypeStruct(pub.getId(), pub.getPubType())) {
			return Result.error(ResultCode.VALIDATION_ERROR, "选择了不支持的转换类型");
		}

		this.kcmsService.update(pub, this.getUserId(), this.getUserIp(), this.getDomainId());
		return Result.ok("ok");
	}

	/**
	 * 删除所发布的信息
	 *
	 * @param pub 信息
	 * @return 成功
	 */
	@ApiOperation(value = "删除信息", notes = "删除已发布的信息")
	@DeleteMapping("info/delete")
	public Result deleteContent(@ApiParam(value = "信息对象", required = true) @RequestBody Pub pub) {
		String res = this.kcmsService.delete(pub);
		return Result.ok(res);
	}

	/**
	 * 上传封面
	 *
	 * @param file 装饰类图片，需要设置alt等属性
	 * @return 附件信息
	 * @throws IOException io
	 */
	@ApiOperation(value = "上传封面", notes = "上传信息封面图片")
	@PostMapping("info/upload")
	public Result uploadCover(@ApiParam(value = "图片文件", required = true) @RequestParam("file") MultipartFile file) throws IOException {
		String width = this.request.getParameter("width");
		String height = this.request.getParameter("height");
		// 调用文件存储服务
		FileInfo fileInfo = this.store.storeFile(this.request, this.response, file, new int[] {
				ObjectUtils.isBlank(width) ? 532 : Integer.parseInt(width), ObjectUtils.isBlank(height) ? 320 : Integer.parseInt(height) });

		Map<String, Object> res = new HashMap<>();
		res.put("path", fileInfo.getPath());
		res.put("url", fileInfo.getUrl());
		return Result.ok(res);
	}

	/**
	 * 查看方式枚举
	 *
	 * @return 查看方式枚举值
	 */
	@ApiOperation(value = "隐私级别枚举", notes = "获取隐私级别枚举列表")
	@GetMapping("info/enum/privacy")
	public List<SelectOption> fetchEnumPrivacy() {
		return Arrays.stream(PrivacyLevelEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}
}
