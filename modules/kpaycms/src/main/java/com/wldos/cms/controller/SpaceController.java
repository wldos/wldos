/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wldos.base.entity.EntityAssists;
import com.wldos.base.NoRepoController;
import com.wldos.cms.dto.PubPicture;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.enums.MIMETypeEnum;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.vo.PubUnit;
import com.wldos.common.Constants;
import com.wldos.common.vo.SelectOption;
import com.wldos.sys.base.enums.PubTypeEnum;
import com.wldos.cms.model.Attachment;
import com.wldos.cms.service.SpaceService;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.Book;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.Pub;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;
import com.wldos.support.storage.dto.Thumbnail;
import com.wldos.common.enums.ThumbTypeEnum;
import com.wldos.support.storage.vo.FileInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 内容创作工作台controller。
 *
 * @author 树悉猿
 * @date 2022/01/05
 * @version 1.0
 */
@RefreshScope
@RestController
public class SpaceController extends NoRepoController<SpaceService> {
	@Value("${wldos.cms.content.maxLength}")
	private int maxLength;

	@Value("${wldos.cms.tag.maxTagNum}")
	private int maxTagNum;

	private final KCMSService kcmsService;

	public SpaceController(KCMSService kcmsService) {
		this.kcmsService = kcmsService;
	}

	/**
	 * 在工作台查看作者的作品列表
	 *
	 * @param params {contType}
	 * @return 作品列表
	 */
	@GetMapping("space/book")
	public PageableResult<PubUnit> bookListByAuthor(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("createBy", this.getCurUserId());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		pageQuery.pushFilter("parentId", Constants.TOP_PUB_ID); // 父id为0的都是主类型
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryWorksList(pageQuery);
	}

	/**
	 * 查看作者的当前作品的章节列表
	 *
	 * @param bookId 作品id
	 * @return 作品实体
	 */
	@GetMapping("space/book/{bookId:\\d+}")
	public Book curBookByAuthorAndBkId(@PathVariable Long bookId) {
		return this.kcmsService.queryBook(bookId);
	}

	/**
	 * 查看指定作品、指定章节的内容
	 *
	 * @param bookId 作品id
	 * @param chapterId 章节id
	 * @return 章节实体
	 */
	@GetMapping("space/book/{bookId:\\d+}/chapter/{chapterId:\\d+}")
	public Chapter curChapterByAuthorAndBkIdAndChapId(@PathVariable Long bookId, @PathVariable Long chapterId) {
		return this.service.queryChapter(chapterId);
	}

	@Value("${wldos.cms.tag.tagLength}")
	private int tagLength;

	/**
	 * 新建作品
	 *
	 * @param json 信息
	 * @return ok
	 * @throws JsonProcessingException 处理异常
	 */
	@PostMapping("space/book/add")
	public String addContent(@RequestBody String json) throws JsonProcessingException {
		Pub pub = InfoUtil.extractPubInfo(json);
		if (ObjectUtils.isOutBoundsClearHtml(pub.getPubContent(), this.maxLength))
			return this.resJson.ok("error", "内容超过一万字");
		// 检查分类是否归属同一个类型
		List<SelectOption> typeIds = pub.getTermTypeIds();
		if (typeIds != null) {
			List<Long> termTypeIds = typeIds.stream().map(o -> Long.parseLong(o.getValue())).collect(Collectors.toList());
			if (!this.kcmsService.isValidTerm(termTypeIds))
				return this.resJson.ok("error", "使用了不可识别的分类数据");
		}
		// 检查标签
		List<String> tags = pub.getTagIds();
		if (tags != null ) {
			if (pub.getTagIds().size() > this.maxTagNum) {
				return this.resJson.ok("error", "标签数超过限制：" + this.maxTagNum);
			}
			if (tags.stream().anyMatch(n -> ObjectUtils.isOutBounds(n, this.tagLength))){

				return this.resJson.ok("error", "标签超长");
			}
		}

		pub.setComId(this.getTenantId()); // 带上租户id，实现数据隔离
		pub.setDomainId(this.getDomainId());

		Long id = this.kcmsService.insertSelective(pub, pub.getPubType(), this.getCurUserId(), this.getUserIp());
		return this.resJson.ok("id", id);
	}

	/**
	 * 新增章节
	 *
	 * @param json 章节信息
	 * @return 章节实体
	 */
	@PostMapping("space/book/newChapter")
	public Chapter createChapter(@RequestBody String json) throws JsonProcessingException {
		Pub chapter = InfoUtil.extractPubInfo(json);
		if (chapter.getParentId() == null)
			return null;
		chapter.setComId(this.getTenantId()); // 带上租户id，实现租户隔离, 关闭租户模式时该字段忽略
		chapter.setDomainId(this.getDomainId()); // 带上域id，实现域隔离，同上

		return this.service.createChapter(chapter, this.getCurUserId(), this.getUserIp());
	}

	/**
	 * 更新章节
	 *
	 * @param json 章节信息
	 * @return 结果
	 */
	@PostMapping("space/book/saveChapter")
	public String saveChapter(@RequestBody String json) throws JsonProcessingException {
		Pub chapter = InfoUtil.extractPubInfo(json);
		if (chapter.getId() == null)
			return this.resJson.ok("error", "保存数据为空忽略");
		if (ObjectUtils.isOutBoundsClearHtml(chapter.getPubContent(), this.maxLength))
			return this.resJson.ok("error", "内容超过一万字");
		this.service.saveChapter(chapter, this.getCurUserId(), this.getUserIp());

		return this.resJson.ok("ok");
	}

	/**
	 * 文章编辑时上传图片, 对每一张图跟踪记录
	 *
	 * @param file 内容图片，需要采集用户设置的seo属性：alt、desc、href
	 * @return 附件信息
	 * @throws IOException IO异常
	 */
	@PostMapping("space/upload/chapter")
	public Result uploadArticle(@RequestParam("file") MultipartFile file) throws IOException {
		Long id = Long.parseLong(this.request.getParameter("id"));
		String type = ObjectUtils.string(file.getContentType()).split("/")[0];
		Map<String, Object> res = new HashMap<>();
		if (MIMETypeEnum.IMAGE.getValue().equals(type)) { // 图片作格式等特殊处理
			// 默认创建5种尺寸：150x150、300x300、1024x1024、1536x1536，其中Full（默认尺寸）超出2048压缩为2048。
			List<Thumbnail> thumbnails = this.kcmsService.getSrcset();
			// 保留中大尺寸缩略图适配移动端，不允许用户设置 {"type": "mediumLarge", "width": 768, "height": 768},
			thumbnails.add(new Thumbnail(ThumbTypeEnum.MEDIUM_LARGE.getValue(), 768, 768, null, null));

			// 调用文件存储服务
			List<Thumbnail> thumbnailList = this.store.storePicWithThumbnails(this.request, this.response, file, thumbnails);

			Thumbnail picture = thumbnailList.get(0);
			String path = picture.getPath();
			String url = this.store.getFileUrl(path, null);

			PubPicture postPicture = new PubPicture();
			postPicture.setPath(path);
			postPicture.setHeight(picture.getHeight());
			postPicture.setWidth(picture.getWidth());

			postPicture.setSrcset(thumbnailList);

			// 创建附件发布内容
			KPubs attachment = new KPubs();
			EntityAssists.beforeInsert(attachment, this.nextId(), this.getCurUserId(), this.getUserIp(), false);
			attachment.setPubType(PubTypeEnum.ATTACHMENT.getName());
			attachment.setParentId(id);
			attachment.setPubMimeType(type);
			attachment.setPubStatus(PubStatusEnum.INHERIT.toString());
			attachment.setPubTitle(file.getName());
			attachment.setComId(this.getTenantId()); // 租户隔离
			attachment.setDomainId(this.getDomainId()); // 域隔离

			Attachment attach = new Attachment();
			// attach.setAttachFileAlt(""); // @todo 前端需要传递alt参数
			attach.setAttachMetadata(this.resJson.toJson(postPicture, false)); // @todo 图片元数据, 缩略图元数据
			attach.setAttachPath(path);
			// 保存图片信息到发布内容元信息,先不考虑图片元数据的处理，渲染格式默认存储在发布内容内容中，图片元数据等在媒体库中维护，有跨平台渲染优化需求时，做系统级图片渲染优化处理（创建多尺寸图片或者运行时css缩放）。
			this.kcmsService.savePubAttach(attach, attachment);

			res.put("path", path);
			res.put("url", url);
		}
		else {
			FileInfo fileInfo = this.store.storeFile(this.request, this.response, file);
			res.put("path", fileInfo.getPath());
			res.put("url", fileInfo.getUrl());
		}

		return this.resJson.format(res);
	}

}
