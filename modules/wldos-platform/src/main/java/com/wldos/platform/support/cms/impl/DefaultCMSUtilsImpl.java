/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.impl;

import com.wldos.common.dto.LevelNode;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.platform.support.cms.CMSUtils;
import com.wldos.platform.support.cms.PubOpener;
import com.wldos.platform.support.cms.PubTypeExtOpener;
import com.wldos.platform.support.cms.PubmetaOpener;
import com.wldos.platform.support.cms.dto.ContModelDto;
import com.wldos.platform.support.cms.dto.PubTypeExt;
import com.wldos.platform.support.cms.entity.KPubmeta;
import com.wldos.platform.support.cms.model.Attachment;
import com.wldos.platform.support.cms.model.IMeta;
import com.wldos.platform.support.cms.vo.Breadcrumb;
import com.wldos.platform.support.cms.vo.RouteParams;
import com.wldos.platform.support.cms.vo.SeoCrumbs;
import com.wldos.platform.support.region.RegionOpener;
import com.wldos.platform.support.term.TermOpener;
import com.wldos.platform.support.term.dto.Term;
import com.wldos.platform.support.term.enums.TermTypeEnum;
import com.wldos.platform.support.web.enums.TemplateTypeEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认 CMSUtils 实现类（开源版本）
 * 当 Agent 模块中的 CMSUtilsImpl 不存在时使用此实现
 * 使用降级处理：简化实现，提供基础的 CMS 功能
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2026/01/10
 */
@ConditionalOnMissingClass("com.wldos.support.cms.CMSUtilsImpl")
@Component("cmsUtils")
public class DefaultCMSUtilsImpl implements CMSUtils {

	@Override
	public SeoCrumbs genSeoCrumbs(RouteParams params, TermOpener termOpener) {
		String slugTerm = params.getSlugTerm();
		String tempType = params.getTempType();

		if (ObjectUtils.isBlank(slugTerm)) {
			String name = tempType == null ? "所有领域" : TemplateTypeEnum.getTemplateTypeEnumByValue(tempType).getLabel();
			List<Breadcrumb> breadcrumbs = new ArrayList<>();
			breadcrumbs.add(Breadcrumb.of("/" + tempType, name));
			return SeoCrumbs.of(name, "内容领域：" + name, name, breadcrumbs);
		}

		// 降级处理：简化实现，直接查询分类项
		Term term = termOpener != null ? termOpener.queryTermBySlugTerm(slugTerm) : null;
		String name;
		List<Breadcrumb> crumb;

		if (term == null) {
			name = "不存在";
			crumb = new ArrayList<>();
		} else {
			name = term.getName();
			// 降级处理：简化面包屑生成
			crumb = new ArrayList<>();
			if (termOpener != null) {
				List<LevelNode> nodes = termOpener.queryTermTreeByChildId(term.getTermTypeId());
				if (nodes != null && !nodes.isEmpty()) {
					List<Long> termTypeIds = nodes.stream().map(LevelNode::getId).collect(Collectors.toList());
					List<Term> terms = termOpener.queryAllByTermTypeIds(termTypeIds);
					crumb = terms.stream()
							.map(t -> Breadcrumb.of("/" + tempType + "/" + t.getSlug(), t.getName()))
							.collect(Collectors.toList());
				}
			}
		}

		String label = tempType != null ? TemplateTypeEnum.getTemplateTypeEnumByValue(tempType).getLabel() : "内容";
		return SeoCrumbs.of(name, label + "分类：" + name, name, crumb);
	}

	@Override
	public void genSeoAndCrumbs(IMeta iMeta, Long termTypeId, PubOpener pubOpener, TermOpener termOpener) {
		// 降级处理：简化实现
		SeoCrumbs seoCrumbs = SeoCrumbs.of(
				iMeta.getPubTitle(),
				pubOpener != null ? pubOpener.genPubExcerpt(iMeta.getPubContent(), 140) : iMeta.getPubTitle(),
				iMeta.getPubTitle(),
				iMeta.getPubType());

		// 生成关键词
		List<Term> tags = iMeta.getTags();
		if (tags != null && !tags.isEmpty()) {
			String keywords = tags.stream().map(Term::getName).collect(Collectors.joining(","));
			seoCrumbs.setKeywords(keywords);
		} else {
			seoCrumbs.setKeywords(iMeta.getPubTitle());
		}

		// 生成面包屑
		List<Breadcrumb> crumb = new ArrayList<>();
		if (termOpener != null && termTypeId != null) {
			List<LevelNode> nodes = termOpener.queryTermTreeByChildId(termTypeId);
			if (nodes != null && !nodes.isEmpty()) {
				List<Long> termTypeIds = nodes.stream().map(LevelNode::getId).collect(Collectors.toList());
				List<Term> terms = termOpener.queryAllByTermTypeIds(termTypeIds);
				crumb = terms.stream().map(term -> {
					Breadcrumb bc = new Breadcrumb();
					String path = "/" + (iMeta.getClass().getSimpleName().toLowerCase()) + "/" + term.getSlug();
					bc.setPath(path);
					bc.setBreadcrumbName(term.getName());
					return bc;
				}).collect(Collectors.toList());
			}
		}
		seoCrumbs.setCrumbs(crumb);
		iMeta.setSeoCrumbs(seoCrumbs);
	}

	@Override
	public void termAndTagHandle(IMeta iMeta, Long pid, TermOpener termOpener) {
		// 降级处理：简化实现
		if (termOpener == null || pid == null) {
			iMeta.setTermTypeIds(new ArrayList<>());
			iMeta.setTags(new ArrayList<>());
			return;
		}

		// 分类目录
		List<Term> termsType = termOpener.findAllByObjectAndClassType(pid, TermTypeEnum.CATEGORY.toString());
		List<Long> termTypeIds = termsType != null 
				? termsType.stream().map(Term::getTermTypeId).collect(Collectors.toList())
				: new ArrayList<>();
		iMeta.setTermTypeIds(termTypeIds);

		// 标签
		List<Term> terms = termOpener.findAllByObjectAndClassType(pid, TermTypeEnum.TAG.toString());
		iMeta.setTags(terms != null ? terms : new ArrayList<>());
	}

	@Override
	public void populateMeta(IMeta product, Map<String, String> pubMeta, RegionOpener regionOpener) {
		// 降级处理：简化实现，直接设置元数据
		if (pubMeta == null) {
			return;
		}

		product.setCover(pubMeta.get("cover"));
		String subTitle = pubMeta.get("subTitle");
		if (!ObjectUtils.isBlank(subTitle)) {
			product.setSubTitle(subTitle);
		}

		String ornPrice = pubMeta.get("ornPrice");
		if (!ObjectUtils.isBlank(ornPrice)) {
			try {
				product.setOrnPrice(new BigDecimal(ornPrice));
			} catch (Exception e) {
				// 忽略转换错误
			}
		}

		String contact = pubMeta.get("contact");
		if (!ObjectUtils.isBlank(contact)) {
			product.setContact(contact);
		}

		String telephone = pubMeta.get("telephone");
		if (!ObjectUtils.isBlank(telephone)) {
			product.setTelephone(telephone);
		}
	}

	@Override
	public IMeta handleContent(IMeta iMeta, List<KPubmeta> metas, ContModelDto contBody, Long pid,
	                           PubTypeExtOpener pubTypeExtService, PubOpener pubService, PubmetaOpener pubmetaService) {
		// 降级处理：简化实现，直接返回原始对象
		return iMeta;
	}

	@Override
	public List<PubTypeExt> getPubTypeExt(List<PubTypeExt> pubTypeExt, List<KPubmeta> metas) {
		// 降级处理：简化实现，直接返回原始列表
		return pubTypeExt != null ? pubTypeExt : new ArrayList<>();
	}

	@Override
	public List<Attachment> getAttachments(Map<Long, List<KPubmeta>> attMetaGroup) {
		// 降级处理：简化实现，返回空列表
		return new ArrayList<>();
	}
}
