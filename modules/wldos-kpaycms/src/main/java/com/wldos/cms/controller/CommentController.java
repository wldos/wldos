/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import java.util.List;

import com.wldos.framework.mvc.controller.EntityController;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;

/**
 * 评论controller。
 *
 * @author 元悉宇宙
 * @date 2021/6/12
 * @version 1.0
 */
@Api(tags = "评论管理")
@RequestMapping("cms/comment")
@RestController
public class CommentController extends EntityController<CommentService, KComments> {


	@ApiOperation(value = "查询评论列表", notes = "根据内容ID查询评论列表")
	@GetMapping("/{pubId:\\d+}")
	public List<Comment> queryPubComments(@ApiParam(value = "内容ID", required = true) @PathVariable Long pubId) {

		return this.service.queryPubComments(pubId, DeleteFlagEnum.NORMAL.toString(), ApproveStatusEnum.APPROVED.getValue());
	}

	@ApiOperation(value = "提交评论", notes = "提交新的评论")
	@PostMapping("commit")
	public Long comment(@ApiParam(value = "评论信息", required = true) @Valid @RequestBody KComments entity) {

		return this.service.comment(entity, this.getUserId(), this.getUserIp());
	}

	@ApiOperation(value = "删除评论", notes = "删除指定的评论")
	@DeleteMapping("del")
	public Boolean delete(@ApiParam(value = "评论信息", required = true) @Valid @RequestBody KComments entity) {
		this.service.deleteComment(entity);

		return Boolean.TRUE;
	}
}
