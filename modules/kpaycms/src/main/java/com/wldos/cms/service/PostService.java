/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.wldos.common.dto.SQLTable;
import com.wldos.base.service.BaseService;
import com.wldos.cms.dto.ContModelDto;
import com.wldos.cms.entity.KPostmeta;
import com.wldos.cms.entity.KPosts;
import com.wldos.cms.entity.KStars;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.sys.base.enums.ContModelTypeEnum;
import com.wldos.cms.model.KModelMetaKey;
import com.wldos.cms.repo.PostRepo;
import com.wldos.cms.vo.AuditPost;
import com.wldos.cms.vo.BookUnit;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.MiniPost;
import com.wldos.cms.vo.PostMember;
import com.wldos.cms.vo.PostUnit;
import com.wldos.cms.vo.InfoUnit;
import com.wldos.cms.vo.SPost;
import com.wldos.common.Constants;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.AtomicUtils;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;
import com.wldos.sys.base.dto.Term;
import com.wldos.sys.base.dto.TermObject;
import com.wldos.sys.base.entity.KTermObject;
import com.wldos.sys.base.enums.TermTypeEnum;
import com.wldos.sys.base.service.DomainService;
import com.wldos.sys.core.service.RegionService;
import com.wldos.sys.base.service.TermService;
import com.wldos.sys.core.service.UserService;
import com.wldos.sys.core.vo.City;
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

	private final UserService userService;

	private final TermService termService;

	private final PostmetaService postmetaService;

	private final DomainService domainService;

	private final RegionService regionService;

	public PostService(UserService userService, TermService termService, PostmetaService postmetaService, DomainService domainService, RegionService regionService) {
		this.userService = userService;
		this.termService = termService;
		this.postmetaService = postmetaService;
		this.domainService = domainService;
		this.regionService = regionService;
	}

	/**
	 * 查询内容主体
	 *
	 * @param pid 帖子id
	 * @return 内容主体
	 */
	public ContModelDto queryContModel(Long pid) {

		return this.entityRepo.queryContModel(pid);
	}

	/**
	 * 查询内容附件(文件、图片等附属子实体)
	 *
	 * @param pid 帖子id
	 * @return 内容列表
	 */
	public List<ContModelDto> queryContAttach(Long pid) {
		return this.entityRepo.queryContSubModel(pid, ContModelTypeEnum.ATTACHMENT.toString());
	}

	/**
	 * 查询作品列表,需要查询扩展属性时
	 *
	 * @param pageQuery 分页参数
	 * @return 一页数据
	 */
	public PageableResult<BookUnit> queryPostWithExtList(PageQuery pageQuery) {
		PageableResult<BookUnit> postUnitPage = this.execQueryForPage(BookUnit.class, KPosts.class, KTermObject.class, "k_posts", "k_term_object", "object_id", pageQuery);

		List<BookUnit> postUnits = postUnitPage.getData().getRows();

		if (postUnits == null || postUnits.isEmpty())
			return postUnitPage;

		List<Long> ids = postUnits.parallelStream().map(BookUnit::getId).collect(Collectors.toList());

		List<KPostmeta> postMetas = this.queryPostExt(ids);

		Map<Long, List<PostMember>> postMembers = this.queryUserInfoByBookIds(ids);

		Map<Long, List<KPostmeta>> maps = postMetas.stream().collect(Collectors.groupingBy(KPostmeta::getPostId));

		AtomicInteger count = AtomicUtils.count(pageQuery);

		postUnits = postUnits.stream().map(post -> {

			post.setMembers(postMembers.get(post.getId()));

			List<KPostmeta> metas = maps.get(post.getId());
			if (ObjectUtils.isBlank(metas)) { // 有的帖子没有元数据，可能为空

				// 获取封面URL
				post.setCover(this.defaultCover(count.getAndIncrement()));

				return post;
			}

			Map<String, String> meta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

			// 获取封面URL
			String coverUrl = this.store.getFileUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			post.setCover(coverUrl);

			post.setSubTitle(ObjectUtils.string(meta.get(KModelMetaKey.PUB_META_KEY_SUB_TITLE)));

			return post;
		}).collect(Collectors.toList());

		postUnitPage.setDataRows(postUnits);

		return postUnitPage;
	}

	/**
	 * 查询帖子列表,也用于查询作品下的章节列表
	 *
	 * @param pageQuery 分页参数
	 * @return 一页数据，仅关注概要信息：id、title、**count、cover、excerpt、tags等
	 */
	public PageableResult<PostUnit> queryArchives(PageQuery pageQuery) {
		PageableResult<PostUnit> postUnits = this.execQueryForPage(PostUnit.class, KPosts.class, KTermObject.class, "k_posts", "k_term_object", "object_id", pageQuery);
		return this.handlePostUnit(postUnits, pageQuery);
	}

	/**
	 * 查询供求信息
	 *
	 * @param pageQuery 分页参数
	 * @return 分页信息列表
	 */
	public PageableResult<InfoUnit> queryInfos(PageQuery pageQuery) {
		String sqlNoWhere = "select p.* from k_posts p where 1=1 ";
		Map<String, Object> condition = pageQuery.getCondition();
		Map<String,List<Object>> filter = pageQuery.getFilter();
		List<Object> params = new ArrayList<>(); // 参数寄存器

		// 子表动态存在,分类按过滤，标签不分层
		if (condition.containsKey("termTypeId") || (filter != null && filter.containsKey("termTypeId"))) {
			String cAlias = "o";
			String baseExistsSql = this.commonOperate.makeBaseExistsSql("k_term_object", cAlias, "p", "object_id");

			sqlNoWhere += this.commonOperate.existsSql(cAlias, KTermObject.class, baseExistsSql, params, pageQuery);
		}

		// 自定义动态存在
		if (condition.containsKey(KModelMetaKey.PUB_META_KEY_CITY)) {
			String baseExistsSql = " and exists(select 1 from k_postmeta m where m.post_id=p.id and m.meta_key='"+KModelMetaKey.PUB_META_KEY_CITY + "' and m.meta_value=?)";
			params.add(condition.get(KModelMetaKey.PUB_META_KEY_CITY));
			sqlNoWhere += baseExistsSql;
		}

		if (condition.containsKey("price")) {
			String baseExistsSql = " and exists(select 1 from k_postmeta m where m.post_id=p.id and m.meta_key='"+KModelMetaKey.PUB_META_KEY_ORN_PRICE + "'";
			Object price = condition.get("price");
			String[] prices = price.toString().split(",");
			if (!prices[0].equals("0")) {
				baseExistsSql += " and m.meta_value + 0 >= ?";
				params.add(prices[0]);
			}

			if (!prices[1].equals("0")) {
				baseExistsSql += " and m.meta_value + 0 <= ?";
				params.add(prices[1]);
			}
			sqlNoWhere += baseExistsSql + ")";
		}

		PageableResult<InfoUnit> postUnits = this.commonOperate.execQueryForPage(InfoUnit.class, sqlNoWhere, pageQuery,
				new SQLTable[]{new SQLTable("k_posts", "p", KPosts.class)}, params);
		return this.handleInfoUnit(postUnits, pageQuery);
	}

	private PageableResult<InfoUnit> handleInfoUnit(PageableResult<InfoUnit> postUnitPage, PageQuery pageQuery) {
		List<InfoUnit> postUnits = postUnitPage.getData().getRows();

		if (postUnits == null || postUnits.isEmpty())
			return postUnitPage;

		List<Long> ids = postUnits.parallelStream().map(InfoUnit::getId).collect(Collectors.toList());

		List<KPostmeta> postMetas = this.queryPostExt(ids);

		Map<Long, PostMember> postMembers = this.queryUserInfoByPostIds(ids);

		Map<Long, List<KPostmeta>> maps = postMetas.stream().collect(Collectors.groupingBy(KPostmeta::getPostId));

		// 查询帖子标签
		List<TermObject> terms = this.termService.findTermByObjectId(ids, TermTypeEnum.TAG.toString());
		Map<Long, List<Term>> termList = terms.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));

		AtomicInteger count = AtomicUtils.count(pageQuery);

		postUnits = postUnits.stream().map(post -> {

			if (ObjectUtils.isBlank(post.getPostExcerpt())) { // 根据正文生成摘要
				// seo大概140个文字
				post.setPostExcerpt(
						this.genPostExcerpt(
								post.getPostContent(), 140));
			}

			post.setMember(postMembers.get(post.getId()));
			// 设置标签
			post.setTags(termList.get(post.getId()));

			List<KPostmeta> metas = maps.get(post.getId());
			if (ObjectUtils.isBlank(metas)) { // 有的帖子没有元数据，可能为空

				// 获取封面URL
				post.setCover(this.defaultCover(count.getAndIncrement()));

				return post;
			}

			Map<String, String> meta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

			// 获取封面URL
			String coverUrl = this.store.getFileUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			post.setCover(coverUrl);

			post.setOrnPrice(new BigDecimal(ObjectUtils.nvlToZero(meta.get(KModelMetaKey.PUB_META_KEY_ORN_PRICE))));

			String city = meta.get(KModelMetaKey.PUB_META_KEY_CITY);
			if (!ObjectUtils.isBlank(city)) {
				City region = this.regionService.queryRegionInfoByCode(city);
				post.setCity(region.getName());
				post.setProv(region.getProvName());
			}

			return post;
		}).collect(Collectors.toList());

		postUnitPage.setDataRows(postUnits);

		return postUnitPage;
	}

	private PageableResult<PostUnit> handlePostUnit(PageableResult<PostUnit> postUnitPage, PageQuery pageQuery) {
		List<PostUnit> postUnits = postUnitPage.getData().getRows();

		if (postUnits == null || postUnits.isEmpty())
			return postUnitPage;

		List<Long> ids = postUnits.parallelStream().map(PostUnit::getId).collect(Collectors.toList());

		List<KPostmeta> postMetas = this.queryPostExt(ids);

		Map<Long, PostMember> postMembers = this.queryUserInfoByPostIds(ids);

		Map<Long, List<KPostmeta>> maps = postMetas.stream().collect(Collectors.groupingBy(KPostmeta::getPostId));

		// 查询帖子标签
		List<TermObject> terms = this.termService.findTermByObjectId(ids, TermTypeEnum.TAG.toString());
		Map<Long, List<Term>> termList = terms.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));

		AtomicInteger count = AtomicUtils.count(pageQuery);

		postUnits = postUnits.stream().map(post -> {

			if (ObjectUtils.isBlank(post.getPostExcerpt())) { // 根据正文生成摘要
				// seo大概140个文字
				post.setPostExcerpt(
						this.genPostExcerpt(
								post.getPostContent(), 140));
			}

			post.setMember(postMembers.get(post.getId()));
			// 设置标签
			post.setTags(termList.get(post.getId()));

			List<KPostmeta> metas = maps.get(post.getId());
			if (ObjectUtils.isBlank(metas)) { // 有的帖子没有元数据，可能为空

				// 获取封面URL
				post.setCover(this.defaultCover(count.getAndIncrement()));

				return post;
			}

			Map<String, String> meta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

			// 获取封面URL
			String coverUrl = this.store.getFileUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			post.setCover(coverUrl);

			return post;
		}).collect(Collectors.toList());

		postUnitPage.setDataRows(postUnits);

		return postUnitPage;
	}

	/**
	 * 通过分站绑定的业务类型查询分站内容
	 *
	 * @param domain 分站域名
	 * @param pageQuery 查询参数
	 * @return 业务对象列表，仅关注概要信息：id、title、createBy、termTypeIds、tags、updateTime、**count等
	 */
	public PageableResult<AuditPost> queryAdminPosts(String domain, PageQuery pageQuery) {
		Long domainId = this.domainService.queryIdByDomain(domain);

		// 域隔离
		pageQuery.appendParam(Constants.COMMON_KEY_DOMAIN_COLUMN, domainId);

		PageableResult<AuditPost> postUnitPage = this.execQueryForPage(AuditPost.class, KPosts.class, KTermObject.class, "k_posts", "k_term_object", "object_id", pageQuery);

		List<AuditPost> postUnits = postUnitPage.getData().getRows();

		if (postUnits == null || postUnits.isEmpty())
			return postUnitPage;

		List<Long> ids = postUnits.parallelStream().map(AuditPost::getId).collect(Collectors.toList());

		Map<Long, PostMember> postMembers = this.queryUserInfoByPostIds(ids);

		// 查询帖子标签
		List<TermObject> terms = this.termService.findTermByObjectId(ids, TermTypeEnum.TAG.toString());
		Map<Long, List<Term>> termList = terms.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));
		// 查询分类
		List<TermObject> termTypes = this.termService.findTermByObjectId(ids, TermTypeEnum.CATEGORY.toString());
		Map<Long, List<Term>> termTypeList = termTypes.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));

		// 查询浏览数
		List<KPostmeta> postMetas = this.postmetaService.queryAllByPostIdInAndMetaKey(ids, KModelMetaKey.PUB_META_KEY_VIEWS);
		Map<Long, String> viewsMap = postMetas.parallelStream().collect(Collectors.toMap(KPostmeta::getPostId, KPostmeta::getMetaValue));

		postUnits = postUnits.stream().map(post -> {
			Long id = post.getId();
			post.setMember(postMembers.get(id));

			// 设置标签
			post.setTags(termList.get(id));
			post.setTerms(termTypeList.get(id));

			post.setViews(viewsMap.get(id));
			return post;
		}).collect(Collectors.toList());

		postUnitPage.setDataRows(postUnits);

		return postUnitPage;
	}

	/**
	 * 全文检索
	 *
	 * @param domain 分站域名
	 * @param pageQuery 查询参数
	 * @return 检索结果集
	 */
	public PageableResult<SPost> searchPosts(String domain, PageQuery pageQuery) {
		Long domainId = this.domainService.queryIdByDomain(domain);

		// 域隔离
		pageQuery.appendParam(Constants.COMMON_KEY_DOMAIN_COLUMN, domainId);

		return this.execQueryForPage(SPost.class, KPosts.class, KTermObject.class, "k_posts", "k_term_object", "object_id", pageQuery);
	}

	/**
	 * 填充分类过滤器
	 *
	 * @param termTypeIds 分类列表
	 * @param pageQuery 查询参数
	 */
	private void filterByParentTermTypeId(List<Object> termTypeIds, PageQuery pageQuery) {
		// 查询分类及其子分类
		pageQuery.pushFilter("termTypeId", termTypeIds);
	}

	public PageableResult<PostUnit> queryArchivesUser(PageQuery pageQuery) {
		PageableResult<PostUnit> postUnitPage = this.execQueryForPage(PostUnit.class, KPosts.class, KStars.class, "k_posts", "k_stars", "object_id", pageQuery);

		return this.handlePostUnit(postUnitPage, pageQuery);
	}

	/**
	 * 封面特色图，在符合系统整体审美体系的条件下，用户可以有限制地定制, 完整的封面包含背景图和内容信息(副标题、时间、作者等)，此处为背景图
	 *
	 * @param count 正序原子整数，请使用AtomicInteger count = new AtomicInteger(0); count.getAndIncrement()作为参数
	 * @return 默认封面
	 */
	public String defaultCover(int count) {
		// 默认封面，每行4张 @todo 可以定制个性默认封面，后续形成封面设计业态
		String[] covers = {
				"/sdfkjiwenfgldfgilq.jpg",
				"/iZBVOIhGJiAnhplqjvZW.jpg",
				"/iXjVmWVHbCJAyqvDxdtx.jpg",
				"/gLaIAoVWTtLbBWZNYEMg.jpg"
		};

		int coverNum = covers.length;
		// 偶数行正序使用封面，奇数行倒序使用封面
		return this.store.getFileUrl(((count / coverNum) % 2 == 0 ? covers[count % coverNum] : covers[(coverNum - 1) - (count % coverNum)]), "");
	}

	/**
	 * 批量查询扩展属性
	 *
	 * @param ids 批量帖子id
	 * @return 批量帖子元数据
	 */
	public List<KPostmeta> queryPostExt(List<Long> ids) {
		return this.entityRepo.queryPostExt(ids);
	}

	/**
	 * 查询作品作者
	 *
	 * @param ids 批量作品id
	 * @return 帖子贡献值集合
	 */
	public Map<Long, List<PostMember>> queryUserInfoByBookIds(List<Long> ids) {
		List<PostMember> postMembers = this.entityRepo.queryMembersByPostIds(ids);
		postMembers = postMembers.parallelStream().map(pm -> {
			pm.setAvatar(this.userService.getAvatarUrl(pm.getAvatar()));
			return pm;
		}).collect(Collectors.toList());
		return postMembers.stream().collect(Collectors.groupingBy(PostMember::getPostId));
	}

	/**
	 * 查询帖子作者
	 *
	 * @param ids 批量作品id
	 * @return 帖子贡献值集合
	 */
	public Map<Long, PostMember> queryUserInfoByPostIds(List<Long> ids) {
		List<PostMember> postMembers = this.entityRepo.queryMembersByPostIds(ids);
		postMembers = postMembers.parallelStream().map(pm -> {
			pm.setAvatar(this.userService.getAvatarUrl(pm.getAvatar()));
			return pm;
		}).collect(Collectors.toList());
		return postMembers.stream().collect(Collectors.toMap(PostMember::getPostId, pm -> pm, (p1, p2) -> p1));
	}

	/**
	 * 按帖子id查询作者信息
	 *
	 * @param postId 帖子id
	 * @return 帖子作者
	 */
	public PostMember queryMemberByPostId(Long postId) {
		PostMember member = this.entityRepo.queryMemberByPostId(postId);
		member.setAvatar(this.userService.getAvatarUrl(member.getAvatar()));
		return member;
	}

	/**
	 * 查询章节
	 *
	 * @param bookId 作品id
	 * @param deleteFlag 是否删除：见DeleteFlagEnum
	 * @return 章节实体
	 */
	public List<Chapter> queryPostsByParentId(Long bookId, String deleteFlag) {
		return this.entityRepo.queryPostsByParentId(bookId, ContModelTypeEnum.CHAPTER.toString(), deleteFlag);
	}

	/**
	 * 更新帖子评论数
	 *
	 * @param postId 帖子id
	 * @param count 增幅
	 */
	public void updateCommentCount(Long postId, int count) {
		this.entityRepo.updateCommentCountByPostId(postId, count);
	}

	/**
	 * 更新帖子点赞数
	 *
	 * @param postId 帖子id
	 * @param count 增幅
	 */
	public void updateStarCount(Long postId, int count) {
		this.entityRepo.updateStarCountByPostId(postId, count);
	}

	/**
	 * 更新帖子关注数
	 *
	 * @param postId 帖子id
	 * @param count 增幅
	 */
	public void updateLikeCount(Long postId, int count) {
		this.entityRepo.updateLikeCountByPostId(postId, count);
	}

	/**
	 * 更新帖子查看数
	 *
	 * @param postId 帖子id
	 * @param views 当前查看数
	 */
	public void updateViews(Long postId, int views) {
		this.entityRepo.updateViewsByPostId(postId, views);
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

	/**
	 * 在指定标签或分类范围内查询相关帖子
	 * 章节和作品集一样需要设置分类
	 *
	 * @param postType 帖子展现类型：作品集、章节、附件、页面等，表现形式不同
	 * @param contentType 业务大类：文章、年谱、菜谱等业务划分，各大类属于不同领域（业务不同）
	 * @param termTypeIds 若干标签、分类等
	 * @param num 查询数量
	 * @return 相关帖子
	 */
	public List<MiniPost> queryRelatedPosts(String postType, String contentType, List<Long> termTypeIds, int num) {
		return this.entityRepo.queryRelatedPosts(postType, contentType, termTypeIds, num);
	}

	/**
	 * 根据帖子富文本html内容和长度生成帖子摘要
	 *
	 * @param html 帖子html内容
	 * @param length 长度，seo描述一般140
	 * @return 摘要信息
	 */
	public String genPostExcerpt(String html, int length) {
		String content = ObjectUtils.htmlToText(html).replaceAll("\\s*", "");
		if (content.length() > length) {
			return content.substring(0, length);
		}
		else
			return content;
	}

	/**
	 * 发布帖子
	 *
	 * @param post 帖子
	 */
	public void publishPost(AuditPost post) {
		this.entityRepo.changePostStatus(post.getId(), PostStatusEnum.PUBLISH.toString());
	}

	/**
	 * 下线帖子,驳回整改
	 *
	 * @param post 帖子
	 */
	public void offlinePost(AuditPost post) {
		this.entityRepo.changePostStatus(post.getId(), PostStatusEnum.OFFLINE.toString());
	}

	/**
	 * 回收站帖子，个人帖子维护
	 *
	 * @param post 帖子
	 */
	public void trashPost(AuditPost post) {
		this.entityRepo.changePostStatus(post.getId(), PostStatusEnum.TRASH.toString());
	}

	/**
	 * 草稿帖子，个人帖子维护
	 *
	 * @param post 帖子
	 */
	public void draftPost(AuditPost post) {
		this.entityRepo.changePostStatus(post.getId(), PostStatusEnum.DRAFT.toString());
	}

	public boolean existsByPostName(String postName, Long postId) {
		return this.entityRepo.existsByPostNameAndId(postName, postId);
	}
}
