/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.cms.dao.CommentDao;
import com.wldos.common.dto.SQLTable;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.cms.entity.KComments;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.enums.ApproveStatusEnum;
import com.wldos.cms.vo.AuditComment;
import com.wldos.cms.vo.Comment;
import com.wldos.common.Constants;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.TreeUtils;
import com.wldos.platform.support.auth.vo.UserInfo;
import com.wldos.platform.core.service.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 评论service。
 *
 * @author 元悉宇宙
 * @date 2021/6/17
 * @version 1.0
 */
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class CommentService extends EntityService<CommentDao, KComments, Long> {

	@Value("${wldos_cms_comment_audit:false}")
	private String auditFlag;

	private final UserService userService;

	private final PubService pubService;

	public CommentService(UserService userService, PubService pubService) {
		this.userService = userService;
		this.pubService = pubService;
	}

	private final BeanCopier kCopier = BeanCopier.create(KComments.class, Comment.class, false);

	/**
	 * 查询发布内容评论
	 *
	 * @param pubId 发布内容id
	 * @param delFlag 删除标识
	 * @param appStatus 审核状态
	 * @return 评论列表
	 */
	public List<Comment> queryPubComments(Long pubId, String delFlag, String appStatus) {
		List<KComments> comments = this.entityRepo.queryCommentsByPubId(pubId, delFlag, appStatus);

		if (ObjectUtils.isBlank(comments))
			return new ArrayList<>();

		List<Long> userIds = comments.parallelStream().map(KComments::getCreateBy).collect(Collectors.toList());

		List<UserInfo> userInfos = this.userService.queryUsersInfo(userIds);
		// 归集用户头像
		Map<Long, String> userAvatars = userInfos.parallelStream().collect(Collectors.toMap(UserInfo::getId, UserInfo::getAvatar, (k1, k2) -> k1));

		List<Comment> commentList =
				comments.parallelStream().map(item -> {
					Comment comment = new Comment();
					this.kCopier.copy(item, comment, null);
					comment.setDatetime(item.getCreateTime());
					// 取出头像
					comment.setAvatar(userAvatars.get(item.getCreateBy()));

					return comment;
				}).collect(Collectors.toList());

		return TreeUtils.build(commentList, Constants.TOP_COMMENT_ID);
	}

	/**
	 * 发表评论
	 *
	 * @param entity 评论内容
	 * @param curUserId 评论人id
	 * @param userIp 评论人ip
	 * @return 评论主键id
	 */
	public Long comment(KComments entity, Long curUserId, String userIp) {

		entity.setApproved("true".equals(this.auditFlag) /* 开启评论审核 */
				? ApproveStatusEnum.APPROVING.getValue() : ApproveStatusEnum.APPROVED.getValue());
		this.saveOrUpdate(entity);
		Long id = entity.getId();
		// 更新发布内容评论数
		Long pId = entity.getPubId();
		this.pubService.updateCommentCount(pId, 1);

		return id;
	}

	/**
	 * 删除一条评论
	 *
	 * @param entity 评论数据
	 */
	public void deleteComment(KComments entity) {
		KComments comment = this.findById(entity.getId());
		this.delete(entity);
		Long pId = comment.getPubId();
		if (pId == null) {
			return;
		}
		this.pubService.updateCommentCount(pId, -1);
	}

	/**
	 * 批量删除评论
	 *
	 * @param ids 评论ids
	 */
	public void deleteBatchComment(Object[] ids) {
		for (Object id : ids) {
			KComments comment = new KComments();
			comment.setId(Long.parseLong(id.toString()));
			this.deleteComment(comment);
		}
	}

	/**
	 * 驳回评论
	 *
	 * @param entity 评论数据
	 */
	public void rejectComment(KComments entity) {
		this.entityRepo.changeCommentStatus(entity.getId(), ApproveStatusEnum.APPROVING.getValue());
	}

	/**
	 * 审核通过评论
	 *
	 * @param entity 评论数据
	 */
	public void auditComment(KComments entity) {
		this.entityRepo.changeCommentStatus(entity.getId(), ApproveStatusEnum.APPROVED.getValue());
	}

	/**
	 * 垃圾评论
	 *
	 * @param entity 评论数据
	 */
	public void spamComment(KComments entity) {
		this.entityRepo.changeCommentStatus(entity.getId(), ApproveStatusEnum.SPAM.getValue());
	}

	/**
	 * 回收站评论
	 *
	 * @param entity 评论数据
	 */
	public void trashComment(KComments entity) {
		this.entityRepo.changeCommentStatus(entity.getId(), ApproveStatusEnum.TRASH.getValue());
	}

	/**
	 * 查询可管理的评论列表
	 *
	 * @param pageQuery 查询条件
	 * @return 评论列表
	 */
	public PageableResult<AuditComment> queryAdminComments(PageQuery pageQuery) {
		PageableResult<AuditComment> commentPage = this.execQueryForPage(AuditComment.class, "select c.* from k_comments c LEFT JOIN k_pubs p on c.pub_id=p.id", pageQuery,
				SQLTable.of(KComments.class, "c"), SQLTable.of(KPubs.class, "p"));

		List<AuditComment> comments = commentPage.getData().getRows();

		if (comments == null || comments.isEmpty())
			return commentPage;

		List<Long> pubIds = comments.parallelStream().map(AuditComment::getPubId).collect(Collectors.toList());
		List<Long> userIds = comments.parallelStream().map(AuditComment::getCreateBy).collect(Collectors.toList());

		List<UserInfo> userInfos = this.userService.queryUsersInfo(userIds);
		// 归集用户头像
		Map<Long, String> userAvatars = userInfos.parallelStream().collect(Collectors.toMap(UserInfo::getId, UserInfo::getAvatar, (k1, k2) -> k1));

		List<KPubs> pubs = this.pubService.queryPubsByIds(pubIds);
		// 归集内容
		Map<Long, KPubs> pubMap = pubs.parallelStream().collect(Collectors.toMap(KPubs::getId, p -> p, (k1, k2) -> k1));

		comments = comments.stream().map(comment -> {
			Long pId = comment.getPubId();
			Long uId = comment.getCreateBy();

			// 设置头像
			comment.setAvatar(userAvatars.get(uId));

			// 设置内容名称 和 评论数
			KPubs pub = pubMap.get(pId);
			if (pub == null)
				return comment;
			comment.setPubTitle(ObjectUtils.string(pub.getPubTitle()));
			comment.setCommentCount(ObjectUtils.nvlToZero(pub.getCommentCount()));
			return comment;
		}).collect(Collectors.toList());

		commentPage.setDataRows(comments);

		return commentPage;
	}
}
