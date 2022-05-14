/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.util.List;
import java.util.Map;

import com.wldos.base.controller.RepoController;
import com.wldos.cms.entity.KComments;
import com.wldos.cms.enums.ApproveStatusEnum;
import com.wldos.cms.service.CommentService;
import com.wldos.cms.vo.AuditComment;
import com.wldos.cms.vo.AuditPost;
import com.wldos.cms.vo.Comment;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.sys.base.enums.ContModelTypeEnum;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论controller。
 *
 * @author 树悉猿
 * @date 2021/6/12
 * @version 1.0
 */
@RequestMapping("admin/cms/comment")
@RestController
public class CommentAdminController extends RepoController<CommentService, KComments> {

	/**
	 * 评论管理列表，后端管理功能既有租户隔离，又有域隔离，前端公共信息可以免登录访问只需域隔离
	 *
	 * @return 评论列表
	 */
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
	@PostMapping("commit")
	public Long comment(@RequestBody KComments entity) {

		return this.service.comment(entity, this.getCurUserId(), this.getUserIp());
	}

	/**
	 * 删除评论
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@DeleteMapping("del")
	public Boolean delete(@RequestBody KComments entity) {
		this.service.deleteComment(entity);

		return Boolean.TRUE;
	}

	/**
	 * 批量删除评论
	 *
	 * @param jsonObject 评论ids
	 * @return 结果
	 */
	@DeleteMapping("delBatch")
	public Boolean deletes(@RequestBody Map<String, Object> jsonObject) {
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
	@PostMapping("reject")
	public Boolean reject(@RequestBody KComments entity) {
		this.service.rejectComment(entity);

		return Boolean.TRUE;
	}

	/**
	 * 审核通过评论
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@PostMapping("audit")
	public Boolean audit(@RequestBody KComments entity) {
		this.service.auditComment(entity);

		return Boolean.TRUE;
	}

	/**
	 * 置为垃圾评论
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@PostMapping("spam")
	public Boolean spam(@RequestBody KComments entity) {
		this.service.spamComment(entity);

		return Boolean.TRUE;
	}

	/**
	 * 移到回收站
	 *
	 * @param entity 评论id
	 * @return 结果
	 */
	@PostMapping("trash")
	public Boolean trash(@RequestBody KComments entity) {
		this.service.trashComment(entity);

		return Boolean.TRUE;
	}
}
