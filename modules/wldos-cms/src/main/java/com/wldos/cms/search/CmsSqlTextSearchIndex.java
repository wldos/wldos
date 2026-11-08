/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.search;

import com.wldos.cms.service.PubService;
import com.wldos.cms.vo.SPub;

import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.search.api.SearchCapability;
import io.github.wldos.search.api.TextSearchIndex;
import io.github.wldos.search.api.model.SearchScope;
import io.github.wldos.search.api.model.TextIndexDocument;
import io.github.wldos.search.api.model.TextSearchHit;
import io.github.wldos.search.api.model.TextSearchRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * CMS MySQL {@code instr} 全文降级（与现网 {@link PubService#searchPubs} 语义一致）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class CmsSqlTextSearchIndex implements TextSearchIndex {

	private final String namespace;
	private final boolean includeInternal;
	private final PubService pubService;

	public CmsSqlTextSearchIndex(String namespace, boolean includeInternal, PubService pubService) {
		this.namespace = namespace;
		this.includeInternal = includeInternal;
		this.pubService = pubService;
	}

	@Override
	public String namespace() {
		return namespace;
	}

	@Override
	public Set<SearchCapability> capabilities() {
		return EnumSet.noneOf(SearchCapability.class);
	}

	@Override
	public void indexDocument(TextIndexDocument document) {
	}

	@Override
	public void deleteDocument(String chunkId) {
	}

	@Override
	public void rebuild(List<TextIndexDocument> documents) {
	}

	@Override
	public List<TextSearchHit> search(TextSearchRequest request) {
		if (request == null || request.getQueryText() == null || request.getQueryText().trim().length() == 0
				|| request.getTopK() <= 0) {
			return Collections.emptyList();
		}
		SearchScope scope = request.getScope();
		Long domainId = scope == null ? null : scope.getDomainId();
		if (domainId == null) {
			return Collections.emptyList();
		}
		PageQuery pageQuery = new PageQuery();
		pageQuery.setCurrent(1);
		pageQuery.setPageSize(request.getTopK());
		String keywords = request.getQueryText().trim();
		PageData<SPub> page;
		if (includeInternal) {
			page = pubService.searchInternalPubs(domainId, pageQuery, keywords);
		}
		else {
			page = pubService.searchPubs(domainId, pageQuery, keywords);
		}
		if (page == null || page.getRows() == null || page.getRows().isEmpty()) {
			return Collections.emptyList();
		}
		List<TextSearchHit> hits = new ArrayList<TextSearchHit>();
		for (int i = 0; i < page.getRows().size(); i++) {
			SPub row = page.getRows().get(i);
			TextSearchHit hit = new TextSearchHit();
			if (row.getId() != null) {
				hit.setChunkId(String.valueOf(row.getId()));
			}
			hit.setTitle(row.getPubTitle());
			hit.setSnippet(row.getPubTitle());
			hit.setScore(1.0d - i * 0.01d);
			hits.add(hit);
		}
		return hits;
	}
}
