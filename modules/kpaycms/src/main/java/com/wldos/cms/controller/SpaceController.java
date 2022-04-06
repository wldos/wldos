/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
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
import com.wldos.base.controller.NoRepoController;
import com.wldos.cms.dto.PostPicture;
import com.wldos.cms.entity.KPosts;
import com.wldos.cms.enums.MIMETypeEnum;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.sys.base.enums.ContModelTypeEnum;
import com.wldos.cms.model.Attachment;
import com.wldos.cms.service.SpaceService;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.Book;
import com.wldos.cms.vo.BookUnit;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.Post;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;
import com.wldos.support.storage.dto.Thumbnail;
import com.wldos.common.enums.ThumbTypeEnum;
import com.wldos.support.storage.vo.FileInfo;

import org.springframework.beans.factory.annotation.Value;
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
@RestController
public class SpaceController extends NoRepoController {
	@Value("${wldos.cms.content.maxLength}")
	private int maxLength;

	@Value("${wldos.cms.tag.maxTagNum}")
	private int maxTagNum;

	private final SpaceService spaceService;
	private final KCMSService kcmsService;

	public SpaceController(SpaceService spaceService, KCMSService kcmsService) {
		this.spaceService = spaceService;
		this.kcmsService = kcmsService;
	}

	/**
	 * 在工作台查看作者的作品列表
	 *
	 * @param params {contType}
	 * @return 作品列表
	 */
	@GetMapping("space/book")
	public PageableResult<BookUnit> bookListByAuthor(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("createBy", this.getCurUserId());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		this.applyDomainFilter(pageQuery);

		return this.kcmsService.queryContentList(pageQuery);
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
		return this.spaceService.queryChapter(chapterId);
	}

	/**
	 * 新建作品
	 *
	 * @param json 信息
	 * @return ok
	 * @throws JsonProcessingException 处理异常
	 */
	@PostMapping("space/book/add")
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

		Long id = this.kcmsService.insertSelective(post, ContModelTypeEnum.BOOK.toString(), this.getCurUserId(), this.getUserIp());
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
		Post chapter = InfoUtil.extractPostInfo(json);
		if (chapter.getParentId() == null)
			return null;
		chapter.setComId(this.getTenantId()); // 带上租户id，实现租户隔离, 关闭租户模式时该字段忽略
		chapter.setDomainId(this.getDomainId()); // 带上域id，实现域隔离，同上

		return this.spaceService.createChapter(chapter, this.getCurUserId(), this.getUserIp());
	}

	/**
	 * 更新章节
	 *
	 * @param json 章节信息
	 * @return 结果
	 */
	@PostMapping("space/book/saveChapter")
	public String saveChapter(@RequestBody String json) throws JsonProcessingException {
		Post chapter = InfoUtil.extractPostInfo(json);
		if (chapter.getId() == null)
			return this.resJson.ok("error", "保存数据为空忽略");
		if (ObjectUtils.isOutBoundsClearHtml(chapter.getPostContent(), this.maxLength))
			return this.resJson.ok("error", "内容超过一万字");
		this.spaceService.saveChapter(chapter, this.getCurUserId(), this.getUserIp());

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
			// 默认创建4种尺寸：150x150、300x300、1024x1024、Full（默认尺寸）
			List<Thumbnail> thumbnails = this.kcmsService.getSrcset();
			// 保留中大尺寸缩略图适配移动端，不允许用户设置 {"type": "mediumLarge", "width": 768, "height": 768},
			thumbnails.add(new Thumbnail(ThumbTypeEnum.MEDIUM_LARGE.getValue(), 768, 768, null, null));

			// 调用文件存储服务
			List<Thumbnail> thumbnailList = this.store.storePicWithThumbnails(this.request, this.response, file, thumbnails);

			Thumbnail picture = thumbnailList.get(0);
			String path = picture.getPath();
			String url = this.store.getFileUrl(path, null);

			PostPicture postPicture = new PostPicture();
			postPicture.setPath(path);
			postPicture.setHeight(picture.getHeight());
			postPicture.setWidth(picture.getWidth());

			thumbnailList = thumbnailList.subList(1, thumbnailList.size() - 1);
			postPicture.setSrcset(thumbnailList);

			// 创建附件帖子
			KPosts post = new KPosts();
			EntityAssists.beforeInsert(post, this.nextId(), this.getCurUserId(), this.getUserIp(), false);
			post.setContentType(ContModelTypeEnum.ATTACHMENT.toString());
			post.setParentId(id);
			post.setPostMimeType(type);
			post.setPostStatus(PostStatusEnum.INHERIT.toString());
			post.setPostTitle(file.getName());
			post.setComId(this.getTenantId()); // 租户隔离
			post.setDomainId(this.getDomainId()); // 域隔离

			Attachment attach = new Attachment();
			// attach.setAttachFileAlt(""); // @todo 前端需要传递alt参数
			attach.setAttachMetadata(this.resJson.toJson(postPicture, false)); // @todo 图片元数据, 缩略图元数据
			attach.setAttachPath(path);
			// 保存图片信息到帖子元信息,先不考虑图片元数据的处理，渲染格式默认存储在帖子内容中，图片元数据等在媒体库中维护，有跨平台渲染优化需求时，做系统级图片渲染优化处理（创建多尺寸图片或者运行时css缩放）。
			this.kcmsService.savePostAttach(attach, post);

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
