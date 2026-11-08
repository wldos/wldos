/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 */

package com.wldos.cms.schedule;

import com.wldos.cms.service.PubDraftService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时物理删除已合并超过留存期的草稿行。
 */
@Slf4j
@Component
public class PubDraftCleanupTask {

	private final PubDraftService pubDraftService;

	public PubDraftCleanupTask(PubDraftService pubDraftService) {
		this.pubDraftService = pubDraftService;
	}

	@Scheduled(cron = "${wldos.cms.draft.cleanup-cron:0 20 4 * * ?}")
	public void cleanupMergedDrafts() {
		try {
			this.pubDraftService.cleanupMergedDrafts();
		}
		catch (Exception e) {
			log.warn("CMS merged draft cleanup failed", e);
		}
	}
}
