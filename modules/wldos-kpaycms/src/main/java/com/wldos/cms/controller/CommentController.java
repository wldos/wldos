/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import java.util.List;

import com.wldos.framework.controller.RepoController;
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
 * @author 元悉宇宙
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

		return this.service.comment(entity, this.getUserId(), this.getUserIp());
	}

	@DeleteMapping("del")
	public Boolean delete(@RequestBody KComments entity) {
		this.service.deleteComment(entity);

		return Boolean.TRUE;
	}
}
