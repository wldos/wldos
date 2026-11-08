/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.search;

import com.wldos.cms.service.PubService;

import io.github.wldos.ai.api.AiEmbeddingExecutor;
import io.github.wldos.ai.autoconfigure.AiProperties;
import io.github.wldos.search.api.TextSearchIndex;
import io.github.wldos.search.api.TextSearchIndexRegistry;
import io.github.wldos.search.api.VectorStoreRegistry;
import io.github.wldos.search.autoconfigure.SearchProperties;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code wldos.index.provider=sql} 时注册 CMS 公开/内部 SQL 全文索引；lucene8 时叠 SQL 读路径兜底。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Configuration
@AutoConfigureAfter(name = "io.github.wldos.search.autoconfigure.SearchSdkAutoConfiguration")
public class CmsSearchAutoConfiguration {

	@Bean
	public CmsSearchIndexSyncService cmsSearchIndexSyncService(TextSearchIndexRegistry textSearchIndexRegistry,
			ObjectProvider<VectorStoreRegistry> vectorStoreRegistries,
			ObjectProvider<AiEmbeddingExecutor> embeddingExecutors,
			ObjectProvider<SearchProperties> searchProperties, ObjectProvider<AiProperties> aiProperties) {
		return new CmsSearchIndexSyncService(textSearchIndexRegistry, vectorStoreRegistries, embeddingExecutors,
				searchProperties, aiProperties);
	}

	@Bean
	@ConditionalOnProperty(name = "wldos.index.provider", havingValue = "sql")
	public TextSearchIndex cmsPublicSqlTextSearchIndex(PubService pubService) {
		return new CmsSqlTextSearchIndex(CmsSearchNamespaces.CMS_PUBLIC, false, pubService);
	}

	@Bean
	@ConditionalOnProperty(name = "wldos.index.provider", havingValue = "sql")
	public TextSearchIndex cmsInternalSqlTextSearchIndex(PubService pubService) {
		return new CmsSqlTextSearchIndex(CmsSearchNamespaces.CMS_INTERNAL, true, pubService);
	}

	@Bean
	@ConditionalOnProperty(name = "wldos.index.provider", havingValue = "lucene8")
	public TextSearchIndex cmsPublicSqlTextSearchFallback(PubService pubService) {
		return new CmsSqlTextSearchIndex(CmsSearchNamespaces.CMS_PUBLIC, false, pubService);
	}

	@Bean
	@ConditionalOnProperty(name = "wldos.index.provider", havingValue = "lucene8")
	public TextSearchIndex cmsInternalSqlTextSearchFallback(PubService pubService) {
		return new CmsSqlTextSearchIndex(CmsSearchNamespaces.CMS_INTERNAL, true, pubService);
	}
}
