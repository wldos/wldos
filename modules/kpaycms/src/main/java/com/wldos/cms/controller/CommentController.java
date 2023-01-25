/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.util.List;

import com.wldos.base.controller.RepoController;
import com.wldos.cms.entity.KComments;
import com.wldos.cms.enums.ApproveStatusEnum;
import com.wldos.cms.service.CommentService;
import com.wldos.cms.vo.Comment;
import com.wldos.common.enums.DeleteFlagEnum;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论controller。
 *
 * @author 树悉猿
 * @date 2021/6/12
 * @version 1.0
 */
@RequestMapping("cms/comment")
@RestController
public class CommentController extends RepoController<CommentService, KComments> {


	@GetMapping("/{pubId:\\d+}")
	public List<Comment> queryPubComments(@PathVariable Long pubId) {

		return this.service.queryPubComments(pubId, DeleteFlagEnum.NORMAL.toString(), ApproveStatusEnum.APPROVED.getValue());
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
