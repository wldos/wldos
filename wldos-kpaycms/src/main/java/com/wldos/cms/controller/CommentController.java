

package com.wldos.cms.controller;

import java.util.List;

import com.wldos.cms.enums.ApproveStatusEnum;
import com.wldos.cms.entity.KComments;
import com.wldos.cms.service.CommentService;
import com.wldos.cms.vo.Comment;
import com.wldos.support.controller.RepoController;
import com.wldos.support.enums.DeleteFlagEnum;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("cms/comment")
@RestController
public class CommentController extends RepoController<CommentService, KComments> {


	@GetMapping("/{postId:\\d+}")
	public List<Comment> queryPostComments(@PathVariable Long postId) {

		return this.service.queryPostComments(postId, DeleteFlagEnum.NORMAL.toString(), ApproveStatusEnum.APPROVED.getValue());
	}

	@PostMapping("commit")
	public Long comment(@RequestBody KComments entity) {

		return this.service.comment(entity, this.getCurUserId(), this.getUserIp());
	}

	@DeleteMapping("del")
	public Boolean delete(@RequestBody KComments entity) {
		this.service.deleteComment(entity);

		return Boolean.TRUE;
	}
}
