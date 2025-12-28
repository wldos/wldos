/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.common.Constants;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.vo.SelectOption;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.WoCompany;
import com.wldos.platform.core.service.CompanyService;

import com.wldos.platform.core.vo.Company;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

/**
 * 公司相关controller。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "公司管理")
@SuppressWarnings({ "unchecked" })
@RestController
@RequestMapping("admin/sys/com")
public class CompanyController extends EntityController<CompanyService, WoCompany> {
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 查询条件
	 * @return 公司列表
	 */
	@ApiOperation(value = "公司列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "comName", value = "公司名称（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageableResult<Company> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		return this.service.execQueryForTree(new Company(), new WoCompany(), pageQuery, Constants.TOP_COM_ID);
	}

	/**
	 * 支持多租户查询的公司下拉选项列表
	 */
	@ApiOperation(value = "公司下拉选项", notes = "支持多租户查询的公司下拉选项列表")
	@GetMapping("select")
	public List<SelectOption> queryComSelectOption() {
		return this.all().stream().map(com -> SelectOption.of(com.getComName(), com.getId().toString())).collect(Collectors.toList());
	}

	/**
	 * 超级管理员后台给租户添加管理员。
	 *
	 * @param orgUser 添加人员参数
	 * @return 添加结果
	 */
	@ApiOperation(value = "添加租户管理员", notes = "超级管理员后台给租户添加管理员")
	@PostMapping("admin")
	public Result addTenantAdmin(@ApiParam(value = "添加人员参数", required = true) @RequestBody Map<String, Object> orgUser) {
		Long userComId = Long.parseLong(orgUser.get("userComId").toString());
		List<String> userIds = (List<String>) orgUser.get("userIds");
		Long curUserId = this.getUserId();
		String uip = this.getUserIp();

		String message = this.service.addTenantAdmin(userIds, userComId, curUserId, uip);

		return Result.ok(message);
	}

	@ApiOperation(value = "移除租户管理员", notes = "移除租户管理员")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "ids", value = "用户ID列表", dataTypeClass = List.class, paramType = "body", required = true),
		@ApiImplicitParam(name = "userComId", value = "用户公司ID", dataTypeClass = Long.class, paramType = "body", required = true)
	})
	@DeleteMapping("rmAdmin")
	public Boolean removeTenantAdmin(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<String>) params.get("ids")).stream().map(Long::parseLong).collect(Collectors.toList());
		Long userComId = Long.parseLong(ObjectUtils.string(params.get("userComId")));
		this.service.removeTenantAdmin(ids, userComId);

		return Boolean.TRUE;
	}

	/**
	 * 查询公司列表时追加平台根
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	@Override
	protected List<WoCompany> doFilter(List<WoCompany> res) {

		if (this.isPlatformAdmin(this.getTenantId())) {
			WoCompany plat = new WoCompany(Constants.TOP_COM_ID, Constants.TOP_COM_NAME);
			if (res.isEmpty()) {
				res.add(plat);
			}
			else {
				res.add(plat);
				res.sort(Comparator.comparing(WoCompany::getId));
			}
		}

		return res;
	}

}