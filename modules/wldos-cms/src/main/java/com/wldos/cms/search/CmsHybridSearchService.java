/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.search;

import com.wldos.cms.dao.PubDao;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.service.PubService;
import com.wldos.cms.vo.SPub;

import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.search.api.HybridRetrievalService;
import io.github.wldos.search.api.model.SearchMatchChannel;
import io.github.wldos.search.api.model.SearchScope;
import io.github.wldos.search.api.model.TextSearchHit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * CMS 门户/管理端混合检索（BM25 + 向量 + RRF，namespace cms-public / cms-internal）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class CmsHybridSearchService {

	private static final int DEFAULT_TOP_K = 20;

	private final ObjectProvider<HybridRetrievalService> hybridRetrievalService;
	private final PubService pubService;
	private final PubDao pubDao;

	public CmsHybridSearchService(ObjectProvider<HybridRetrievalService> hybridRetrievalService, PubService pubService,
			PubDao pubDao) {
		this.hybridRetrievalService = hybridRetrievalService;
		this.pubService = pubService;
		this.pubDao = pubDao;
	}

	public PageData<SPub> searchHybrid(Long domainId, PageQuery pageQuery, String keywords, boolean includeInternal) {
		if (keywords == null || keywords.trim().isEmpty()) {
			return new PageData<>();
		}
		int topK = pageQuery != null && pageQuery.getPageSize() > 0 ? pageQuery.getPageSize() : DEFAULT_TOP_K;
		HybridRetrievalService hybrid = hybridRetrievalService.getIfAvailable();
		if (hybrid == null) {
			PageData<SPub> fallback = includeInternal
					? pubService.searchInternalPubs(domainId, pageQuery, keywords.trim())
					: pubService.searchPubs(domainId, pageQuery, keywords.trim());
			tagRows(fallback != null ? fallback.getRows() : null, SearchMatchChannel.SQL_FALLBACK, null);
			return fallback;
		}
		SearchScope scope = new SearchScope();
		scope.setDomainId(domainId);
		scope.setNamespace(includeInternal ? CmsSearchNamespaces.CMS_INTERNAL : CmsSearchNamespaces.CMS_PUBLIC);
		List<TextSearchHit> hits = hybrid.hybrid(scope, keywords.trim(), topK);
		if (hits == null || hits.isEmpty()) {
			PageData<SPub> fallback = includeInternal
					? pubService.searchInternalPubs(domainId, pageQuery, keywords.trim())
					: pubService.searchPubs(domainId, pageQuery, keywords.trim());
			tagRows(fallback != null ? fallback.getRows() : null, SearchMatchChannel.SQL_FALLBACK, null);
			return fallback;
		}
		List<SPub> rows = new ArrayList<SPub>();
		if (hits != null) {
			for (int i = 0; i < hits.size(); i++) {
				TextSearchHit hit = hits.get(i);
				SPub row = new SPub();
				if (hit.getChunkId() != null && hit.getChunkId().length() > 0) {
					try {
						row.setId(Long.valueOf(hit.getChunkId()));
					}
					catch (NumberFormatException ignored) {
						// 非数字 chunkId 时仅返回标题
					}
				}
				row.setPubTitle(hit.getTitle());
				row.setMatchChannel(hit.getMatchChannel());
				row.setMatchScore(hit.getScore());
				rows.add(row);
			}
		}
		hydrateRowsFromStore(rows);
		PageData<SPub> page = new PageData<>();
		page.setRows(rows);
		page.setTotal(rows.size());
		page.setCurrent(pageQuery != null ? pageQuery.getCurrent() : 1);
		page.setPageSize(topK);
		return page;
	}

	private static void tagRows(List<SPub> rows, SearchMatchChannel channel, Double score) {
		if (rows == null || rows.isEmpty()) {
			return;
		}
		String wire = channel != null ? channel.getValue() : null;
		for (int i = 0; i < rows.size(); i++) {
			SPub row = rows.get(i);
			if (row == null) {
				continue;
			}
			row.setMatchChannel(wire);
			if (score != null) {
				row.setMatchScore(score);
			}
		}
	}

	private void hydrateRowsFromStore(List<SPub> rows) {
		if (rows == null || rows.isEmpty()) {
			return;
		}
		List<Long> ids = new ArrayList<Long>();
		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).getId() != null) {
				ids.add(rows.get(i).getId());
			}
		}
		if (ids.isEmpty()) {
			return;
		}
		List<KPubs> pubs = pubDao.findAllByIdIn(ids);
		Map<Long, KPubs> byId = new HashMap<Long, KPubs>();
		if (pubs != null) {
			for (int i = 0; i < pubs.size(); i++) {
				KPubs pub = pubs.get(i);
				if (pub.getId() != null) {
					byId.put(pub.getId(), pub);
				}
			}
		}
		for (int i = 0; i < rows.size(); i++) {
			SPub row = rows.get(i);
			if (row.getId() == null) {
				continue;
			}
			KPubs pub = byId.get(row.getId());
			if (pub == null) {
				continue;
			}
			row.setPubType(pub.getPubType());
			if (row.getPubTitle() == null || row.getPubTitle().length() == 0) {
				row.setPubTitle(pub.getPubTitle());
			}
		}
	}
}
