/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.cms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.framework.service.NoRepoService;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.Pub;
import com.wldos.common.utils.DateUtils;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.cms.dto.PubTypeExt;
import com.wldos.support.term.dto.Term;
import com.wldos.support.term.enums.TermTypeEnum;
import com.wldos.sys.base.enums.PubTypeEnum;
import com.wldos.sys.base.service.TermService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 内容创作工作台service。
 *
 * @author 元悉宇宙
 * @date 2022/01/05
 * @version 1.0
 */
@Slf4j
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class SpaceService extends NoRepoService {

	private final PubService pubService;

	private final TermService termService;

	private final KCMSService kcmsService;

	public SpaceService(PubService pubService, TermService termService, KCMSService kcmsService) {
		this.pubService = pubService;
		this.termService = termService;
		this.kcmsService = kcmsService;
	}

	/**
	 * 创作时查询作品的某个章节，无需关心状态
	 *
	 * @param chapterId the chapter id
	 * @return chapter info
	 */
	public Chapter queryChapter(Long chapterId) {
		KPubs pub = this.pubService.findById(chapterId);

		if (pub == null)
			return null;

		return Chapter.of(pub);
	}

	/**
	 * 创建新章节
	 *
	 * @param chapter chapter
	 * @param curUserId current user id
	 * @param userIp current user ip
	 * @return chapter info
	 */
	public Chapter createChapter(Pub chapter, Long curUserId, String userIp) {
		chapter.setPubTitle(DateUtils.format(new Date(), DateUtils.TIME_PATTER));

		// 元素直接发布，为继承状态，作品不发布元素不会被读取
		chapter.setPubStatus(PubStatusEnum.INHERIT.toString());
		chapter.setPubType(PubTypeEnum.CHAPTER.getName());
		Long id = this.insertChapter(chapter, curUserId, userIp);

		// 取出父作品分类
		List<Term> termsType = this.termService.findAllByObjectAndClassType(chapter.getParentId(), TermTypeEnum.CATEGORY.toString());
		List<Long> termTypeIds = termsType.stream().map(Term::getTermTypeId).collect(Collectors.toList());

		// 内容分类以作品为准，另外已经被内容继承的作品分类不允许删除（暂未作约束）
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
	 * @param domainId 当前域id
	 */
	public void saveChapter(Pub chapter, Long curUserId, String userIp, Long domainId) {
		this.kcmsService.update(chapter, curUserId, userIp, domainId);
	}

	private final BeanCopier pubCopier = BeanCopier.create(Pub.class, KPubs.class, false);

	/**
	 * 保存带扩展属性的子内容：章节、附件、图片等
	 *
	 * @param pub 发布内容
	 * @param userId 用户id
	 * @param userIp 用户ip
	 */
	public Long insertChapter(Pub pub, Long userId, String userIp) {

		// 为便于结构化处理，对于图片等附件的处理，要在上传文件和编辑文件时将设置数据存储到pub metadata中，在发布内容渲染时再读出

		KPubs pubs = new KPubs();
		this.pubCopier.copy(pub, pubs, null);

		// @todo 考虑嵌入过滤器hook：pubs = applyFilter("savePub", pubs);

		Long id = this.pubService.insertSelective(pubs, true);

		List<PubTypeExt> pubTypeExt = pub.getPubTypeExt();

		if (ObjectUtils.isBlank(pubTypeExt)) {
			pubTypeExt = new ArrayList<>();
			this.kcmsService.appendPubMeta(pubTypeExt); // 添加后台静默处理的元数据，比如查看数
		}

		// @todo 考虑嵌入过滤器hook：pubTypeExt = applyFilter("savePubTypeExt", pubTypeExt);
		// 保存扩展属性
		this.kcmsService.createPubMeta(pubTypeExt, id);

		return id;
	}
}
