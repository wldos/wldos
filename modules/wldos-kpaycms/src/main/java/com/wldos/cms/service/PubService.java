/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.cms.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.wldos.framework.service.RepoService;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.entity.KStars;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.repo.PubRepo;
import com.wldos.cms.vo.AuditPub;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.DocItem;
import com.wldos.cms.vo.InfoUnit;
import com.wldos.cms.vo.MiniPub;
import com.wldos.cms.vo.PubMember;
import com.wldos.cms.vo.PubType;
import com.wldos.cms.vo.PubUnit;
import com.wldos.cms.vo.SPub;
import com.wldos.common.Constants;
import com.wldos.common.dto.SQLTable;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.AtomicUtils;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.cms.PubOpener;
import com.wldos.support.cms.dto.ContModelDto;
import com.wldos.support.cms.entity.KPubmeta;
import com.wldos.support.cms.model.KModelMetaKey;
import com.wldos.support.region.vo.City;
import com.wldos.support.term.dto.Term;
import com.wldos.support.term.enums.TermTypeEnum;
import com.wldos.sys.base.dto.TermObject;
import com.wldos.sys.base.entity.KTermObject;
import com.wldos.sys.base.enums.PubTypeEnum;
import com.wldos.sys.base.service.TermService;
import com.wldos.sys.core.service.RegionService;
import com.wldos.sys.core.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发布内容service。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PubService extends RepoService<PubRepo, KPubs, Long> implements PubOpener {

	private final UserService userService;

	private final TermService termService;

	private final PubmetaService pubmetaService;

	private final RegionService regionService;

	public PubService(UserService userService, TermService termService, PubmetaService pubmetaService, RegionService regionService) {
		this.userService = userService;
		this.termService = termService;
		this.pubmetaService = pubmetaService;
		this.regionService = regionService;
	}

	/**
	 * 查询内容主体
	 *
	 * @param pid 发布内容id
	 * @return 内容主体
	 */
	public ContModelDto queryContModel(Long pid) {

		return this.entityRepo.queryContModel(pid);
	}

	/**
	 * 指定域查询内容主体
	 *
	 * @param pid 发布内容id
	 * @param domainId 要匹配的域id
	 * @return 内容主体
	 */
	public ContModelDto queryContModel(Long pid, Long domainId) {

		return this.entityRepo.queryContModel(pid, domainId);
	}

	/**
	 * 查询内容附件(文件、图片等附属子实体)
	 *
	 * @param pid 发布内容id
	 * @return 内容列表
	 */
	public List<ContModelDto> queryContAttach(Long pid) {
		return this.entityRepo.queryContSubModel(pid, PubTypeEnum.ATTACHMENT.getName());
	}

	/**
	 * 查询作品列表,需要查询扩展属性时
	 *
	 * @param pageQuery 分页参数
	 * @return 一页数据
	 */
	public PageableResult<PubUnit> queryPubWithExtList(PageQuery pageQuery) {
		PageableResult<PubUnit> pubUnitPage = this.execQueryForPage(PubUnit.class, KPubs.class, KTermObject.class, "k_pubs", "k_term_object", "object_id", pageQuery);

		List<PubUnit> pubUnits = pubUnitPage.getData().getRows();

		if (pubUnits == null || pubUnits.isEmpty())
			return pubUnitPage;

		List<Long> ids = pubUnits.parallelStream().map(PubUnit::getId).collect(Collectors.toList());

		List<KPubmeta> pubMetas = this.queryPubExt(ids);

		Map<Long, List<PubMember>> pubMembers = this.queryUserInfoByBookIds(ids);

		Map<Long, List<KPubmeta>> maps = pubMetas.stream().collect(Collectors.groupingBy(KPubmeta::getPubId));

		AtomicInteger count = AtomicUtils.count(pageQuery);

		pubUnits = pubUnits.stream().map(pub -> {

			pub.setMembers(pubMembers.get(pub.getId()));

			List<KPubmeta> metas = maps.get(pub.getId());
			if (ObjectUtils.isBlank(metas)) { // 有的发布内容没有元数据，可能为空

				// 获取封面URL
				pub.setCover(this.defaultCover(count.getAndIncrement()));

				return pub;
			}

			Map<String, String> meta = metas.stream().collect(Collectors.toMap(KPubmeta::getMetaKey, KPubmeta::getMetaValue, (k1, k2) -> k1));

			// 获取封面URL
			String coverUrl = this.store.getFileUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			pub.setCover(coverUrl);

			pub.setSubTitle(ObjectUtils.string(meta.get(KModelMetaKey.PUB_META_KEY_SUB_TITLE)));

			return pub;
		}).collect(Collectors.toList());

		pubUnitPage.setDataRows(pubUnits);

		return pubUnitPage;
	}

	/**
	 * 查询发布内容列表,也用于查询作品下的章节列表
	 *
	 * @param pageQuery 分页参数
	 * @return 一页数据，仅关注概要信息：id、title、**count、cover、excerpt、tags等
	 */
	public PageableResult<PubUnit> queryArchives(PageQuery pageQuery) {
		PageableResult<PubUnit> pubUnits = this.execQueryForPage(PubUnit.class, KPubs.class, KTermObject.class, "k_pubs", "k_term_object", "object_id", pageQuery);
		return this.handlePubUnit(pubUnits, pageQuery);
	}

	/**
	 * 查询文档列表
	 *
	 * @param pageQuery 查询条件
	 * @return 一页文档列表
	 */
	public PageableResult<DocItem> queryDocList(PageQuery pageQuery) {
		return this.execQueryForPage(DocItem.class, KPubs.class, KTermObject.class, "k_pubs", "k_term_object", "object_id", pageQuery);
	}

	/**
	 * 查询供求信息
	 *
	 * @param pageQuery 分页参数
	 * @return 分页信息列表
	 */
	public PageableResult<InfoUnit> queryInfos(PageQuery pageQuery) {
		String sqlNoWhere = "select p.* from k_pubs p where 1=1 ";
		Map<String, Object> condition = pageQuery.getCondition();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		List<Object> params = new ArrayList<>(); // 参数寄存器

		// 子表动态存在,分类按过滤，标签不分层
		Object tId = condition.get("termTypeId");
		if (!ObjectUtils.isBlank(tId) || (filter != null && filter.containsKey("termTypeId"))) {
			String cAlias = "o";
			String baseExistsSql = this.commonOperate.makeBaseExistsSql("k_term_object", cAlias, "p", "object_id");

			sqlNoWhere += this.commonOperate.existsSql(cAlias, KTermObject.class, baseExistsSql, params, pageQuery);
		}

		// 自定义动态存在
		Object city = condition.get(KModelMetaKey.PUB_META_KEY_CITY);
		if (!ObjectUtils.isBlank(city)) {
			String baseExistsSql = " and exists(select 1 from k_pubmeta m where m.pub_id=p.id and m.meta_key='" + KModelMetaKey.PUB_META_KEY_CITY + "' and m.meta_value=?)";
			params.add(city);
			sqlNoWhere += baseExistsSql;
		}

		Object prov = condition.get(KModelMetaKey.PUB_META_KEY_PROV);
		if (!ObjectUtils.isBlank(prov)) {
			String baseExistsSql = " and exists(select 1 from k_pubmeta m where m.pub_id=p.id and m.meta_key='" + KModelMetaKey.PUB_META_KEY_PROV + "' and m.meta_value=?)";
			params.add(prov);
			sqlNoWhere += baseExistsSql;
		}

		if (condition.containsKey("price")) {
			String baseExistsSql = " and exists(select 1 from k_pubmeta m where m.pub_id=p.id and m.meta_key='" + KModelMetaKey.PUB_META_KEY_ORN_PRICE + "'";
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

		PageableResult<InfoUnit> pubUnits = this.commonOperate.execQueryForPage(InfoUnit.class, sqlNoWhere, pageQuery, SQLTable.of(KPubs.class, "p"));
		return this.handleInfoUnit(pubUnits, pageQuery);
	}

	private PageableResult<InfoUnit> handleInfoUnit(PageableResult<InfoUnit> pubUnitPage, PageQuery pageQuery) {
		List<InfoUnit> pubUnits = pubUnitPage.getData().getRows();

		if (pubUnits == null || pubUnits.isEmpty())
			return pubUnitPage;

		List<Long> ids = pubUnits.parallelStream().map(InfoUnit::getId).collect(Collectors.toList());

		List<KPubmeta> pubMetas = this.queryPubExt(ids);

		Map<Long, PubMember> pubMembers = this.queryUserInfoByPubIds(ids);

		Map<Long, List<KPubmeta>> maps = pubMetas.stream().collect(Collectors.groupingBy(KPubmeta::getPubId));

		// 查询发布内容标签
		List<TermObject> terms = this.termService.findTermByObjectId(ids, TermTypeEnum.TAG.toString());
		Map<Long, List<Term>> termList = terms.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));

		AtomicInteger count = AtomicUtils.count(pageQuery);

		pubUnits = pubUnits.stream().map(pub -> {

			if (ObjectUtils.isBlank(pub.getPubExcerpt())) { // 根据正文生成摘要
				// seo大概140个文字
				pub.setPubExcerpt(
						this.genPubExcerpt(
								pub.getPubContent(), 140));
			}

			pub.setMember(pubMembers.get(pub.getId()));
			// 设置标签
			pub.setTags(termList.get(pub.getId()));

			List<KPubmeta> metas = maps.get(pub.getId());
			if (ObjectUtils.isBlank(metas)) { // 有的发布内容没有元数据，可能为空

				// 获取封面URL
				pub.setCover(this.defaultCover(count.getAndIncrement()));

				return pub;
			}

			Map<String, String> meta = metas.stream().collect(Collectors.toMap(KPubmeta::getMetaKey, KPubmeta::getMetaValue, (k1, k2) -> k1));

			// 获取封面URL
			String coverUrl = this.store.getFileUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			pub.setCover(coverUrl);

			pub.setOrnPrice(new BigDecimal(ObjectUtils.nvlToZero(meta.get(KModelMetaKey.PUB_META_KEY_ORN_PRICE))));

			String city = meta.get(KModelMetaKey.PUB_META_KEY_CITY);
			if (!ObjectUtils.isBlank(city)) {
				City region = this.regionService.queryRegionInfoByCode(city);
				pub.setCity(region.getName());
				pub.setProv(region.getProvName());
			}

			return pub;
		}).collect(Collectors.toList());

		pubUnitPage.setDataRows(pubUnits);

		return pubUnitPage;
	}

	private PageableResult<PubUnit> handlePubUnit(PageableResult<PubUnit> pubUnitPage, PageQuery pageQuery) {
		List<PubUnit> pubUnits = pubUnitPage.getData().getRows();

		if (pubUnits == null || pubUnits.isEmpty())
			return pubUnitPage;

		List<Long> ids = pubUnits.parallelStream().map(PubUnit::getId).collect(Collectors.toList());

		List<KPubmeta> pubMetas = this.queryPubExt(ids);

		Map<Long, PubMember> pubMembers = this.queryUserInfoByPubIds(ids);

		Map<Long, List<KPubmeta>> maps = pubMetas.stream().collect(Collectors.groupingBy(KPubmeta::getPubId));

		// 查询发布内容标签
		List<TermObject> terms = this.termService.findTermByObjectId(ids, TermTypeEnum.TAG.toString());
		Map<Long, List<Term>> termList = terms.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));

		AtomicInteger count = AtomicUtils.count(pageQuery);

		pubUnits = pubUnits.stream().map(pub -> {

			if (ObjectUtils.isBlank(pub.getPubExcerpt())) { // 根据正文生成摘要
				// seo大概140个文字
				pub.setPubExcerpt(
						this.genPubExcerpt(
								pub.getPubContent(), 140));
			}

			pub.setMember(pubMembers.get(pub.getId()));
			// 设置标签
			pub.setTags(termList.get(pub.getId()));

			List<KPubmeta> metas = maps.get(pub.getId());
			if (ObjectUtils.isBlank(metas)) { // 有的发布内容没有元数据，可能为空

				// 获取封面URL
				pub.setCover(this.defaultCover(count.getAndIncrement()));

				return pub;
			}

			Map<String, String> meta = metas.stream().collect(Collectors.toMap(KPubmeta::getMetaKey, KPubmeta::getMetaValue, (k1, k2) -> k1));

			// 获取封面URL
			String coverUrl = this.store.getFileUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			pub.setCover(coverUrl);

			return pub;
		}).collect(Collectors.toList());

		pubUnitPage.setDataRows(pubUnits);

		return pubUnitPage;
	}

	/**
	 * 通过分站查询分站内容
	 *
	 * @param domainId 分站域名Id
	 * @param pageQuery 查询参数
	 * @return 业务对象列表，仅关注概要信息：id、title、createBy、termTypeIds、tags、updateTime、**count等
	 */
	public PageableResult<AuditPub> queryAdminPubs(Long domainId, PageQuery pageQuery) {

		// 域隔离
		pageQuery.appendParam(Constants.COMMON_KEY_DOMAIN_COLUMN, domainId);

		PageableResult<AuditPub> pubUnitPage = this.execQueryForPage(AuditPub.class, KPubs.class, KTermObject.class, "k_pubs", "k_term_object", "object_id", pageQuery);

		List<AuditPub> pubUnits = pubUnitPage.getData().getRows();

		if (pubUnits == null || pubUnits.isEmpty())
			return pubUnitPage;

		List<Long> ids = pubUnits.parallelStream().map(AuditPub::getId).collect(Collectors.toList());

		Map<Long, PubMember> pubMembers = this.queryUserInfoByPubIds(ids);

		// 查询发布内容标签
		List<TermObject> terms = this.termService.findTermByObjectId(ids, TermTypeEnum.TAG.toString());
		Map<Long, List<Term>> termList = terms.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));
		// 查询分类
		List<TermObject> termTypes = this.termService.findTermByObjectId(ids, TermTypeEnum.CATEGORY.toString());
		Map<Long, List<Term>> termTypeList = termTypes.parallelStream().collect(Collectors.toMap(TermObject::getId, TermObject::getTerms));

		// 查询浏览数
		List<KPubmeta> pubMetas = this.pubmetaService.queryAllByPubIdInAndMetaKey(ids, KModelMetaKey.PUB_META_KEY_VIEWS);
		Map<Long, String> viewsMap = pubMetas.parallelStream().collect(Collectors.toMap(KPubmeta::getPubId, KPubmeta::getMetaValue));

		pubUnits = pubUnits.stream().peek(pub -> {
			Long id = pub.getId();
			pub.setMember(pubMembers.get(id));

			// 设置标签
			pub.setTags(termList.get(id));
			pub.setTerms(termTypeList.get(id));

			pub.setViews(viewsMap.get(id));
		}).collect(Collectors.toList());

		pubUnitPage.setDataRows(pubUnits);

		return pubUnitPage;
	}

	/**
	 * 全文检索
	 *
	 * @param domainId 分站域名Id
	 * @param pageQuery 查询参数
	 * @return 检索结果集
	 */
	public PageableResult<SPub> searchPubs(Long domainId, PageQuery pageQuery, String keywords) {

		String sql = "select s.id, s.pub_title, s.pub_type from k_pubs s where s.delete_flag = 'normal' and s.pub_status in ('publish', 'inherit')  and (instr(s.pub_title, ?)>0 or instr(s.pub_content, ?) >0) and domain_id=?";

		return this.commonOperate.execQueryForPageNoOrder(SPub.class, sql, pageQuery.getCurrent(), pageQuery.getPageSize(), keywords, keywords, domainId);
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

	public PageableResult<PubUnit> queryArchivesUser(PageQuery pageQuery) {
		PageableResult<PubUnit> pubUnitPage = this.execQueryForPage(PubUnit.class, KPubs.class, KStars.class, "k_pubs", "k_stars", "object_id", pageQuery);

		return this.handlePubUnit(pubUnitPage, pageQuery);
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
	 * @param ids 批量发布内容id
	 * @return 批量发布内容元数据
	 */
	public List<KPubmeta> queryPubExt(List<Long> ids) {
		return this.entityRepo.queryPubExt(ids);
	}

	/**
	 * 查询作品作者
	 *
	 * @param ids 批量作品id
	 * @return 发布内容贡献值集合
	 */
	public Map<Long, List<PubMember>> queryUserInfoByBookIds(List<Long> ids) {
		List<PubMember> pubMembers = this.entityRepo.queryMembersByPubIds(ids);
		pubMembers = pubMembers.parallelStream().peek(pm -> pm.setAvatar(this.userService.getAvatarUrl(pm.getAvatar()))).collect(Collectors.toList());
		return pubMembers.stream().collect(Collectors.groupingBy(PubMember::getPubId));
	}

	/**
	 * 查询发布内容作者
	 *
	 * @param ids 批量作品id
	 * @return 发布内容贡献值集合
	 */
	public Map<Long, PubMember> queryUserInfoByPubIds(List<Long> ids) {
		List<PubMember> pubMembers = this.entityRepo.queryMembersByPubIds(ids);
		pubMembers = pubMembers.parallelStream().peek(pm -> pm.setAvatar(this.userService.getAvatarUrl(pm.getAvatar()))).collect(Collectors.toList());
		return pubMembers.stream().collect(Collectors.toMap(PubMember::getPubId, pm -> pm, (p1, p2) -> p1));
	}

	/**
	 * 按发布内容id查询作者信息
	 *
	 * @param pubId 发布内容id
	 * @return 发布内容作者
	 */
	public PubMember queryMemberByPubId(Long pubId) {
		PubMember member = this.entityRepo.queryMemberByPubId(pubId);
		member.setAvatar(this.userService.getAvatarUrl(member.getAvatar()));
		return member;
	}

	/**
	 * 根据id批量查询所有发布内容
	 *
	 * @param pubIds 发布内容列表
	 * @return 发布内容列表
	 */
	public List<KPubs> queryPubsByIds(List<Long> pubIds) {
		return this.entityRepo.findAllByIdIn(pubIds);
	}

	public List<Chapter> queryChapterBySinglePubId(Long singleId) {
		KPubs single = this.findById(singleId);
		Chapter singleChapter = Chapter.of(single);
		List<Chapter> list = new ArrayList<>();
		list.add(singleChapter);
		return list;
	}

	/**
	 * 查询章节
	 *
	 * @param bookId 作品id
	 * @param deleteFlag 是否删除：见DeleteFlagEnum
	 * @return 章节实体
	 */
	public List<Chapter> queryChapterByParentId(Long bookId, String deleteFlag) {
		return this.entityRepo.queryChapterByParentId(bookId, PubTypeEnum.CHAPTER.getName(), deleteFlag);
	}

	/**
	 * 查询文档章节
	 *
	 * @param bookId 文档id
	 * @param deleteFlag 是否删除：见DeleteFlagEnum
	 * @return 章节实体
	 */
	public List<DocItem> queryDocItemByParentId(Long bookId, String deleteFlag) {
		return this.entityRepo.queryDocItemByParentId(bookId, PubTypeEnum.CHAPTER.getName(), deleteFlag);
	}

	/**
	 * 更新发布内容评论数
	 *
	 * @param pubId 发布内容id
	 * @param count 增幅
	 */
	public void updateCommentCount(Long pubId, int count) {
		this.entityRepo.updateCommentCountByPubId(pubId, count);
	}

	/**
	 * 更新发布内容点赞数
	 *
	 * @param pubId 发布内容id
	 * @param count 增幅
	 */
	public void updateStarCount(Long pubId, int count) {
		this.entityRepo.updateStarCountByPubId(pubId, count);
	}

	/**
	 * 更新发布内容关注数
	 *
	 * @param pubId 发布内容id
	 * @param count 增幅
	 */
	public void updateLikeCount(Long pubId, int count) {
		this.entityRepo.updateLikeCountByPubId(pubId, count);
	}

	/**
	 * 更新发布内容查看数
	 *
	 * @param pubId 发布内容id
	 * @param views 当前查看数
	 */
	public void updateViews(Long pubId, int views) {
		this.entityRepo.updateViewsByPubId(pubId, views);
	}

	public MiniPub queryPrevPub(Long pid) {
		return this.entityRepo.queryPrev(pid);
	}

	public MiniPub queryNextPub(Long pid) {
		return this.entityRepo.queryNext(pid);
	}

	public MiniPub queryPrevPub(Long pid, Long termTypeId) {
		return this.entityRepo.queryPrev(pid, termTypeId);
	}

	public MiniPub queryNextPub(Long pid, Long termTypeId) {
		return this.entityRepo.queryNext(pid, termTypeId);
	}

	public MiniPub queryPrevChapter(Long pid, Long parentId) {
		return this.entityRepo.queryPrevChapter(pid, parentId);
	}

	public MiniPub queryNextChapter(Long pid, Long parentId) {
		return this.entityRepo.queryNextChapter(pid, parentId);
	}

	/**
	 * 在指定标签或分类范围内查询相关发布内容
	 *
	 * @param pubType 内容发布类型
	 * @param domainId 域id
	 * @param termTypeIds 标签集、分类集等
	 * @param num 查询数量
	 * @return 相关发布内容
	 */
	public List<MiniPub> queryRelatedPubs(String pubType, Long domainId, List<Long> termTypeIds, int num) {
		return this.entityRepo.queryRelatedPubs(pubType, domainId, termTypeIds, num);
	}

	/**
	 * 根据发布内容富文本html内容和长度生成发布内容摘要
	 *
	 * @param html 发布内容html内容
	 * @param length 长度，seo描述一般140
	 * @return 摘要信息
	 */
	public String genPubExcerpt(String html, int length) {
		String content = ObjectUtils.htmlToText(html).replaceAll("\\s*", "");
		if (content.length() > length) {
			return content.substring(0, length);
		}
		else
			return content;
	}

	/**
	 * 发布元素，元素只有继承作品发布状态，没有独立的发布状态
	 *
	 * @param pId 发布内容id
	 * @param pubType 发布类型
	 */
	public void publishPub(Long pId, String pubType) {
		this.entityRepo.changePubStatus(pId, PubTypeEnum.isSub(pubType) ? PubStatusEnum.INHERIT.toString() : PubStatusEnum.PUBLISH.toString());
	}

	/**
	 * 下线发布内容,驳回整改
	 *
	 * @param pub 发布内容
	 */
	public void offlinePub(AuditPub pub) {
		this.entityRepo.changePubStatus(pub.getId(), PubStatusEnum.OFFLINE.toString());
	}

	/**
	 * 回收站发布内容，个人发布内容维护
	 *
	 * @param pub 发布内容
	 */
	public void trashPub(AuditPub pub) {
		this.entityRepo.changePubStatus(pub.getId(), PubStatusEnum.TRASH.toString());
	}

	/**
	 * 草稿发布内容，个人发布内容维护
	 *
	 * @param pub 发布内容
	 */
	public void draftPub(AuditPub pub) {
		this.entityRepo.changePubStatus(pub.getId(), PubStatusEnum.DRAFT.toString());
	}

	/**
	 * 别名已存在，则自动追加1，直到在同域内找到不重复的别名返回
	 *
	 * @param domainId 域名id
	 * @param pubName 用户输入别名
	 * @param pubId 发布内容id
	 * @return 不重复的别名
	 */
	public String existsAutoDiffPubName(Long domainId, String pubName, Long pubId) {
		// 先判断当前记录是否没有修改别名，避免执行后面的性能消耗
		if (this.entityRepo.existsPubNameByNameAndId(pubName, pubId))
			return pubName;
		if (this.entityRepo.existsDifPubByNameAndId(domainId, pubName, pubId))
			// 存在，自动加1再判断
			return existsAutoDiffPubName(domainId, pubName + "1", pubId);
		return pubName;
	}

	public boolean pubNameIsNull(Long pubId) {
		return this.entityRepo.pubNameIsNull(pubId);
	}

	public PubType queryPubTypeByPubName(Long domainId, String pubName, String deleteFlag) {
		return this.entityRepo.queryPubTypeByPubName(domainId, pubName, deleteFlag);
	}

	public boolean existsByIdAndPubStatusAndDeleteFlag(Long id, String pubStatus, String deleteFlag) {
		return this.entityRepo.existsByIdAndPubStatusAndDeleteFlag(id, pubStatus, deleteFlag);
	}
}
