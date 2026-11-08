/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 */

package com.wldos.cms.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import com.wldos.cms.dao.PubDao;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.vo.Pub;
import com.wldos.platform.core.enums.PubTypeEnum;
import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.platform.support.cms.dto.ContModelDto;

import lombok.extern.slf4j.Slf4j;

import com.wldos.cms.search.CmsSearchIndexSyncService;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 已发布作品的草稿快照：创建、提交审核、合并、清理。
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PubDraftService {

	private static final List<String> ACTIVE_DRAFT_STATUSES = Arrays.asList(
			PubStatusEnum.DRAFT.getValue(),
			PubStatusEnum.IN_REVIEW.getValue());

	private static final int CLEANUP_BATCH_SIZE = 500;

	private final PubDao pubDao;

	private final JdbcTemplate jdbcTemplate;

	private final ObjectProvider<CmsSearchIndexSyncService> cmsSearchIndexSyncService;

	@Value("${wldos.cms.draft.merged-retention-days:30}")
	private int mergedRetentionDays;

	public PubDraftService(PubDao pubDao, JdbcTemplate jdbcTemplate,
			ObjectProvider<CmsSearchIndexSyncService> cmsSearchIndexSyncService) {
		this.pubDao = pubDao;
		this.jdbcTemplate = jdbcTemplate;
		this.cmsSearchIndexSyncService = cmsSearchIndexSyncService;
	}

	/**
	 * 是否可对正式行启用草稿改稿（作品壳或章节/元素，且父作品已发布时章节才启用）。
	 */
	public boolean isDraftEligibleCanonical(KPubs canonical) {
		if (canonical == null || canonical.getOriginPubId() != null) {
			return false;
		}
		if (PubTypeEnum.isSub(canonical.getPubType())) {
			Long parentId = canonical.getParentId();
			return parentId != null && parentId > Constants.TOP_PUB_ID && this.parentWorkIsPublished(parentId);
		}
		return canonical.getParentId() != null
				&& canonical.getParentId().equals(Constants.TOP_PUB_ID)
				&& !PubTypeEnum.isSub(canonical.getPubType());
	}

	/** 正式行是否已对 C 端生效（作品 publish / 章节 inherit 且父作品 publish）。 */
	public boolean isPublishedCanonical(KPubs canonical) {
		if (canonical == null || canonical.getOriginPubId() != null) {
			return false;
		}
		if (PubTypeEnum.isSub(canonical.getPubType())) {
			return PubStatusEnum.INHERIT.getValue().equals(canonical.getPubStatus())
					&& canonical.getParentId() != null
					&& canonical.getParentId() > Constants.TOP_PUB_ID
					&& this.parentWorkIsPublished(canonical.getParentId());
		}
		return PubStatusEnum.PUBLISH.getValue().equals(canonical.getPubStatus());
	}

	public KPubs findActiveDraft(Long canonicalId) {
		return this.pubDao.findFirstByOriginPubIdAndPubStatusInAndDeleteFlagOrderByIdDesc(
				canonicalId, ACTIVE_DRAFT_STATUSES, DeleteFlagEnum.NORMAL.toString());
	}

	public boolean hasActiveDraft(Long canonicalId) {
		return this.pubDao.existsByOriginPubIdAndPubStatusInAndDeleteFlag(
				canonicalId, ACTIVE_DRAFT_STATUSES, DeleteFlagEnum.NORMAL.toString());
	}

	public ContModelDto queryWorkingContModel(Long canonicalId) {
		ContModelDto draft = this.pubDao.queryActiveDraftByCanonicalId(canonicalId);
		if (draft != null) {
			return draft;
		}
		return this.pubDao.queryContModel(canonicalId);
	}

	public ContModelDto queryWorkingContModel(Long canonicalId, Long domainId) {
		KPubs draft = this.findActiveDraft(canonicalId);
		if (draft != null) {
			return this.pubDao.queryContModelAny(draft.getId());
		}
		return this.pubDao.queryContModel(canonicalId, domainId);
	}

	/**
	 * 非可信者改已发布正式行：写入或更新活跃草稿。
	 */
	public Long upsertActiveDraft(KPubs canonical, Pub pub, Long userId, String userIp) {
		KPubs draft = this.findActiveDraft(canonical.getId());
		if (draft == null) {
			draft = newDraftFromCanonical(canonical);
			draft.setCreateBy(userId);
			draft.setCreateIp(userIp);
			draft.setCreateTime(new Timestamp(System.currentTimeMillis()));
		}
		applyPubContentToDraft(draft, pub);
		draft.setUpdateBy(userId);
		draft.setUpdateIp(userIp);
		draft.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		this.pubDao.saveOrUpdate(draft);
		return draft.getId();
	}

	/**
	 * 可信者：将提交内容合并进正式行，并归档活跃草稿。
	 */
	public void mergeTrustedEdit(KPubs canonical, Pub pub, Long userId, String userIp) {
		copyPubFieldsToCanonical(canonical, pub);
		canonical.setUpdateBy(userId);
		canonical.setUpdateIp(userIp);
		canonical.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		this.pubDao.saveOrUpdate(canonical);
		this.trashActiveDrafts(canonical.getId(), userId, userIp);
	}

	/** 用户提交草稿审核：draft → in_review。 */
	public void submitDraftForReview(Long draftId) {
		KPubs draft = requireActiveDraftRow(draftId);
		if (!PubStatusEnum.DRAFT.getValue().equals(draft.getPubStatus())) {
			throw new IllegalStateException("仅手动草稿可提交审核");
		}
		this.pubDao.changePubStatus(draftId, PubStatusEnum.IN_REVIEW.getValue());
	}

	/** 管理员驳回：in_review → draft。 */
	public void rejectDraft(Long draftId) {
		KPubs draft = requireActiveDraftRow(draftId);
		if (!PubStatusEnum.IN_REVIEW.getValue().equals(draft.getPubStatus())) {
			throw new IllegalStateException("仅待审草稿可驳回");
		}
		this.pubDao.changePubStatus(draftId, PubStatusEnum.DRAFT.getValue());
	}

	/** 审核通过：草稿合并到正式行，草稿标 trash。 */
	public void mergeDraft(Long canonicalId, Long draftId, Long operatorUserId, String operatorIp) {
		KPubs canonical = requireCanonicalForMerge(canonicalId);
		KPubs draft = this.pubDao.findById(draftId).orElse(null);
		if (draft == null || !canonicalId.equals(draft.getOriginPubId())) {
			throw new IllegalArgumentException("草稿与正式行不匹配");
		}
		if (!PubStatusEnum.IN_REVIEW.getValue().equals(draft.getPubStatus())) {
			throw new IllegalStateException("仅待审草稿可发布合并");
		}
		canonical.setPubTitle(draft.getPubTitle());
		canonical.setPubExcerpt(draft.getPubExcerpt());
		canonical.setPubContent(draft.getPubContent());
		canonical.setPubMimeType(draft.getPubMimeType());
		canonical.setUpdateBy(operatorUserId);
		canonical.setUpdateIp(operatorIp);
		canonical.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		this.pubDao.saveOrUpdate(canonical);

		draft.setPubStatus(PubStatusEnum.TRASH.getValue());
		draft.setUpdateBy(operatorUserId);
		draft.setUpdateIp(operatorIp);
		draft.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		this.pubDao.saveOrUpdate(draft);

		if (mergedRetentionDays <= 0) {
			this.pubDao.deleteById(draftId);
		}
		syncSearchIndex(canonical);
	}

	/** 可信者可直接合并 draft 状态草稿（保存即发布）。 */
	public void mergeDraftAllowDraftStatus(Long canonicalId, Long draftId, Long operatorUserId, String operatorIp) {
		KPubs draft = this.pubDao.findById(draftId).orElse(null);
		if (draft == null || !canonicalId.equals(draft.getOriginPubId())) {
			throw new IllegalArgumentException("草稿与正式行不匹配");
		}
		if (!PubStatusEnum.DRAFT.getValue().equals(draft.getPubStatus())
				&& !PubStatusEnum.IN_REVIEW.getValue().equals(draft.getPubStatus())) {
			throw new IllegalStateException("草稿状态不可合并");
		}
		this.pubDao.changePubStatus(draftId, PubStatusEnum.IN_REVIEW.getValue());
		this.mergeDraft(canonicalId, draftId, operatorUserId, operatorIp);
	}

	public void trashActiveDrafts(Long canonicalId, Long userId, String userIp) {
		KPubs draft = this.findActiveDraft(canonicalId);
		if (draft == null) {
			return;
		}
		draft.setPubStatus(PubStatusEnum.TRASH.getValue());
		draft.setUpdateBy(userId);
		draft.setUpdateIp(userIp);
		draft.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		this.pubDao.saveOrUpdate(draft);
	}

	public int cleanupMergedDrafts() {
		if (mergedRetentionDays <= 0) {
			return 0;
		}
		long cutoff = System.currentTimeMillis() - mergedRetentionDays * 86_400_000L;
		Timestamp before = new Timestamp(cutoff);
		int total = 0;
		int deleted;
		do {
			deleted = this.jdbcTemplate.update(
					"DELETE FROM k_pubs WHERE origin_pub_id IS NOT NULL AND pub_status = 'trash' AND update_time < ? LIMIT ?",
					before, CLEANUP_BATCH_SIZE);
			total += deleted;
		} while (deleted >= CLEANUP_BATCH_SIZE);
		if (total > 0) {
			log.info("CMS merged draft cleanup removed {} rows (retentionDays={})", total, mergedRetentionDays);
		}
		return total;
	}

	public Long resolveCanonicalId(Long pubId) {
		KPubs row = this.pubDao.findById(pubId).orElse(null);
		if (row == null) {
			return null;
		}
		return row.getOriginPubId() != null ? row.getOriginPubId() : row.getId();
	}

	private KPubs requireCanonicalForMerge(Long canonicalId) {
		KPubs canonical = this.pubDao.findById(canonicalId).orElse(null);
		if (canonical == null || canonical.getOriginPubId() != null) {
			throw new IllegalArgumentException("正式行不存在");
		}
		if (!this.isPublishedCanonical(canonical)) {
			throw new IllegalStateException("正式行未处于可对外的已发布/继承状态");
		}
		return canonical;
	}

	private boolean parentWorkIsPublished(Long parentId) {
		return this.pubDao.existsByIdAndPubStatusAndDeleteFlag(
				parentId, PubStatusEnum.PUBLISH.getValue(), DeleteFlagEnum.NORMAL.toString());
	}

	private KPubs requireActiveDraftRow(Long draftId) {
		KPubs draft = this.pubDao.findById(draftId).orElse(null);
		if (draft == null || draft.getOriginPubId() == null) {
			throw new IllegalArgumentException("草稿不存在");
		}
		if (!ACTIVE_DRAFT_STATUSES.contains(draft.getPubStatus())) {
			throw new IllegalStateException("非活跃草稿");
		}
		return draft;
	}

	private static KPubs newDraftFromCanonical(KPubs canonical) {
		KPubs draft = new KPubs();
		draft.setOriginPubId(canonical.getId());
		draft.setParentId(canonical.getParentId());
		draft.setPubType(canonical.getPubType());
		draft.setDomainId(canonical.getDomainId());
		draft.setComId(canonical.getComId());
		draft.setPubName(null);
		draft.setPubStatus(PubStatusEnum.DRAFT.getValue());
		draft.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		draft.setCommentStatus(canonical.getCommentStatus());
		draft.setVisibilityScope(canonical.getVisibilityScope());
		draft.setPubTitle(canonical.getPubTitle());
		draft.setPubExcerpt(canonical.getPubExcerpt());
		draft.setPubContent(canonical.getPubContent());
		draft.setPubMimeType(canonical.getPubMimeType());
		return draft;
	}

	private static void applyPubContentToDraft(KPubs draft, Pub pub) {
		if (!ObjectUtils.isBlank(pub.getPubTitle())) {
			draft.setPubTitle(pub.getPubTitle());
		}
		if (pub.getPubExcerpt() != null) {
			draft.setPubExcerpt(pub.getPubExcerpt());
		}
		if (pub.getPubContent() != null) {
			draft.setPubContent(pub.getPubContent());
		}
		if (pub.getPubMimeType() != null) {
			draft.setPubMimeType(pub.getPubMimeType());
		}
	}

	private static void copyPubFieldsToCanonical(KPubs canonical, Pub pub) {
		if (pub.getPubTitle() != null) {
			canonical.setPubTitle(pub.getPubTitle());
		}
		if (pub.getPubExcerpt() != null) {
			canonical.setPubExcerpt(pub.getPubExcerpt());
		}
		if (pub.getPubContent() != null) {
			canonical.setPubContent(pub.getPubContent());
		}
		if (pub.getPubMimeType() != null) {
			canonical.setPubMimeType(pub.getPubMimeType());
		}
	}

	private void syncSearchIndex(KPubs canonical) {
		CmsSearchIndexSyncService sync = cmsSearchIndexSyncService.getIfAvailable();
		if (sync == null || canonical == null) {
			return;
		}
		sync.syncCanonical(canonical);
	}
}
