/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.search;

import com.wldos.cms.entity.KPubs;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.query.CmsKpubsDiscoverySql;

import io.github.wldos.common.enums.DeleteFlagEnum;

import io.github.wldos.ai.api.AiEmbeddingExecutor;
import io.github.wldos.ai.api.model.AiEmbeddingRequest;
import io.github.wldos.ai.api.model.AiEmbeddingResponse;
import io.github.wldos.ai.api.model.AiInvocationContext;
import io.github.wldos.search.api.TextSearchIndexRegistry;
import io.github.wldos.search.api.VectorProvider;
import io.github.wldos.search.api.VectorStore;
import io.github.wldos.search.api.VectorStoreRegistry;
import io.github.wldos.search.api.model.SearchScope;
import io.github.wldos.search.api.model.TextIndexDocument;
import io.github.wldos.search.api.model.VectorDocument;
import io.github.wldos.ai.autoconfigure.AiProperties;
import io.github.wldos.search.autoconfigure.SearchProperties;
import io.github.wldos.search.support.EmbedInputDigest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;

/**
 * 内容发布/下线时同步 Lucene 全文与可选向量索引（provider=sql 时全文 Bean 为 no-op 写入）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public class CmsSearchIndexSyncService {

	private static final int EMBED_TEXT_MAX_CHARS = 8000;

	private final TextSearchIndexRegistry textSearchIndexRegistry;
	private final ObjectProvider<VectorStoreRegistry> vectorStoreRegistries;
	private final ObjectProvider<AiEmbeddingExecutor> embeddingExecutors;
	private final ObjectProvider<SearchProperties> searchProperties;
	private final ObjectProvider<AiProperties> aiProperties;

	public CmsSearchIndexSyncService(TextSearchIndexRegistry textSearchIndexRegistry,
			ObjectProvider<VectorStoreRegistry> vectorStoreRegistries,
			ObjectProvider<AiEmbeddingExecutor> embeddingExecutors,
			ObjectProvider<SearchProperties> searchProperties, ObjectProvider<AiProperties> aiProperties) {
		this.textSearchIndexRegistry = textSearchIndexRegistry;
		this.vectorStoreRegistries = vectorStoreRegistries;
		this.embeddingExecutors = embeddingExecutors;
		this.searchProperties = searchProperties;
		this.aiProperties = aiProperties;
	}

	public void syncCanonical(KPubs pub) {
		if (pub == null || pub.getId() == null || pub.getOriginPubId() != null) {
			return;
		}
		String chunkId = String.valueOf(pub.getId());
		if (!isIndexableCanonical(pub)) {
			remove(chunkId);
			return;
		}
		String visibility = normalizeVisibility(pub.getVisibilityScope());
		if (CmsKpubsDiscoverySql.SCOPE_UNLISTED.equals(visibility)) {
			remove(chunkId);
			return;
		}
		String title = pub.getPubTitle() == null ? "" : pub.getPubTitle();
		String body = pub.getPubContent() == null ? "" : pub.getPubContent();
		TextIndexDocument document = new TextIndexDocument();
		document.setChunkId(chunkId);
		document.setTitle(title);
		document.setBody(body);
		if (CmsKpubsDiscoverySql.SCOPE_INTERNAL_ONLY.equals(visibility)) {
			removeFromNamespace(CmsSearchNamespaces.CMS_PUBLIC, chunkId);
			indexNamespace(CmsSearchNamespaces.CMS_INTERNAL, document);
			upsertVector(CmsSearchNamespaces.CMS_INTERNAL, chunkId, title, body);
		}
		else {
			removeFromNamespace(CmsSearchNamespaces.CMS_INTERNAL, chunkId);
			indexNamespace(CmsSearchNamespaces.CMS_PUBLIC, document);
			upsertVector(CmsSearchNamespaces.CMS_PUBLIC, chunkId, title, body);
		}
	}

	/**
	 * 全量重建 Lucene（或 registry 内全文实现）的 cms-public / cms-internal 分库。
	 *
	 * @return [publicCount, internalCount]
	 */
	public int[] rebuildFullTextIndex(List<KPubs> canonicals) {
		List<TextIndexDocument> publicDocs = new ArrayList<TextIndexDocument>();
		List<TextIndexDocument> internalDocs = new ArrayList<TextIndexDocument>();
		if (canonicals != null) {
			for (int i = 0; i < canonicals.size(); i++) {
				KPubs pub = canonicals.get(i);
				if (!isIndexableCanonical(pub)) {
					continue;
				}
				String visibility = normalizeVisibility(pub.getVisibilityScope());
				if (CmsKpubsDiscoverySql.SCOPE_UNLISTED.equals(visibility)) {
					continue;
				}
				TextIndexDocument document = toTextDocument(pub);
				if (CmsKpubsDiscoverySql.SCOPE_INTERNAL_ONLY.equals(visibility)) {
					internalDocs.add(document);
				}
				else {
					publicDocs.add(document);
				}
			}
		}
		rebuildNamespace(CmsSearchNamespaces.CMS_PUBLIC, publicDocs);
		rebuildNamespace(CmsSearchNamespaces.CMS_INTERNAL, internalDocs);
		return new int[] { publicDocs.size(), internalDocs.size() };
	}

	/** 仅同步向量侧（全文已 rebuild 时调用）。 */
	public void syncCanonicalVectorsOnly(KPubs pub) {
		if (pub == null || pub.getId() == null || pub.getOriginPubId() != null) {
			return;
		}
		String chunkId = String.valueOf(pub.getId());
		if (!isIndexableCanonical(pub)) {
			removeVectorFromNamespace(CmsSearchNamespaces.CMS_PUBLIC, chunkId);
			removeVectorFromNamespace(CmsSearchNamespaces.CMS_INTERNAL, chunkId);
			return;
		}
		String visibility = normalizeVisibility(pub.getVisibilityScope());
		if (CmsKpubsDiscoverySql.SCOPE_UNLISTED.equals(visibility)) {
			removeVectorFromNamespace(CmsSearchNamespaces.CMS_PUBLIC, chunkId);
			removeVectorFromNamespace(CmsSearchNamespaces.CMS_INTERNAL, chunkId);
			return;
		}
		String title = pub.getPubTitle() == null ? "" : pub.getPubTitle();
		String body = pub.getPubContent() == null ? "" : pub.getPubContent();
		if (CmsKpubsDiscoverySql.SCOPE_INTERNAL_ONLY.equals(visibility)) {
			removeVectorFromNamespace(CmsSearchNamespaces.CMS_PUBLIC, chunkId);
			upsertVector(CmsSearchNamespaces.CMS_INTERNAL, chunkId, title, body);
		}
		else {
			removeVectorFromNamespace(CmsSearchNamespaces.CMS_INTERNAL, chunkId);
			upsertVector(CmsSearchNamespaces.CMS_PUBLIC, chunkId, title, body);
		}
	}

	static boolean isIndexableCanonical(KPubs pub) {
		if (pub == null || pub.getOriginPubId() != null) {
			return false;
		}
		if (pub.getDeleteFlag() != null && !DeleteFlagEnum.NORMAL.toString().equals(pub.getDeleteFlag())) {
			return false;
		}
		String status = pub.getPubStatus();
		return PubStatusEnum.PUBLISH.getValue().equals(status) || PubStatusEnum.INHERIT.getValue().equals(status);
	}

	private static TextIndexDocument toTextDocument(KPubs pub) {
		TextIndexDocument document = new TextIndexDocument();
		document.setChunkId(String.valueOf(pub.getId()));
		document.setTitle(pub.getPubTitle() == null ? "" : pub.getPubTitle());
		document.setBody(pub.getPubContent() == null ? "" : pub.getPubContent());
		return document;
	}

	private void rebuildNamespace(String namespace, List<TextIndexDocument> documents) {
		SearchScope scope = new SearchScope();
		scope.setNamespace(namespace);
		textSearchIndexRegistry.resolve(scope).rebuild(documents);
	}

	public void removeCanonical(Long pubId) {
		if (pubId == null) {
			return;
		}
		remove(String.valueOf(pubId));
	}

	private void remove(String chunkId) {
		removeFromNamespace(CmsSearchNamespaces.CMS_PUBLIC, chunkId);
		removeFromNamespace(CmsSearchNamespaces.CMS_INTERNAL, chunkId);
	}

	private void indexNamespace(String namespace, TextIndexDocument document) {
		SearchScope scope = new SearchScope();
		scope.setNamespace(namespace);
		textSearchIndexRegistry.resolve(scope).indexDocument(document);
	}

	private void removeFromNamespace(String namespace, String chunkId) {
		SearchScope scope = new SearchScope();
		scope.setNamespace(namespace);
		textSearchIndexRegistry.resolve(scope).deleteDocument(chunkId);
		removeVectorFromNamespace(namespace, chunkId);
	}

	private void removeVectorFromNamespace(String namespace, String chunkId) {
		VectorStoreRegistry vectorRegistry = vectorStoreRegistries.getIfAvailable();
		if (vectorRegistry == null) {
			return;
		}
		SearchScope scope = new SearchScope();
		scope.setNamespace(namespace);
		vectorRegistry.resolve(scope).delete(chunkId);
	}

	private void upsertVector(String namespace, String chunkId, String title, String body) {
		SearchProperties properties = searchProperties.getIfAvailable();
		AiProperties ai = aiProperties == null ? null : aiProperties.getIfAvailable();
		if (properties == null || !properties.getVector().isEnabled()) {
			return;
		}
		VectorProvider provider = VectorProvider.fromConfig(properties.getVector().getProvider());
		if (provider == VectorProvider.NOOP) {
			return;
		}
		VectorStoreRegistry vectorRegistry = vectorStoreRegistries.getIfAvailable();
		AiEmbeddingExecutor executor = embeddingExecutors.getIfAvailable();
		if (vectorRegistry == null || executor == null) {
			return;
		}
		String embedInput = embedInput(title, body);
		String digest = EmbedInputDigest.sha256Hex(embedInput);
		String modelKey = cmsEmbeddingModelKey(properties, ai);
		SearchScope scope = new SearchScope();
		scope.setNamespace(namespace);
		VectorStore store = vectorRegistry.resolve(scope);
		if (store.hasSameFingerprint(chunkId, digest, modelKey)) {
			return;
		}
		float[] vector = embed(properties, ai, executor, embedInput);
		if (vector == null) {
			return;
		}
		VectorDocument doc = new VectorDocument();
		doc.setChunkId(chunkId);
		doc.setVector(vector);
		doc.setTitle(title);
		doc.setSnippet(truncate(stripTags(body), 512));
		doc.setContentDigest(digest);
		doc.setEmbeddingModelId(modelKey);
		store.upsert(doc);
	}

	private static String embedInput(String title, String body) {
		return truncate((title == null ? "" : title) + "\n" + stripTags(body == null ? "" : body), EMBED_TEXT_MAX_CHARS)
				.trim();
	}

	private String cmsEmbeddingModelKey(SearchProperties properties, AiProperties ai) {
		StringBuilder key = new StringBuilder("cms");
		String model = ai != null && ai.getEmbedding() != null ? ai.getEmbedding().resolvedModel() : null;
		if (model != null && model.length() > 0) {
			key.append(':').append(model);
		}
		return key.toString();
	}

	private float[] embed(SearchProperties properties, AiProperties ai, AiEmbeddingExecutor executor,
			String combined) {
		if (combined == null || combined.isEmpty()) {
			return null;
		}
		AiEmbeddingRequest request = new AiEmbeddingRequest();
		request.setInput(combined);
		if (ai != null && ai.getEmbedding() != null) {
			String m = ai.getEmbedding().resolvedModel();
			if (m != null) {
				request.setModelId(m);
			}
		}
		AiEmbeddingResponse response = executor.embed(new AiInvocationContext(), request);
		if (response == null || !response.isSuccess() || response.getEmbedding() == null) {
			return null;
		}
		double[] embedding = response.getEmbedding();
		float[] out = new float[embedding.length];
		for (int i = 0; i < embedding.length; i++) {
			out[i] = (float) embedding[i];
		}
		return out;
	}

	private static String normalizeVisibility(String scope) {
		if (scope == null || scope.trim().isEmpty()) {
			return CmsKpubsDiscoverySql.SCOPE_PUBLIC_LISTED;
		}
		return scope.trim();
	}

	private static String stripTags(String html) {
		if (html == null || html.length() == 0) {
			return "";
		}
		return html.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
	}

	private static String truncate(String text, int max) {
		if (text == null || text.length() <= max) {
			return text == null ? "" : text;
		}
		return text.substring(0, max);
	}
}
