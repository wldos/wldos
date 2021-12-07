/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.wldos.cms.dto.TermObject;
import com.wldos.cms.entity.KStars;
import com.wldos.cms.entity.KTermObject;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.cms.enums.PostTypeEnum;
import com.wldos.cms.dto.ContModelDto;
import com.wldos.cms.enums.TermTypeEnum;
import com.wldos.cms.model.KModelMetaKey;
import com.wldos.cms.repo.PostRepo;
import com.wldos.cms.vo.AuditPost;
import com.wldos.cms.vo.BookUnit;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.MiniPost;
import com.wldos.cms.vo.PostMember;
import com.wldos.cms.vo.PostUnit;
import com.wldos.cms.vo.SPost;
import com.wldos.cms.vo.Term;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.AtomicUtil;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.system.storage.IStore;
import com.wldos.cms.entity.KPostmeta;
import com.wldos.cms.entity.KPosts;
import com.wldos.system.core.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 帖子service。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PostService extends BaseService<PostRepo, KPosts, Long> {

	private final IStore store;

	private final UserService userService;

	private final TermService termService;

	private final PostmetaService postmetaService;

	public PostService(IStore store, UserService userService, TermService termService, PostmetaService postmetaService) {
		this.store = store;
		this.userService = userService;
		this.termService = termService;
		this.postmetaService = postmetaService;
	}


	public ContModelDto queryContModel(Long pid) {

		return this.entityRepo.queryContModel(pid);
	}


	public List<ContModelDto> queryContAttach(Long pid) {
		return this.entityRepo.queryContSubModel(pid, PostTypeEnum.ATTACHMENT.toString());
	}


	public PageableResult<BookUnit> queryPostWithExtList(PageQuery pageQuery) {
		PageableResult<BookUnit> postUnitPage = this.execQueryForPage(BookUnit.class, KPosts.class, KTermObject.class, "k_posts", "k_term_object", "object_id", pageQuery);

		List<BookUnit> postUnits = postUnitPage.getData().getRows();

		if (postUnits == null || postUnits.isEmpty())
			return postUnitPage;

		List<Long> ids = postUnits.parallelStream().map(BookUnit::getId).collect(Collectors.toList());

		List<KPostmeta> postMetas = this.queryPostExt(ids);

		Map<Long, List<PostMember>> postMembers = this.queryUserInfoByBookIds(ids);

		Map<Long, List<KPostmeta>> maps = postMetas.stream().collect(Collectors.groupingBy(KPostmeta::getPostId));

		AtomicInteger count = AtomicUtil.count(pageQuery);

		postUnits = postUnits.stream().map(post -> {

			post.setMembers(postMembers.get(post.getId()));

			List<KPostmeta> metas = maps.get(post.getId());
			if (ObjectUtil.isBlank(metas)) {


				post.setCover(this.defaultCover(count.getAndIncrement()));

				return post;
			}

			Map<String, String> meta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));


			String coverUrl = this.store.getFileUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			post.setCover(coverUrl);

			post.setSubTitle(ObjectUtil.string(meta.get(KModelMetaKey.PUB_META_KEY_SUB_TITLE)));

			return post;
		}).collect(Collectors.toList());

		postUnitPage.setDataRows(postUnits);

		return postUnitPage;
	}


	public PageableResult<PostUnit> queryArchives(PageQuery pageQuery) {
		PageableResult<PostUnit> postUnitPage = this.execQueryForPage(PostUnit.class, KPosts.class, KTermObject.class, "k_posts", "k_term_object", "object_id", pageQuery);

		List<PostUnit> postUnits = postUnitPage.getData().getRows();

		if (postUnits == null || postUnits.isEmpty())
			return postUnitPage;

		List<Long> ids = postUnits.parallelStream().map(PostUnit::getId).collect(Collectors.toList());

		List<KPostmeta> postMetas = this.queryPostExt(ids);

		Map<Long, PostMember> postMembers = this.queryUserInfoByPostIds(ids);

		Map<Long, List<KPostmeta>> maps = postMetas.stream().collect(Collectors.groupingBy(KPostmeta::getPostId));


		List<TermObject> terms = this.termService.findTermByObjectId(ids, TermTypeEnum.TAG.toString());
		Map<Long, List<Term>> termList = terms.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));

		AtomicInteger count = AtomicUtil.count(pageQuery);

		postUnits = postUnits.stream().map(post -> {

			if (ObjectUtil.isBlank(post.getPostExcerpt())) {

				post.setPostExcerpt(
						this.genPostExcerpt(
								post.getPostContent(), 140));
			}

			post.setMember(postMembers.get(post.getId()));

			post.setTags(termList.get(post.getId()));

			List<KPostmeta> metas = maps.get(post.getId());
			if (ObjectUtil.isBlank(metas)) {


				post.setCover(this.defaultCover(count.getAndIncrement()));

				return post;
			}

			Map<String, String> meta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));


			String coverUrl = this.store.getFileUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			post.setCover(coverUrl);

			return post;
		}).collect(Collectors.toList());

		postUnitPage.setDataRows(postUnits);

		return postUnitPage;
	}


	public PageableResult<AuditPost> queryAdminPosts(PageQuery pageQuery) {
		PageableResult<AuditPost> postUnitPage = this.execQueryForPage(AuditPost.class, KPosts.class, KTermObject.class, "k_posts", "k_term_object", "object_id", pageQuery);

		List<AuditPost> postUnits = postUnitPage.getData().getRows();

		if (postUnits == null || postUnits.isEmpty())
			return postUnitPage;

		List<Long> ids = postUnits.parallelStream().map(AuditPost::getId).collect(Collectors.toList());

		Map<Long, PostMember> postMembers = this.queryUserInfoByPostIds(ids);


		List<TermObject> terms = this.termService.findTermByObjectId(ids, TermTypeEnum.TAG.toString());
		Map<Long, List<Term>> termList = terms.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));

		List<TermObject> termTypes = this.termService.findTermByObjectId(ids, TermTypeEnum.CATEGORY.toString());
		Map<Long, List<Term>> termTypeList = termTypes.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));


		List<KPostmeta> postMetas = this.postmetaService.queryAllByPostIdInAndMetaKey(ids, KModelMetaKey.PUB_META_KEY_VIEWS);
		Map<Long, String> viewsMap = postMetas.parallelStream().collect(Collectors.toMap(KPostmeta::getPostId, KPostmeta::getMetaValue));

		postUnits = postUnits.stream().map(post -> {
			Long id = post.getId();
			post.setMember(postMembers.get(id));


			post.setTags(termList.get(id));
			post.setTerms(termTypeList.get(id));

			post.setViews(viewsMap.get(id));
			return post;
		}).collect(Collectors.toList());

		postUnitPage.setDataRows(postUnits);

		return postUnitPage;
	}

	public PageableResult<AuditPost> queryAdminPosts(String domain, PageQuery pageQuery) {
		List<Object> termTypeIds = this.termService.findAllTermTypeIdsByDomain(domain);

		if (termTypeIds.isEmpty())
			return new PageableResult<>();

		this.filterByParentTermTypeId(termTypeIds, pageQuery);

		return this.queryAdminPosts(pageQuery);
	}

	public PageableResult<SPost> searchPosts(String domain, PageQuery pageQuery) {
		List<Object> termTypeIds = this.termService.findAllTermTypeIdsByDomain(domain);

		if (termTypeIds.isEmpty())
			return new PageableResult<>();

		this.filterByParentTermTypeId(termTypeIds, pageQuery);

		return this.execQueryForPage(SPost.class, KPosts.class, KTermObject.class, "k_posts", "k_term_object", "object_id", pageQuery);
	}

	private void filterByParentTermTypeId(List<Object> termTypeIds, PageQuery pageQuery) {

		pageQuery.pushFilter("termTypeId", termTypeIds);
	}

	public PageableResult<PostUnit> queryArchivesUser(PageQuery pageQuery) {
		PageableResult<PostUnit> postUnitPage = this.execQueryForPage(PostUnit.class, KPosts.class, KStars.class, "k_posts", "k_stars", "object_id", pageQuery);

		List<PostUnit> postUnits = postUnitPage.getData().getRows();

		if (postUnits == null || postUnits.isEmpty())
			return postUnitPage;

		List<Long> ids = postUnits.parallelStream().map(PostUnit::getId).collect(Collectors.toList());

		List<KPostmeta> postMetas = this.queryPostExt(ids);

		Map<Long, PostMember> postMembers = this.queryUserInfoByPostIds(ids);

		Map<Long, List<KPostmeta>> maps = postMetas.stream().collect(Collectors.groupingBy(KPostmeta::getPostId));


		List<TermObject> terms = this.termService.findTermByObjectId(ids, TermTypeEnum.TAG.toString());
		Map<Long, List<Term>> termList = terms.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));

		AtomicInteger count = AtomicUtil.count(pageQuery);

		postUnits = postUnits.stream().map(post -> {

			if (ObjectUtil.isBlank(post.getPostExcerpt())) {

				post.setPostExcerpt(
						this.genPostExcerpt(
								post.getPostContent(), 140));
			}

			post.setMember(postMembers.get(post.getId()));

			post.setTags(termList.get(post.getId()));

			List<KPostmeta> metas = maps.get(post.getId());
			if (ObjectUtil.isBlank(metas)) {


				post.setCover(this.defaultCover(count.getAndIncrement()));

				return post;
			}

			Map<String, String> meta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));


			String coverUrl = this.store.getFileUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			post.setCover(coverUrl);

			return post;
		}).collect(Collectors.toList());

		postUnitPage.setDataRows(postUnits);

		return postUnitPage;
	}


	public String defaultCover(int count) {

		String[] covers = {
				"/sdfkjiwenfgldfgilq.jpg",
				"/iZBVOIhGJiAnhplqjvZW.jpg",
				"/iXjVmWVHbCJAyqvDxdtx.jpg",
				"/gLaIAoVWTtLbBWZNYEMg.jpg"
		};

		int coverNum = covers.length;

		return this.store.getFileUrl(((count / coverNum) % 2 == 0 ? covers[count % coverNum] : covers[(coverNum - 1) - (count % coverNum)]), "");
	}


	public List<KPostmeta> queryPostExt(List<Long> ids) {
		return this.entityRepo.queryPostExt(ids);
	}


	public Map<Long, List<PostMember>> queryUserInfoByBookIds(List<Long> ids) {
		List<PostMember> postMembers = this.entityRepo.queryMembersByPostIds(ids);
		postMembers = postMembers.parallelStream().map(pm -> {
			pm.setAvatar(this.userService.getAvatarUrl(pm.getAvatar()));
			return pm;
		}).collect(Collectors.toList());
		return postMembers.stream().collect(Collectors.groupingBy(PostMember::getPostId));
	}


	public Map<Long, PostMember> queryUserInfoByPostIds(List<Long> ids) {
		List<PostMember> postMembers = this.entityRepo.queryMembersByPostIds(ids);
		postMembers = postMembers.parallelStream().map(pm -> {
			pm.setAvatar(this.userService.getAvatarUrl(pm.getAvatar()));
			return pm;
		}).collect(Collectors.toList());
		return postMembers.stream().collect(Collectors.toMap(PostMember::getPostId, pm -> pm, (p1, p2) -> p1));
	}


	public PostMember queryMemberByPostId(Long postId) {
		PostMember member = this.entityRepo.queryMemberByPostId(postId);
		member.setAvatar(this.userService.getAvatarUrl(member.getAvatar()));
		return member;
	}


	public List<Chapter> queryPostsByParentId(Long bookId) {
		return this.entityRepo.queryPostsByParentId(bookId, PostTypeEnum.CHAPTER.toString());
	}


	public void updateCommentCount(Long postId, int count) {
		this.entityRepo.updateCommentCountByPostId(postId, count);
	}


	public void updateStarCount(Long postId, int count) {
		this.entityRepo.updateStarCountByPostId(postId, count);
	}


	public void updateLikeCount(Long postId, int count) {
		this.entityRepo.updateLikeCountByPostId(postId, count);
	}

	public MiniPost queryPrevPost(Long pid) {
		return this.entityRepo.queryPrev(pid);
	}

	public MiniPost queryNextPost(Long pid) {
		return this.entityRepo.queryNext(pid);
	}

	public MiniPost queryPrevPost(Long pid, Long termTypeId) {
		return this.entityRepo.queryPrev(pid, termTypeId);
	}

	public MiniPost queryNextPost(Long pid, Long termTypeId) {
		return this.entityRepo.queryNext(pid, termTypeId);
	}

	public MiniPost queryPrevChapter(Long pid, Long parentId) {
		return this.entityRepo.queryPrevChapter(pid, parentId);
	}

	public MiniPost queryNextChapter(Long pid, Long parentId) {
		return this.entityRepo.queryNextChapter(pid, parentId);
	}


	public List<MiniPost> queryRelatedPosts(String postType, String contentType, List<Long> termTypeIds, int num) {
		return this.entityRepo.queryRelatedPosts(postType, contentType, termTypeIds, num);
	}


	public String genPostExcerpt(String html, int length) {
		String content = ObjectUtil.htmlToText(html);
		if (content.length() > length) {
			return content.substring(0, length);
		} else
			return content;
	}


	public void publishPost(AuditPost post) {
		this.entityRepo.changePostStatus(post.getId(), PostStatusEnum.PUBLISH.toString());
	}


	public void offlinePost(AuditPost post) {
		this.entityRepo.changePostStatus(post.getId(), PostStatusEnum.OFFLINE.toString());
	}


	public void trashPost(AuditPost post) {
		this.entityRepo.changePostStatus(post.getId(), PostStatusEnum.TRASH.toString());
	}


	public void draftPost(AuditPost post) {
		this.entityRepo.changePostStatus(post.getId(), PostStatusEnum.DRAFT.toString());
	}
}
