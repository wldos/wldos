/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import java.util.List;
import java.util.Map;

import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.cms.entity.KComments;
import com.wldos.cms.service.CommentService;
import com.wldos.cms.vo.AuditComment;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@Api(tags = "评论管理（后台）")
@RequestMapping("admin/cms/comment")
@RestController
public class CommentAdminController extends EntityController<CommentService, KComments> {

	/**
	 * 评论管理列表，后端管理功能既有租户隔离，又有域隔离，前端公共信息可以免登录访问只需域隔离
	 *
	 * @return 评论列表
	 */
	@ApiOperation(value = "评论管理列表", notes = "查询评论管理列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "pubId", value = "内容ID", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "approved", value = "审核状态", dataTypeClass = String.class, paramType = "query")
	})
	@RequestMapping("")
	public PageableResult<AuditComment> adminInfo(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		// 业务对象需要作多租隔离处理：当前域-->所有分类 + 当前租户 => 当前管理员可管对象
		this.applyTenantFilter(pageQuery);
		// 应用域隔离
		this.applyDomainFilter(pageQuery);

		return this.service.queryAdminComments(pageQuery);
	}

	/**
	 * 提交评论
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@ApiOperation(value = "提交评论", notes = "提交新的评论")
	@PostMapping("commit")
	public Long comment(@ApiParam(value = "评论信息", required = true) @Valid @RequestBody KComments entity) {

		return this.service.comment(entity, this.getUserId(), this.getUserIp());
	}

	/**
	 * 删除评论
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@ApiOperation(value = "删除评论", notes = "删除指定的评论")
	@DeleteMapping("del")
	public Boolean delete(@ApiParam(value = "评论信息", required = true) @Valid @RequestBody KComments entity) {
		this.service.deleteComment(entity);

		return Boolean.TRUE;
	}

	/**
	 * 批量删除评论
	 *
	 * @param jsonObject 评论ids
	 * @return 结果
	 */
	@ApiOperation(value = "批量删除评论", notes = "批量删除评论")
	@SuppressWarnings("unchecked")
	@DeleteMapping("delBatch")
	public Boolean deletes(@ApiParam(value = "评论IDs", required = true) @RequestBody Map<String, Object> jsonObject) {
		Object ids = jsonObject.get("ids");
		if (ids != null) {
			List<Object> objects = (List<Object>) ids;
			this.service.deleteBatchComment(objects.toArray());
		}

		return Boolean.TRUE;
	}

	/**
	 * 驳回评论
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@ApiOperation(value = "驳回评论", notes = "驳回指定的评论")
	@PostMapping("reject")
	public Boolean reject(@ApiParam(value = "评论信息", required = true) @Valid @RequestBody KComments entity) {
		this.service.rejectComment(entity);

		return Boolean.TRUE;
	}

	/**
	 * 审核通过评论
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@ApiOperation(value = "审核通过评论", notes = "审核通过指定的评论")
	@PostMapping("audit")
	public Boolean audit(@ApiParam(value = "评论信息", required = true) @Valid @RequestBody KComments entity) {
		this.service.auditComment(entity);

		return Boolean.TRUE;
	}

	/**
	 * 置为垃圾评论
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@ApiOperation(value = "置为垃圾评论", notes = "将评论标记为垃圾评论")
	@PostMapping("spam")
	public Boolean spam(@ApiParam(value = "评论信息", required = true) @Valid @RequestBody KComments entity) {
		this.service.spamComment(entity);

		return Boolean.TRUE;
	}

	/**
	 * 移到回收站
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@ApiOperation(value = "移到回收站", notes = "将评论移到回收站")
	@PostMapping("trash")
	public Boolean trash(@ApiParam(value = "评论信息", required = true) @Valid @RequestBody KComments entity) {
		this.service.trashComment(entity);

		return Boolean.TRUE;
	}
}
