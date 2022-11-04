/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.base.Base;
import com.wldos.base.entity.EntityAssists;
import com.wldos.cms.entity.KPosts;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.sys.base.enums.ContModelTypeEnum;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.Post;
import com.wldos.common.utils.DateUtils;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.sys.base.dto.ContentExt;
import com.wldos.sys.base.dto.Term;
import com.wldos.sys.base.enums.TermTypeEnum;
import com.wldos.sys.base.service.TermService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 内容创作工作台service。
 *
 * @author 树悉猿
 * @date 2022/01/05
 * @version 1.0
 */
@Slf4j
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class SpaceService extends Base {

	private final PostService postService;

	private final TermService termService;

	private final KCMSService kcmsService;

	public SpaceService(PostService postService, TermService termService, KCMSService kcmsService) {
		this.postService = postService;
		this.termService = termService;
		this.kcmsService = kcmsService;
	}

	/**
	 * 查询作品的某个章节
	 *
	 * @param chapterId the chapter id
	 * @return chapter info
	 */
	public Chapter queryChapter(Long chapterId) {
		KPosts post = this.postService.findById(chapterId);

		return new Chapter(post.getId(), ObjectUtils.string(post.getPostTitle()), ObjectUtils.string(post.getPostContent())/* 过滤null值，防止前端不刷新*/,
				post.getParentId(), post.getPostStatus(), post.getContentType());
	}

	/**
	 * 创建新章节
	 *
	 * @param chapter chapter
	 * @param curUserId current user id
	 * @param userIp current user ip
	 * @return chapter info
	 */
	public Chapter createChapter(Post chapter, Long curUserId, String userIp) {
		chapter.setPostTitle(DateUtils.format(new Date(), DateUtils.TIME_PATTER));

		// 行业门类contentType从前端传送
		chapter.setPostStatus(PostStatusEnum.INHERIT.toString());
		chapter.setPostType(ContModelTypeEnum.CHAPTER.toString());
		Long id = this.insertChapter(chapter, curUserId, userIp);

		// 取出父作品分类
		List<Term> termsType = this.termService.findAllByObjectAndClassType(chapter.getParentId(), TermTypeEnum.CATEGORY.toString());
		List<Long> termTypeIds = termsType.stream().map(Term::getTermTypeId).collect(Collectors.toList());

		// 内容分类以作品为准，作品分类的变更不能超出创建时确定的行业门类（这是为了模型的一致性），另外已经被内容继承的作品分类不允许删除（暂未作约束）
		// 首先，内容的分类应该继承作品，这是模型确定的结果，而标签则无此限制，标签仅是反映了当下内容的信息特征，可以脱离业务边界而设置。
		this.termService.saveTermObject(termTypeIds, id);
		// 标签同分类
		List<Term> tags = this.termService.findAllByObjectAndClassType(chapter.getParentId(), TermTypeEnum.TAG.toString());
		List<Long> tagIds = tags.stream().map(Term::getTermTypeId).collect(Collectors.toList());
		this.termService.saveTermObject(tagIds, id);

		return this.queryChapter(id);
	}

	/**
	 * 保存章节内容
	 *
	 * @param chapter 章节信息
	 * @param curUserId 操作用户id
	 * @param userIp 用户ip
	 */
	public void saveChapter(Post chapter, Long curUserId, String userIp) {
		this.kcmsService.update(chapter, curUserId, userIp);
	}

	private final BeanCopier postCopier = BeanCopier.create(Post.class, KPosts.class, false);

	/**
	 * 保存带扩展属性的子内容：章节、附件、图片等
	 *
	 * @param post 帖子
	 * @param userId 用户id
	 * @param userIp 用户ip
	 */
	public Long insertChapter(Post post, Long userId, String userIp) {

		// 为便于结构化处理，对于图片等附件的处理，要在上传文件和编辑文件时将设置数据存储到post metadata中，在帖子渲染时再读出

		KPosts posts = new KPosts();
		this.postCopier.copy(post, posts, null);

		Long id = this.nextId();
		EntityAssists.beforeInsert(posts, id, userId, userIp, false);

		// @todo 考虑嵌入过滤器hook：posts = applyFilter("savePost", posts);

		this.postService.insertSelective(posts);

		List<ContentExt> contentExt = post.getContentExt();

		if (ObjectUtils.isBlank(contentExt)) {
			contentExt = new ArrayList<>();
			this.kcmsService.appendPubMeta(contentExt); // 添加后台静默处理的元数据，比如查看数
		}

		// @todo 考虑嵌入过滤器hook：contentExt = applyFilter("saveContentExt", contentExt);
		// 保存扩展属性
		this.kcmsService.createPostMeta(contentExt, id);

		return id;
	}
}
