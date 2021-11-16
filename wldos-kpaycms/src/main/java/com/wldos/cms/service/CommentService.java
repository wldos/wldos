

package com.wldos.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.cms.enums.ApproveStatusEnum;
import com.wldos.cms.repo.CommentRepo;
import com.wldos.support.controller.EntityAssists;
import com.wldos.support.service.BaseService;
import com.wldos.support.Constants;
import com.wldos.cms.entity.KComments;
import com.wldos.cms.vo.Comment;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.service.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

	public List<Comment> queryPostComments(Long postId, String delFlag, String appStatus) {
		List<KComments> comments = this.entityRepo.queryCommentsByPostId(postId, delFlag, appStatus);

		if (ObjectUtil.isBlank(comments))
			return new ArrayList<>();

		List<Long> userIds = comments.parallelStream().map(KComments::getCreateBy).collect(Collectors.toList());

		List<UserInfo> userInfos = this.userService.queryUsersInfo(userIds);

		Map<Long, String> userAvatars = userInfos.parallelStream().collect(Collectors.toMap(UserInfo::getId, UserInfo::getAvatar, (k1, k2) -> k1));

		List<Comment> commentList =
				comments.parallelStream().map(item -> {
					Comment comment = new Comment();
					this.kCopier.copy(item, comment, null);
					comment.setDatetime(item.getCreateTime());

					comment.setAvatar(userAvatars.get(item.getCreateBy()));

					return comment;
				}).collect(Collectors.toList());

		return TreeUtil.build(commentList, Constants.TOP_COMMENT_ID);
	}


	public Long comment(KComments entity, Long curUserId, String userIp) {
		Long id = this.nextId();
		EntityAssists.beforeInsert(entity, id, curUserId, userIp, false);
		entity.setApproved("true".equals(this.auditFlag)
				? ApproveStatusEnum.APPROVING.getValue() : ApproveStatusEnum.APPROVED.getValue());
		this.insertSelective(entity);

		Long postId = entity.getPostId();
		this.postService.updateCommentCount(postId, 1);

		return id;
	}


	public void deleteComment(KComments entity) {
		this.delete(entity);
		Long postId = entity.getPostId();
		this.postService.updateCommentCount(postId, -1);
	}
}
