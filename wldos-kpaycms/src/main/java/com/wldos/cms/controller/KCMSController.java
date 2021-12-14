/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.cms.dto.PostPicture;
import com.wldos.cms.dto.Thumbnail;
import com.wldos.cms.entity.KPosts;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.cms.enums.PostTypeEnum;
import com.wldos.cms.enums.ThumbTypeEnum;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.Article;
import com.wldos.cms.vo.Book;
import com.wldos.cms.vo.BookUnit;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.PostMeta;
import com.wldos.cms.vo.SeoCrumbs;
import com.wldos.cms.vo.TypeDomainTerm;
import com.wldos.support.controller.EntityAssists;
import com.wldos.support.controller.NoRepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.controller.web.Result;
import com.wldos.support.enums.DeleteFlagEnum;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.cms.enums.MIMETypeEnum;
import com.wldos.cms.enums.PrivacyLevelEnum;
import com.wldos.cms.model.Attachment;
import com.wldos.cms.vo.ContentExt;
import com.wldos.cms.vo.Post;
import com.wldos.cms.vo.PostUnit;
import com.wldos.support.vo.ViewNode;
import com.wldos.system.storage.vo.FileInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class KCMSController extends NoRepoController {

	private final KCMSService kcmsService;

	public KCMSController(KCMSService kcmsService) {
		this.kcmsService = kcmsService;
	}

	
	@GetMapping("/")
	public PageableResult<BookUnit> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryContentList(pageQuery);
	}

	
	@GetMapping("cms/listSideCar/{pageName}")
	public PageableResult<BookUnit> listSideCar(@PathVariable String pageName) throws JsonProcessingException {

		Map<String, Object> params = this.kcmsService.readParamsSideCar(pageName);
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryContentList(pageQuery);
	}

	
	@GetMapping("archives/{contentType}")
	public PageableResult<PostUnit> archives(@PathVariable String contentType,@RequestParam Map<String, Object> params) {


		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryArchivesDomain(this.getDomain(), pageQuery);
	}

	
	@GetMapping("archives/{contentType}/category/{slugCategory}")
	public PageableResult<PostUnit> archivesCategory(@PathVariable String contentType, @PathVariable String slugCategory, @RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryArchivesCategory(this.getDomain(), slugCategory, pageQuery);
	}

	
	@GetMapping("archives/{contentType}/tag/{slugTag}")
	public String archivesTag(@PathVariable String contentType, @PathVariable String slugTag) {

		return this.resJson.ok("");
	}

	
	@GetMapping("archives-author/{userId:[0-9]+}.html")
	public PageableResult<PostUnit> archivesAuthor(@PathVariable String userId, @RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("createBy", userId);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryArchivesDomain(this.getDomain(), pageQuery);
	}

	
	@GetMapping("archives/{contentType}/author/{userId:[0-9]+}.html")
	public PageableResult<PostUnit> archivesAuthorByContType(@PathVariable String contentType, @PathVariable String userId, @RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("createBy", userId);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryArchivesDomain(this.getDomain(), pageQuery);
	}

	
	@GetMapping("book/{contentType}/{bookId:[0-9]+}.html")
	public PageableResult<PostUnit> queryBookChapter(@PathVariable String contentType, @PathVariable Long bookId, @RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryBookChapter(bookId, pageQuery);
	}

	
	@GetMapping("archives-{id:[0-9]+}.html")
	public Article archivesId(@PathVariable Long id) {
		return this.kcmsService.queryArticle(id);
	}

	
	@GetMapping("/archive/{contAlias}")
	public String archivesId(@PathVariable String contAlias) {

		return this.resJson.ok("");
	}

	
	@GetMapping("archives-like/{userId:[0-9]+}.html")
	public PageableResult<PostUnit> archivesLike(@PathVariable String userId, @RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("userId", userId);
		pageQuery.pushParam("likes", "1");
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryArchivesUserDomain(this.getDomain(), pageQuery);
	}

	
	@GetMapping("archives-star/{userId:[0-9]+}.html")
	public PageableResult<PostUnit> archivesStar(@PathVariable String userId, @RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("userId", userId);
		pageQuery.pushParam("stars", "1");
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryArchivesUserDomain(this.getDomain(), pageQuery);
	}

	
	@GetMapping("/page/{pageAlias}")
	public String page(@PathVariable String pageAlias) {

		return this.resJson.ok("");
	}

	
	@GetMapping("category/{cteType}/{parentId:\\d+}")
	public String categoryList(@PathVariable String cteType, @PathVariable Long parentId) {
		return this.resJson.ok("");
	}

	
	@GetMapping("space/book")
	public PageableResult<BookUnit> bookListByAuthor(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("createBy", this.getCurUserId());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.kcmsService.queryContentList(pageQuery);
	}

	
	@GetMapping("space/book/{bookId:\\d+}")
	public Book curBookByAuthorAndBkId(@PathVariable Long bookId) {
		return this.kcmsService.queryBook(bookId);
	}

	
	@GetMapping("space/book/{bookId:\\d+}/chapter/{chapterId:\\d+}")
	public Chapter curChapterByAuthorAndBkIdAndChapId(@PathVariable Long bookId, @PathVariable Long chapterId) {
		return this.kcmsService.queryChapter(chapterId);
	}

	
	@PostMapping("space/book/newChapter")
	public Chapter createChapter(@RequestBody Post chapter) {

		chapter.setComId(this.getTenantId()); // 带上租户id，实现数据隔离

		return this.kcmsService.createChapter(chapter, this.getCurUserId(), this.getUserIp());
	}

	
	@PostMapping("space/book/saveChapter")
	public String saveChapter(@RequestBody Post chapter) {
		this.kcmsService.saveChapter(chapter, this.getCurUserId(), this.getUserIp());

		return this.resJson.ok("ok");
	}

	
	@GetMapping("info/domain/tree")
	public List<ViewNode> categoryAllTree() {
		return this.kcmsService.queryCategoryByDomain(this.getDomain());
	}

	@Value("${wldos.cms.content.maxLength}")
	private int maxLength;

	@PostMapping("info/add")
	public String addContent(@RequestBody String json) throws JsonProcessingException {
		Post post = this.extractPostInfo(json);
		int contLen = post.getPostContent().getBytes(StandardCharsets.UTF_8).length;
		if (contLen > this.maxLength)
			return this.resJson.ok("error", "正文不能超过"+this.maxLength+"字符");
		post.setComId(this.getTenantId());

		Long id = this.kcmsService.insertSelective(post, this.getCurUserId(), this.getUserIp());
		return this.resJson.ok("id", id);
	}

	@GetMapping("info-{id:\\d+}")
	public PostMeta preUpdate(@PathVariable Long id) {
		return this.kcmsService.postInfo(id);
	}

	@PostMapping("info/update")
	public String updateContent(@RequestBody String json) throws JsonProcessingException {
		Post post = this.extractPostInfo(json);
		int contLen = post.getPostContent().getBytes(StandardCharsets.UTF_8).length;
		if (contLen > this.maxLength)
			return this.resJson.ok("error", "正文不能超过"+this.maxLength+"字符");
		this.kcmsService.update(post, this.getCurUserId(), this.getUserIp());
		return this.resJson.ok("ok");
	}

	private Post extractPostInfo(String json) throws JsonProcessingException {
		Post post = new Post();

		Class<Post> postClass = Post.class;
		Field[] fields = postClass.getDeclaredFields();


		Map<String, Object> params = new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {});

		Map<String, Object> temp = new HashMap<>();
		Arrays.stream(fields).forEach(field -> {
			String key = field.getName();
			if (params.containsKey(key)) {
				Object value = params.get(key);
				if (value != null) {
					temp.put(key, value);
				}
				params.remove(key);
			}
		});

		if (!temp.isEmpty())
			post = new ObjectMapper().readValue(this.resJson.toJson(temp, false), new TypeReference<Post>() {});

		List<ContentExt> contentExtList = params.entrySet().parallelStream().map(entry -> {
			if (ObjectUtil.isBlank(entry.getValue()))
				return null;

			ContentExt contentExt = new ContentExt();
			contentExt.setMetaKey(entry.getKey());
			contentExt.setMetaValue(entry.getValue().toString());
			return contentExt;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		post.setContentExt(contentExtList);

		return post;
	}

	@DeleteMapping("info/delete")
	public String deleteContent(@RequestBody Post post) {
		String res = this.kcmsService.delete(post);
		return this.resJson.ok(res);
	}

	
	@PostMapping("info/upload")
	public Result uploadCover(@RequestParam("file") MultipartFile file) throws IOException {
		String width = this.request.getParameter("width");
		String height = this.request.getParameter("height");

		FileInfo fileInfo = this.store.storeFile(this.request, this.response, file, new int[] {
				ObjectUtil.isBlank(width) ? 532 : Integer.parseInt(width), ObjectUtil.isBlank(height) ? 320 : Integer.parseInt(height) });

		Map<String, Object> res = new HashMap<>();
		res.put("path", fileInfo.getPath());
		res.put("url", fileInfo.getUrl());
		return this.resJson.format(res);
	}

	
	@PostMapping("space/upload/chapter")
	public Result uploadArticle(@RequestParam("file") MultipartFile file) throws IOException {
		Long id = Long.parseLong(this.request.getParameter("id"));
		String type = ObjectUtil.string(file.getContentType()).split("/")[0];
		Map<String, Object> res = new HashMap<>();
		if (MIMETypeEnum.IMAGE.getValue().equals(type)) {

			List<Thumbnail> thumbnails = this.kcmsService.getSrcset();

			Thumbnail thumbnail = new Thumbnail();
			thumbnail.setType(ThumbTypeEnum.MEDIUM_LARGE.getValue());
			thumbnail.setWidth(768);
			thumbnail.setHeight(768);
			thumbnails.add(thumbnail);


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


			KPosts post = new KPosts();
			EntityAssists.beforeInsert(post, this.nextId(), this.getCurUserId(), this.getUserIp(), false);
			post.setContentType(PostTypeEnum.ATTACHMENT.toString());
			post.setGuid(url);
			post.setParentId(id);
			post.setPostMimeType(type);
			post.setPostStatus(PostStatusEnum.INHERIT.toString());
			post.setPostTitle(file.getName());

			Attachment attach = new Attachment();

			attach.setAttachMetadata(this.resJson.toJson(postPicture, false));
			attach.setAttachPath(path);

			this.kcmsService.savePostAttach(attach, post);

			res.put("path", path);
			res.put("url", url);
		} else {
			FileInfo fileInfo = this.store.storeFile(this.request, this.response, file);
			res.put("path", fileInfo.getPath());
			res.put("url", fileInfo.getUrl());
		}

		return this.resJson.format(res);
	}

	
	@GetMapping("cms/read/{cid}")
	public String collectRead(@PathVariable Long cid) {
		return this.resJson.ok("");
	}

	
	@GetMapping("cms/down/{fid}")
	public String fileDownload(@PathVariable Long fid) {
		return this.resJson.ok("");
	}

	
	@GetMapping("cms/multi/{mid}")
	public String multimedia(@PathVariable Long mid) {
		return this.resJson.ok("");
	}

	
	@GetMapping("info/enum/privacy")
	public List<Map<String, Object>> fetchEnumPrivacy() {
		return Arrays.stream(PrivacyLevelEnum.values()).map(item -> {
			Map<String, Object> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
	}

	
	@GetMapping("cms/seoCrumbs")
	public SeoCrumbs genSeoCrumbs(@RequestParam Map<String, String> params) {
		TypeDomainTerm tdt = new TypeDomainTerm();
		tdt.setContentType(params.get("contentType"));
		tdt.setSlugTerm(params.get("slugTerm"));
		tdt.setTempType(params.get("tempType"));

		return this.kcmsService.genSeoCrumbs(tdt);
	}
}
