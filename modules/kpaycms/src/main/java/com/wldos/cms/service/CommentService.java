/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.base.entity.EntityAssists;
import com.wldos.base.service.BaseService;
import com.wldos.cms.entity.KComments;
import com.wldos.cms.enums.ApproveStatusEnum;
import com.wldos.cms.repo.CommentRepo;
import com.wldos.cms.vo.Comment;
import com.wldos.common.Constants;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.TreeUtils;
import com.wldos.sys.core.service.UserService;
import com.wldos.support.auth.vo.UserInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 评论service。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommentService extends BaseService<CommentRepo, KComments, Long> {

	@Value("${wldos.cms.comment.audit}")
	private String auditFlag;

	private final UserService userService;

	private final PostService postService;

	public CommentService(UserService userService, PostService postService) {
		this.userService = userService;
		this.postService = postService;
	}

	private final BeanCopier kCopier = BeanCopier.create(KComments.class, Comment.class, false);

	/**
	 * 查询帖子评论
	 *
	 * @param postId 帖子id
	 * @param delFlag 删除标识
	 * @param appStatus 审核状态
	 * @return 评论列表
	 */
	public List<Comment> queryPostComments(Long postId, String delFlag, String appStatus) {
		List<KComments> comments = this.entityRepo.queryCommentsByPostId(postId, delFlag, appStatus);

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
		Long id = this.nextId();
		EntityAssists.beforeInsert(entity, id, curUserId, userIp, false);
		entity.setApproved("true".equals(this.auditFlag) /* 开启评论审核 */
				? ApproveStatusEnum.APPROVING.getValue() : ApproveStatusEnum.APPROVED.getValue());
		this.insertSelective(entity);
		// 更新帖子评论数
		Long postId = entity.getPostId();
		this.postService.updateCommentCount(postId, 1);

		return id;
	}

	/**
	 * 删除一条评论
	 *
	 * @param entity 评论数据
	 */
	public void deleteComment(KComments entity) {
		this.delete(entity);
		Long postId = entity.getPostId();
		this.postService.updateCommentCount(postId, -1);
	}
}
