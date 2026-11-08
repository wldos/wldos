/*

 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.

 * Created by Yuanxi Universe (306991142@qq.com)

 * Licensed under the Apache License, Version 2.0 or a commercial license.

 * For Apache License Version 2.0 see License in the project root for license information.

 * For commercial licenses see term.md or contact 306991142@qq.com

 */



package com.wldos.cms.service;



import com.wldos.cms.policy.WorkbenchLimitsSnapshot;

import com.wldos.cms.policy.WorkbenchPolicyContext;

import com.wldos.cms.policy.WorkbenchPolicyHooks;

import com.wldos.cms.policy.WorkbenchPolicyOrchestrator;

import com.wldos.platform.core.enums.PubTypeEnum;



import org.springframework.stereotype.Component;



/**

 * 创作工作台权益门禁（Hook 聚合后断言）。

 *

 * @author Yuanxi Universe

 * @version 1.0

 * @since 3.0

 */

@Component

public class WorkbenchPolicySupport {



	private final PubService pubService;



	private final WorkbenchPolicyOrchestrator workbenchPolicyOrchestrator;



	public WorkbenchPolicySupport(PubService pubService, WorkbenchPolicyOrchestrator workbenchPolicyOrchestrator) {

		this.pubService = pubService;

		this.workbenchPolicyOrchestrator = workbenchPolicyOrchestrator;

	}



	public void assertCreateRootWork(Long userId, Long domainId, Long comId, String pubType) {

		if (userId == null || !PubTypeEnum.BOOK.getName().equals(pubType)) {

			return;

		}

		WorkbenchPolicyContext ctx = new WorkbenchPolicyContext();

		ctx.setAction(WorkbenchPolicyHooks.ACTION_CREATE_WORK);

		ctx.setSubjectUserId(userId);

		ctx.setDomainId(domainId);

		ctx.setComId(comId != null ? comId : 0L);

		ctx.setPubType(pubType);

		ctx.setCurrentUsage(this.pubService.countPolicyWorkbenchWorks(userId, pubType));

		this.workbenchPolicyOrchestrator.assertAllowed(ctx);

	}



	public void assertCreateChapter(Long userId, Long domainId, Long comId, Long bookId, String pubType) {

		if (userId == null || bookId == null) {

			return;

		}

		WorkbenchPolicyContext ctx = new WorkbenchPolicyContext();

		ctx.setAction(WorkbenchPolicyHooks.ACTION_CREATE_CHAPTER);

		ctx.setSubjectUserId(userId);

		ctx.setDomainId(domainId);

		ctx.setComId(comId != null ? comId : 0L);

		ctx.setPubType(pubType);

		ctx.setParentResourceId(bookId);

		ctx.setCurrentUsage(this.pubService.countPolicyWorkbenchChapters(userId, bookId));

		this.workbenchPolicyOrchestrator.assertAllowed(ctx);

	}



	public WorkbenchLimitsSnapshot resolveWorkbenchLimits(Long userId, Long domainId, Long comId, Long bookId) {

		WorkbenchLimitsSnapshot snapshot = new WorkbenchLimitsSnapshot();

		if (userId == null) {

			return snapshot;

		}

		long com = comId != null ? comId : 0L;

		WorkbenchPolicyContext workCtx = new WorkbenchPolicyContext();

		workCtx.setAction(WorkbenchPolicyHooks.ACTION_CREATE_WORK);

		workCtx.setSubjectUserId(userId);

		workCtx.setDomainId(domainId);

		workCtx.setComId(com);

		workCtx.setPubType(PubTypeEnum.BOOK.getName());

		workCtx.setCurrentUsage(this.pubService.countPolicyWorkbenchWorks(userId, PubTypeEnum.BOOK.getName()));

		snapshot.add(this.workbenchPolicyOrchestrator.resolveLimit(workCtx));

		if (bookId != null) {

			WorkbenchPolicyContext chapterCtx = new WorkbenchPolicyContext();

			chapterCtx.setAction(WorkbenchPolicyHooks.ACTION_CREATE_CHAPTER);

			chapterCtx.setSubjectUserId(userId);

			chapterCtx.setDomainId(domainId);

			chapterCtx.setComId(com);

			chapterCtx.setPubType(PubTypeEnum.CHAPTER.getName());

			chapterCtx.setParentResourceId(bookId);

			chapterCtx.setCurrentUsage(this.pubService.countPolicyWorkbenchChapters(userId, bookId));

			snapshot.add(this.workbenchPolicyOrchestrator.resolveLimit(chapterCtx));

		}

		return snapshot;

	}

}


