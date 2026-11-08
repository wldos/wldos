/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.service;

import com.wldos.cms.dao.PubDao;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.search.CmsSearchIndexSyncService;
import com.wldos.cms.search.CmsSearchNamespaces;
import com.wldos.cms.vo.CmsSearchOverviewVO;
import com.wldos.cms.vo.CmsSearchRebuildSummary;
import com.wldos.framework.mvc.service.NonEntityService;

import io.github.wldos.search.autoconfigure.SearchProperties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CMS 检索索引运维：全量重建与配置概览。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class CmsSearchReindexService extends NonEntityService {

	private static final int REINDEX_PAGE_SIZE = 500;

	private final PubDao pubDao;
	private final ObjectProvider<CmsSearchIndexSyncService> cmsSearchIndexSyncService;
	private final ObjectProvider<SearchProperties> searchProperties;

	public CmsSearchReindexService(PubDao pubDao,
			ObjectProvider<CmsSearchIndexSyncService> cmsSearchIndexSyncService,
			ObjectProvider<SearchProperties> searchProperties) {
		this.pubDao = pubDao;
		this.cmsSearchIndexSyncService = cmsSearchIndexSyncService;
		this.searchProperties = searchProperties;
	}

	@Transactional(readOnly = true)
	public CmsSearchOverviewVO overview() {
		CmsSearchOverviewVO vo = new CmsSearchOverviewVO();
		SearchProperties search = searchProperties.getIfAvailable();
		if (search != null) {
			vo.setFulltextProvider(search.getFulltext().getProvider());
			vo.setVectorEnabled(search.getVector().isEnabled());
			vo.setVectorProvider(search.getVector().getProvider());
			vo.setHybridRrfEnabled(search.getHybrid().isRrfEnabled());
		}
		vo.setCmsTextNamespaces(CmsSearchNamespaces.CMS_PUBLIC + ", " + CmsSearchNamespaces.CMS_INTERNAL);
		return vo;
	}

	public CmsSearchRebuildSummary rebuildAll(Long operatorUserId) {
		if (!this.isAdmin(operatorUserId)) {
			throw new IllegalStateException("无权限重建检索索引");
		}
		CmsSearchIndexSyncService sync = cmsSearchIndexSyncService.getIfAvailable();
		if (sync == null) {
			throw new IllegalStateException("检索同步服务未启用");
		}
		List<KPubs> all = loadAllIndexableCanonicals();
		int[] textCounts = sync.rebuildFullTextIndex(all);
		int vectorAttempts = 0;
		for (int i = 0; i < all.size(); i++) {
			sync.syncCanonicalVectorsOnly(all.get(i));
			vectorAttempts++;
		}
		CmsSearchRebuildSummary summary = new CmsSearchRebuildSummary();
		summary.setCanonicalCount(all.size());
		summary.setPublicTextCount(textCounts[0]);
		summary.setInternalTextCount(textCounts[1]);
		summary.setVectorUpsertAttempts(vectorAttempts);
		return summary;
	}

	private List<KPubs> loadAllIndexableCanonicals() {
		long total = pubDao.countIndexableCanonicals();
		List<KPubs> all = new ArrayList<KPubs>((int) Math.min(total, Integer.MAX_VALUE));
		long offset = 0L;
		while (offset < total) {
			List<KPubs> page = pubDao.pageIndexableCanonicals(REINDEX_PAGE_SIZE, offset);
			if (page == null || page.isEmpty()) {
				break;
			}
			all.addAll(page);
			offset += page.size();
		}
		return all;
	}
}
