/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.wldos.cms.dao.PubAssociationPickerDao;
import com.wldos.cms.dao.PubDao;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.entity.KStars;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.vo.AuditPub;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.DocItem;
import com.wldos.cms.vo.InfoUnit;
import com.wldos.cms.vo.MiniPub;
import com.wldos.cms.vo.PubMember;
import com.wldos.cms.vo.PubType;
import com.wldos.cms.vo.PubUnit;
import com.wldos.cms.vo.SPub;
import com.wldos.cms.vo.WorkbenchListScope;
import com.wldos.framework.mvc.service.EntityService;
import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.dto.SQLTable;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.utils.AtomicUtils;
import io.github.wldos.common.utils.ObjectUtils;
import com.wldos.platform.core.dto.TermObject;
import com.wldos.platform.core.entity.KTermObject;
import com.wldos.platform.core.service.TermService;
import io.github.wldos.platform.support.cms.PubOpener;
import io.github.wldos.platform.asset.CmsTemplateOfflineCallback;
import io.github.wldos.platform.asset.CmsTemplatePublishedCallback;
import io.github.wldos.platform.support.cms.dto.ContModelDto;
import io.github.wldos.platform.support.cms.entity.KPubmeta;
import io.github.wldos.platform.support.cms.model.KModelMetaKey;
import com.wldos.cms.query.CmsKpubsDiscoverySql;
import com.wldos.cms.search.CmsSearchIndexSyncService;
import io.github.wldos.platform.support.region.vo.City;
import io.github.wldos.platform.support.term.dto.Term;
import io.github.wldos.platform.support.term.enums.TermTypeEnum;
import com.wldos.platform.core.enums.PubTypeEnum;
import com.wldos.platform.core.service.RegionService;
import com.wldos.platform.core.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发布内容service。
 *
 * @author Yuanxi Universe
 * @date 2021/6/17
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PubService extends EntityService<PubDao, KPubs, Long> implements PubOpener {

	private final UserService userService;

	private final TermService termService;

	private final PubmetaService pubmetaService;

	private final RegionService regionService;

	private final ObjectProvider<CmsTemplatePublishedCallback> cmsTemplatePublishedCallback;

	private final ObjectProvider<CmsTemplateOfflineCallback> cmsTemplateOfflineCallback;

	private final ObjectProvider<CmsSearchIndexSyncService> cmsSearchIndexSyncService;

    private final PubDraftService pubDraftService;

	private final PubAssociationPickerDao pubAssociationPickerDao;

	@Value("${wldos.cms.workbench.reorder-max-siblings:3000}")
	private int workbenchReorderMaxSiblings;

	@Autowired
	public PubService(UserService userService, TermService termService, PubmetaService pubmetaService,
			RegionService regionService,
			ObjectProvider<CmsTemplatePublishedCallback> cmsTemplatePublishedCallback,
			ObjectProvider<CmsTemplateOfflineCallback> cmsTemplateOfflineCallback,
			ObjectProvider<CmsSearchIndexSyncService> cmsSearchIndexSyncService,
                      PubDraftService pubDraftService,
                      PubAssociationPickerDao pubAssociationPickerDao) {
		this.userService = userService;
		this.termService = termService;
		this.pubmetaService = pubmetaService;
		this.regionService = regionService;
		this.cmsTemplatePublishedCallback = cmsTemplatePublishedCallback;
		this.cmsTemplateOfflineCallback = cmsTemplateOfflineCallback;
		this.cmsSearchIndexSyncService = cmsSearchIndexSyncService;
        this.pubDraftService = pubDraftService;
		this.pubAssociationPickerDao = pubAssociationPickerDao;
	}

	/**
	 * 产品/协议等「关联 CMS 文档」选择器列表（专用 Dao，不经过 {@link #queryAdminPubs}）。
	 */
	public PageData<AuditPub> queryAssociationPickerPubs(PageQuery pageQuery) {
		PageQuery query = pageQuery != null ? pageQuery : new PageQuery();
		return this.pubAssociationPickerDao.pagePublishedForAssociation(query);
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

	public ContModelDto queryWorkingContModel(Long canonicalPubId) {
		return this.pubDraftService.queryWorkingContModel(canonicalPubId);
	}

	public ContModelDto queryWorkingContModel(Long canonicalPubId, Long domainId) {
		return this.pubDraftService.queryWorkingContModel(canonicalPubId, domainId);
	}

	public void assertPublished(Long pubId) {
		if (pubId == null) {
			throw new IllegalArgumentException("pubId 不能为空");
		}
		Long canonicalId = this.resolveCanonicalId(pubId);
		if (canonicalId == null) {
			throw new IllegalArgumentException("关联的 CMS 正式内容不存在: " + pubId);
		}
		KPubs pub = this.findById(canonicalId);
		if (pub == null || pub.getOriginPubId() != null) {
			throw new IllegalArgumentException("关联的 CMS 正式内容不存在: " + pubId);
		}
		if (!DeleteFlagEnum.NORMAL.toString().equals(pub.getDeleteFlag())) {
			throw new IllegalArgumentException("关联的 CMS 内容未发布: " + pubId);
		}
		if (!this.isPublishedCanonical(pub)) {
			throw new IllegalArgumentException("关联的 CMS 内容未发布: " + pubId);
		}
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
	public PageData<PubUnit> queryPubWithExtList(PageQuery pageQuery) {
		PageData<PubUnit> pubUnitPage = this.execQueryForPageKpubsParentWithChild(PubUnit.class, KTermObject.class, "k_term_object", "object_id", pageQuery);

		List<PubUnit> pubUnits = pubUnitPage.getRows();

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
			String coverUrl = this.store.getPublicUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			pub.setCover(coverUrl);

			pub.setSubTitle(ObjectUtils.string(meta.get(KModelMetaKey.PUB_META_KEY_SUB_TITLE)));

			return pub;
		}).collect(Collectors.toList());

		pubUnitPage.setRows(pubUnits);

		return pubUnitPage;
	}

	/**
	 * 查询发布内容列表,也用于查询作品下的章节列表
	 *
	 * @param pageQuery 分页参数
	 * @return 一页数据，仅关注概要信息：id、title、**count、cover、excerpt、tags等
	 */
	public PageData<PubUnit> queryArchives(PageQuery pageQuery) {
		PageData<PubUnit> pubUnits = this.execQueryForPageKpubsParentWithChild(PubUnit.class, KTermObject.class, "k_term_object", "object_id", pageQuery);
		return this.handlePubUnit(pubUnits, pageQuery);
	}

	/**
	 * 查询文档列表
	 *
	 * @param pageQuery 查询条件
	 * @return 一页文档列表
	 */
	public PageData<DocItem> queryDocList(PageQuery pageQuery) {
		return this.execQueryForPageKpubsParentWithChild(DocItem.class, KTermObject.class, "k_term_object", "object_id", pageQuery);
	}

	/**
	 * 查询供求信息
	 *
	 * @param pageQuery 分页参数
	 * @return 分页信息列表
	 */
	public PageData<InfoUnit> queryInfos(PageQuery pageQuery) {
		Map<String, Object> condition = pageQuery.getCondition();
		StringBuilder sqlSb = new StringBuilder("select p.* from k_pubs p where 1=1 ");
		CmsKpubsDiscoverySql.appendFrontDiscoveryWhere(sqlSb, "p", condition);
		String sqlNoWhere = sqlSb.toString();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		List<Object> params = new ArrayList<>(); // 参数寄存器

		// 子表动态存在,分类按过滤，标签不分层
		Object tId = condition.get("termTypeId");
		if (!ObjectUtils.isBlank(tId) || (filter != null && filter.containsKey("termTypeId"))) {
			String cAlias = "o";
			String baseExistsSql = this.commonOperate.makeBaseExistsSql("k_term_object", cAlias, "p", "object_id");

			sqlNoWhere += this.commonOperate.existsSql(cAlias, KTermObject.class, baseExistsSql, params, pageQuery);
		}

		// 自定义动态存在（这里用法其实不对，应该是确定的sqlNoWhere：使用表关联确定sql主体，然后在底层生成where的动态条件，不过此种可能更适应具体场景）
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
				baseExistsSql += " and CAST(COALESCE(m.meta_value, '0') AS SIGNED) >= ?";
				params.add(prices[0]);
			}

			if (!prices[1].equals("0")) {
				baseExistsSql += " and CAST(COALESCE(m.meta_value, '0') AS SIGNED) <= ?";
				params.add(prices[1]);
			}
			sqlNoWhere += baseExistsSql + ")";
		}


		PageData<InfoUnit> pubUnits = this.commonOperate.execQueryForPage(InfoUnit.class, sqlNoWhere, params, pageQuery, SQLTable.of(KPubs.class, "p"));
		return this.handleInfoUnit(pubUnits, pageQuery);
	}

	private PageData<InfoUnit> handleInfoUnit(PageData<InfoUnit> pubUnitPage, PageQuery pageQuery) {
		List<InfoUnit> pubUnits = pubUnitPage.getRows();

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
			String coverUrl = this.store.getPublicUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

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

		pubUnitPage.setRows(pubUnits);

		return pubUnitPage;
	}

	private PageData<PubUnit> handlePubUnit(PageData<PubUnit> pubUnitPage, PageQuery pageQuery) {
		List<PubUnit> pubUnits = pubUnitPage.getRows();

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
			String coverUrl = this.store.getPublicUrl(meta.get(KModelMetaKey.PUB_META_KEY_COVER), this.defaultCover(count.getAndIncrement()));

			pub.setCover(coverUrl);

			return pub;
		}).collect(Collectors.toList());

		pubUnitPage.setRows(pubUnits);

		return pubUnitPage;
	}

	/**
	 * 通过分站查询分站内容
	 *
	 * @param domainId 分站域名Id
	 * @param pageQuery 查询参数
	 * @return 业务对象列表，仅关注概要信息：id、title、createBy、termTypeIds、tags、updateTime、**count等
	 */
	public PageData<AuditPub> queryAdminPubs(PageQuery pageQuery) {

		// 管理端列表：不过滤 UNLISTED / INTERNAL_ONLY
		pageQuery.pushParam(CmsKpubsDiscoverySql.CONDITION_INCLUDE_UNLISTED, Boolean.TRUE);

		PageData<AuditPub> pubUnitPage = this.execQueryAdminCanonicalPubs(pageQuery);

		List<AuditPub> pubUnits = pubUnitPage.getRows();

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
			pub.setHasPendingDraft(this.pubDraftService.hasActiveDraft(id));
		}).collect(Collectors.toList());

		pubUnitPage.setRows(pubUnits);

		return pubUnitPage;
	}

	/**
	 * 全文检索
	 *
	 * @param domainId 分站域名Id
	 * @param pageQuery 查询参数
	 * @return 检索结果集
	 */
	public PageData<SPub> searchPubs(Long domainId, PageQuery pageQuery, String keywords) {
		return searchPubsByVisibility(domainId, pageQuery, keywords, false);
	}

	/**
	 * 内部资料全文检索：含 {@code INTERNAL_ONLY}，仍排除 {@code UNLISTED}；须登录且域内鉴权由网关/调用方保证。
	 */
	public PageData<SPub> searchInternalPubs(Long domainId, PageQuery pageQuery, String keywords) {
		return searchPubsByVisibility(domainId, pageQuery, keywords, true);
	}

	private PageData<SPub> searchPubsByVisibility(Long domainId, PageQuery pageQuery, String keywords,
			boolean includeInternal) {
		String visibilityClause;
		if (includeInternal) {
			visibilityClause = "and (COALESCE(NULLIF(TRIM(s.visibility_scope), ''), '"
					+ CmsKpubsDiscoverySql.SCOPE_PUBLIC_LISTED + "') NOT IN ('"
					+ CmsKpubsDiscoverySql.SCOPE_UNLISTED + "')) ";
		}
		else {
			visibilityClause = "and (COALESCE(NULLIF(TRIM(s.visibility_scope), ''), '"
					+ CmsKpubsDiscoverySql.SCOPE_PUBLIC_LISTED + "') NOT IN ('"
					+ CmsKpubsDiscoverySql.SCOPE_UNLISTED + "', '"
					+ CmsKpubsDiscoverySql.SCOPE_INTERNAL_ONLY + "')) ";
		}
		String sql = "select s.id, s.pub_title, s.pub_type from k_pubs s where s.delete_flag = 'normal' and s.origin_pub_id is null and s.pub_status in ('publish', 'inherit') "
				+ visibilityClause
				+ "and (instr(s.pub_title, ?)>0 or instr(s.pub_content, ?) >0) and domain_id=?";

		return this.commonOperate.execQueryForPageNoOrder(SPub.class, sql, pageQuery.getCurrent(), pageQuery.getPageSize(),
				keywords, keywords, domainId);
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

	public PageData<PubUnit> queryArchivesUser(PageQuery pageQuery) {
		PageData<PubUnit> pubUnitPage = this.execQueryForPageKpubsParentWithChild(PubUnit.class, KStars.class, "k_stars", "object_id", pageQuery);

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
		return this.store.getPublicUrl(((count / coverNum) % 2 == 0 ? covers[count % coverNum] : covers[(coverNum - 1) - (count % coverNum)]), "");
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

	/** 同级末尾序号：未传 displayOrder 的新增内容用 MAX+1，与 {@link #reorderDisplayOrder} 的 1..n 一致。 */
	public int allocateNextDisplayOrder(Long parentId) {
		Long pid = parentId != null ? parentId : Constants.TOP_PUB_ID;
		Integer max = this.entityRepo.maxDisplayOrderByParent(pid);
		return (max == null ? 0 : max) + 1;
	}

	public long countPolicyWorkbenchWorks(Long userId, String pubType) {
		Long count = this.entityRepo.countCanonicalByAuthorParentAndType(userId, Constants.TOP_PUB_ID, pubType);
		return count != null ? count.longValue() : 0L;
	}

	public long countPolicyWorkbenchChapters(Long userId, Long bookId) {
		Long count = this.entityRepo.countCanonicalByAuthorParentAndType(userId, bookId, PubTypeEnum.CHAPTER.getName());
		return count != null ? count.longValue() : 0L;
	}

	public void shiftSiblingOrder(Long id, int step, Long userId, Long domainId, WorkbenchListScope scope) {
		if (step == 0 || id == null) {
			return;
		}
		KPubs row = this.findById(id);
		if (row == null) {
			throw new BaseException("内容不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		List<Long> orderedIds;
		if (PubTypeEnum.CHAPTER.getName().equals(row.getPubType())) {
			orderedIds = this.listChapterIdsForWorkbench(row.getParentId(), scope);
		} else {
			orderedIds = this.listMainWorkIdsForWorkbench(userId, domainId, scope);
		}
		if (orderedIds == null || orderedIds.size() < 2) {
			return;
		}
		this.assertWorkbenchReorderSiblingLimit(orderedIds.size());
		int index = orderedIds.indexOf(id);
		if (index < 0) {
			throw new BaseException("内容不在可排序列表中", ResultCode.VALIDATION_ERROR.getCode());
		}
		int target = index + step;
		if (target < 0 || target >= orderedIds.size()) {
			return;
		}
		Collections.swap(orderedIds, index, target);
		this.reorderDisplayOrder(orderedIds, row.getParentId(), userId);
	}

	/**
	 * 在当前页 id 集合内重排：在全量 sibling 序中定位其连续区间并替换，再一次性写 display_order。
	 *
	 * @param pageOrderedIds 当前页按目标顺序排列的 id（须为全量序中的连续一段）
	 * @param userId 作者
	 * @param chapterBookId 非空则按该作品下章节 sibling 加载全量序，否则按作者作品列表
	 */
	public void reorderSiblingPageSegment(List<Long> pageOrderedIds, Long userId, Long chapterBookId, Long domainId,
			WorkbenchListScope scope) {
		if (pageOrderedIds == null || pageOrderedIds.isEmpty()) {
			return;
		}
		Set<Long> pageSet = new HashSet<>(pageOrderedIds);
		if (pageSet.size() != pageOrderedIds.size()) {
			throw new BaseException("排序列表存在重复 id", ResultCode.VALIDATION_ERROR.getCode());
		}
		List<Long> full;
		Long expectedParent;
		if (chapterBookId != null) {
			full = this.listChapterIdsForWorkbench(chapterBookId, scope);
			expectedParent = chapterBookId;
		} else {
			full = this.listMainWorkIdsForWorkbench(userId, domainId, scope);
			expectedParent = Constants.TOP_PUB_ID;
		}
		if (full == null || full.isEmpty()) {
			throw new BaseException("内容不在可排序列表中", ResultCode.VALIDATION_ERROR.getCode());
		}
		this.assertWorkbenchReorderSiblingLimit(full.size());
		int first = -1;
		int last = -1;
		for (int i = 0; i < full.size(); i += 1) {
			if (pageSet.contains(full.get(i))) {
				if (first < 0) {
					first = i;
				}
				last = i;
			}
		}
		if (first < 0) {
			throw new BaseException("内容不在可排序列表中", ResultCode.VALIDATION_ERROR.getCode());
		}
		int span = last - first + 1;
		if (span != pageOrderedIds.size()) {
			throw new BaseException("仅支持当前页连续条目排序", ResultCode.VALIDATION_ERROR.getCode());
		}
		for (int i = first; i <= last; i += 1) {
			if (!pageSet.contains(full.get(i))) {
				throw new BaseException("仅支持当前页连续条目排序", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
		List<Long> merged = new ArrayList<>(full.subList(0, first));
		merged.addAll(pageOrderedIds);
		merged.addAll(full.subList(last + 1, full.size()));
		this.reorderDisplayOrder(merged, expectedParent, userId);
	}

	private void assertWorkbenchReorderSiblingLimit(int siblingCount) {
		if (siblingCount > this.workbenchReorderMaxSiblings) {
			throw new BaseException(
					"同级内容过多（" + siblingCount + " 条），超过创作工作台排序上限 "
							+ this.workbenchReorderMaxSiblings + " 条，请拆分作品或减少章节后再试",
					ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private List<String> scopePubTypes(WorkbenchListScope scope) {
		return scope != null ? scope.getPubTypes() : null;
	}

	private String scopeKeyword(WorkbenchListScope scope) {
		return scope != null ? scope.getKeyword() : null;
	}

	/** 与 {@code GET /space/book} 列表 SQL 范围一致（域、类型、keyword）。 */
	private List<Long> listMainWorkIdsForWorkbench(Long userId, Long domainId, WorkbenchListScope scope) {
		PageQuery pageQuery = this.buildWorkbenchWorkListQuery(userId, domainId, scopePubTypes(scope),
				scopeKeyword(scope));
		return this.queryOrderedPubIds(pageQuery, KTermObject.class, "k_term_object", "object_id");
	}

	private List<Long> listChapterIdsForWorkbench(Long bookId, WorkbenchListScope scope) {
		PageQuery pageQuery = new PageQuery();
		pageQuery.pushParam("parentId", bookId);
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		String keyword = scopeKeyword(scope);
		if (!ObjectUtils.isBlank(keyword)) {
			pageQuery.pushParam("keyword", keyword);
		}
		pageQuery.setSorter(Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("id")));
		return this.queryOrderedPubIds(pageQuery, KTermObject.class, "k_term_object", "object_id");
	}

	private PageQuery buildWorkbenchWorkListQuery(Long userId, Long domainId, List<String> pubTypes, String keyword) {
		PageQuery pageQuery = new PageQuery();
		pageQuery.pushParam("createBy", userId);
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		pageQuery.pushParam(CmsKpubsDiscoverySql.CONDITION_INCLUDE_UNLISTED, Boolean.TRUE);
		if (domainId != null) {
			pageQuery.pushParam(Constants.COMMON_KEY_DOMAIN_COLUMN, domainId);
		}
		pageQuery.pushFilter("parentId", Constants.TOP_PUB_ID);
		if (pubTypes != null && !pubTypes.isEmpty()) {
			pageQuery.pushFilter("pubType", pubTypes);
		}
		if (!ObjectUtils.isBlank(keyword)) {
			pageQuery.pushParam("keyword", keyword);
		}
		pageQuery.setSorter(Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("id")));
		return pageQuery;
	}

	private List<Long> queryOrderedPubIds(PageQuery pageQuery, Class<?> childClass, String childTable, String parentKey) {
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition() == null ? new LinkedHashMap<>()
				: new LinkedHashMap<>(pageQuery.getCondition());
		String keyword = extractAndRemoveKeyword(condition);
		List<Object> params = new ArrayList<>();
		StringBuilder sql = this.commonOperate.querySqlByTable("k_pubs", KPubs.class, params, condition, filter);
		replaceSelectWithIdOnly(sql);
		CmsKpubsDiscoverySql.appendCanonicalPubsOnly(sql, "a");
		CmsKpubsDiscoverySql.appendFrontDiscoveryWhere(sql, "a", condition);
		appendAdminKeywordFilter(sql, params, keyword);
		sql.append(this.commonOperate.existsSql("b", childClass,
				this.commonOperate.makeBaseExistsSql(childTable, "b", "a", parentKey), params, condition, filter));
		this.commonOperate.orderSql(sort, sql);
		return this.commonOperate.getJdbcOperations().query(sql.toString(), (rs, rowNum) -> rs.getLong("id"),
				params.toArray());
	}

	private static void replaceSelectWithIdOnly(StringBuilder sql) {
		int from = sql.indexOf("select a.*");
		if (from >= 0) {
			sql.replace(from, from + "select a.*".length(), "select a.id");
		}
	}

	public void reorderDisplayOrder(List<Long> orderedIds, Long parentId, Long userId) {
		if (orderedIds == null || orderedIds.isEmpty()) {
			return;
		}
		this.assertWorkbenchReorderSiblingLimit(orderedIds.size());
		Long expectedParent = parentId != null ? parentId : Constants.TOP_PUB_ID;
		Map<Long, KPubs> loaded = new HashMap<>();
		for (KPubs row : this.entityRepo.findAllById(orderedIds)) {
			if (row != null && row.getId() != null) {
				loaded.put(row.getId(), row);
			}
		}
		for (int i = 0; i < orderedIds.size(); i++) {
			Long id = orderedIds.get(i);
			int desiredOrder = i + 1;
			KPubs row = loaded.get(id);
			if (row == null || !DeleteFlagEnum.NORMAL.toString().equals(row.getDeleteFlag())) {
				throw new BaseException("内容不存在或已删除", ResultCode.VALIDATION_ERROR.getCode());
			}
			if (userId != null && !userId.equals(row.getCreateBy())) {
				throw new BaseException("无权调整该内容顺序", ResultCode.FORBIDDEN.getCode());
			}
			if (!expectedParent.equals(row.getParentId())) {
				throw new BaseException("内容不属于同一层级", ResultCode.VALIDATION_ERROR.getCode());
			}
			if (!Objects.equals(row.getDisplayOrder(), desiredOrder)) {
				row.setDisplayOrder(desiredOrder);
				this.saveOrUpdate(row);
			}
		}
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
		if (!PubTypeEnum.isSub(pubType)) {
			notifyCmsTemplatePublished(pId, pubType);
			syncSearchIndex(pId);
		}
	}

	private void syncSearchIndex(Long pubId) {
		CmsSearchIndexSyncService sync = cmsSearchIndexSyncService.getIfAvailable();
		if (sync == null || pubId == null) {
			return;
		}
		KPubs pub = this.findById(pubId);
		if (pub != null) {
			sync.syncCanonical(pub);
		}
	}

	private void removeSearchIndex(Long pubId) {
		CmsSearchIndexSyncService sync = cmsSearchIndexSyncService.getIfAvailable();
		if (sync != null && pubId != null) {
			sync.removeCanonical(pubId);
		}
	}

	private void notifyCmsTemplatePublished(Long pId, String pubType) {
		CmsTemplatePublishedCallback callback = cmsTemplatePublishedCallback.getIfAvailable();
		if (callback == null) {
			return;
		}
		KPubs pub = this.findById(pId);
		String title = pub != null && pub.getPubTitle() != null ? pub.getPubTitle() : String.valueOf(pId);
		String version = pub != null && pub.getUpdateTime() != null
				? String.valueOf(pub.getUpdateTime().getTime())
				: "1.0.0";
		String payloadJson = "{\"pubId\":" + pId + ",\"pubType\":\"" + pubType + "\"}";
		callback.onPublished(String.valueOf(pId), title, version, payloadJson);
	}

	/**
	 * 发布：首次发布正式行；草稿提交审核；管理员合并待审草稿。
	 */
	public void publishOrMergeDraft(Long pubId, String pubType, Long operatorUserId, String operatorIp) {
		KPubs row = this.findById(pubId);
		if (row == null) {
			throw new IllegalArgumentException("内容不存在");
		}
		if (row.getOriginPubId() != null) {
			if (PubStatusEnum.DRAFT.getValue().equals(row.getPubStatus())) {
				this.pubDraftService.submitDraftForReview(pubId);
				return;
			}
			if (PubStatusEnum.IN_REVIEW.getValue().equals(row.getPubStatus())) {
				if (!this.isCanTrust(operatorUserId)) {
					throw new IllegalStateException("待审草稿须由管理员审核发布");
				}
				this.pubDraftService.mergeDraft(row.getOriginPubId(), pubId, operatorUserId, operatorIp);
				return;
			}
			throw new IllegalStateException("当前草稿状态不可发布");
		}
		if (this.pubDraftService.isPublishedCanonical(row)) {
			KPubs activeDraft = this.pubDraftService.findActiveDraft(pubId);
			if (activeDraft != null && PubStatusEnum.DRAFT.getValue().equals(activeDraft.getPubStatus())) {
				this.pubDraftService.submitDraftForReview(activeDraft.getId());
				return;
			}
			if (activeDraft != null && PubStatusEnum.IN_REVIEW.getValue().equals(activeDraft.getPubStatus())) {
				if (!this.isCanTrust(operatorUserId)) {
					throw new IllegalStateException("待审改稿须由管理员审核");
				}
				this.pubDraftService.mergeDraft(pubId, activeDraft.getId(), operatorUserId, operatorIp);
				return;
			}
			throw new IllegalStateException("内容已发布");
		}
		this.publishPub(pubId, pubType);
	}

	public void rejectDraft(Long draftId) {
		this.pubDraftService.rejectDraft(draftId);
	}

	public boolean isDraftEligibleCanonical(KPubs canonical) {
		return this.pubDraftService.isDraftEligibleCanonical(canonical);
	}

	public boolean isPublishedCanonical(KPubs canonical) {
		return this.pubDraftService.isPublishedCanonical(canonical);
	}

	public Long upsertActiveDraft(KPubs canonical, com.wldos.cms.vo.Pub pub, Long userId, String userIp) {
		return this.pubDraftService.upsertActiveDraft(canonical, pub, userId, userIp);
	}

	public void mergeTrustedEdit(KPubs canonical, com.wldos.cms.vo.Pub pub, Long userId, String userIp) {
		this.pubDraftService.mergeTrustedEdit(canonical, pub, userId, userIp);
	}

	public boolean hasActiveDraft(Long canonicalId) {
		return this.pubDraftService.hasActiveDraft(canonicalId);
	}

	public Long resolveCanonicalId(Long pubId) {
		return this.pubDraftService.resolveCanonicalId(pubId);
	}

	/**
	 * 下线发布内容,驳回整改
	 *
	 * @param pub 发布内容
	 */
	public void offlinePub(AuditPub pub) {
		this.entityRepo.changePubStatus(pub.getId(), PubStatusEnum.OFFLINE.toString());
		if (pub != null && pub.getId() != null) {
			notifyCmsTemplateOffline(pub.getId());
			removeSearchIndex(pub.getId());
		}
	}

	private void notifyCmsTemplateOffline(Long pId) {
		CmsTemplateOfflineCallback callback = cmsTemplateOfflineCallback.getIfAvailable();
		if (callback == null) {
			return;
		}
		callback.onOffline(String.valueOf(pId));
	}

	/**
	 * 回收站发布内容，个人发布内容维护
	 *
	 * @param pub 发布内容
	 */
	public void trashPub(AuditPub pub) {
		this.entityRepo.changePubStatus(pub.getId(), PubStatusEnum.TRASH.toString());
		if (pub != null && pub.getId() != null) {
			removeSearchIndex(pub.getId());
		}
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

	/** 更新可发现性（主表 {@code k_pubs.visibility_scope}）。 */
	public void updateVisibilityScope(Long id, String visibilityScope, Long domainId, Long userId, String userIp) {
		if (!this.isAdmin(userId) && !this.isCanTrust(userId)) {
			throw new IllegalStateException("无权限修改发现范围");
		}
		KPubs pub = this.findById(id);
		if (pub == null) {
			throw new IllegalArgumentException("内容不存在");
		}
		if (domainId != null && pub.getDomainId() != null && !domainId.equals(pub.getDomainId())) {
			throw new IllegalArgumentException("无权限操作该内容");
		}
		pub.setVisibilityScope(CmsKpubsDiscoverySql.normalizeScopeForSave(visibilityScope));
		pub.setUpdateBy(userId);
		pub.setUpdateIp(userIp);
		this.entityRepo.saveOrUpdate(pub);
		if (PubStatusEnum.PUBLISH.getValue().equals(pub.getPubStatus())
				|| PubStatusEnum.INHERIT.getValue().equals(pub.getPubStatus())) {
			syncSearchIndex(id);
		}
	}

	/**
	 * k_pubs 父子分页：父表动态条件 + 前台可发现性（{@link CmsKpubsDiscoverySql#appendFrontDiscoveryWhere}）+ 子表 EXISTS。
	 */
	private PageData<AuditPub> execQueryAdminCanonicalPubs(PageQuery pageQuery) {
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		String keyword = extractAndRemoveKeyword(condition);
		List<Object> params = new ArrayList<>();
		StringBuilder sql = this.commonOperate.querySqlByTable("k_pubs", KPubs.class, params, condition, filter);
		CmsKpubsDiscoverySql.appendCanonicalPubsOnly(sql, "a");
		appendAdminKeywordFilter(sql, params, keyword);
		sql.append(this.commonOperate.existsSql("b", KTermObject.class,
				this.commonOperate.makeBaseExistsSql("k_term_object", "b", "a", "object_id"), params, condition, filter));
		String countSql = "select count(1) as total from ( " + sql + " ) w";
		List<Map<String, Object>> all = this.commonOperate.getJdbcOperations().queryForList(countSql, params.toArray());
		int total = Integer.parseInt(ObjectUtils.string(all.get(0).get("total")));
		this.commonOperate.orderSql(sort, sql);
		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		int totalPageNum = total == 0 ? 1 : (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);
		List<AuditPub> list = this.commonOperate.execQueryForPage(AuditPub.class, sql.toString(), currentPage, pageSize, params.toArray());
		return new PageData<>(total, currentPage, pageSize, list);
	}

	private static String extractAndRemoveKeyword(Map<String, Object> condition) {
		if (condition == null) {
			return null;
		}
		Object raw = condition.remove("keyword");
		if (raw == null) {
			return null;
		}
		String keyword = ObjectUtils.string(raw).trim();
		return keyword.isEmpty() ? null : keyword;
	}

	private static void appendAdminKeywordFilter(StringBuilder sql, List<Object> params, String keyword) {
		if (keyword == null) {
			return;
		}
		sql.append(" and (instr(a.pub_title, ?) > 0 or instr(a.pub_content, ?) > 0) ");
		params.add(keyword);
		params.add(keyword);
	}

	private <V, C> PageData<V> execQueryForPageKpubsParentWithChild(Class<V> vo, Class<C> cClass, String cTable, String pIdKey, PageQuery pageQuery) {
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		String keyword = extractAndRemoveKeyword(condition);
		List<Object> params = new ArrayList<>();
		StringBuilder sql = this.commonOperate.querySqlByTable("k_pubs", KPubs.class, params, condition, filter);
		CmsKpubsDiscoverySql.appendCanonicalPubsOnly(sql, "a");
		CmsKpubsDiscoverySql.appendFrontDiscoveryWhere(sql, "a", condition);
		appendAdminKeywordFilter(sql, params, keyword);
		sql.append(this.commonOperate.existsSql("b", cClass, this.commonOperate.makeBaseExistsSql(cTable, "b", "a", pIdKey), params, condition, filter));
		String countSql = "select count(1) as total from ( " + sql + " ) w";
		List<Map<String, Object>> all = this.commonOperate.getJdbcOperations().queryForList(countSql, params.toArray());
		int total = Integer.parseInt(ObjectUtils.string(all.get(0).get("total")));
		this.commonOperate.orderSql(sort, sql);
		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);
		List<V> list = this.commonOperate.execQueryForPage(vo, sql.toString(), currentPage, pageSize, params.toArray());
		return new PageData<>(total, currentPage, pageSize, list);
	}
}
