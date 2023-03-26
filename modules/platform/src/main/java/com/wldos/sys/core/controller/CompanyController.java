/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.base.RepoController;
import com.wldos.common.Constants;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.vo.SelectOption;
import com.wldos.sys.base.entity.WoCompany;
import com.wldos.sys.base.vo.Company;
import com.wldos.sys.core.service.CompanyService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公司相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@SuppressWarnings({ "unchecked" })
@RestController
@RequestMapping("admin/sys/com")
public class CompanyController extends RepoController<CompanyService, WoCompany> {
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 查询条件
	 * @return 公司列表
	 */
	@GetMapping("")
	public PageableResult<Company> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		return this.service.execQueryForTree(new Company(), new WoCompany(), pageQuery, Constants.TOP_COM_ID);
	}

	/**
	 * 支持多租户查询的公司下拉选项列表
	 */
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
	@PostMapping("admin")
	public String addTenantAdmin(@RequestBody Map<String, Object> orgUser) {
		Long userComId = Long.parseLong(orgUser.get("userComId").toString());
		List<String> userIds = (List<String>) orgUser.get("userIds");
		Long curUserId = this.getCurUserId();
		String uip = this.getUserIp();

		String message = this.service.addTenantAdmin(userIds, userComId, curUserId, uip);

		return this.resJson.ok(message);
	}

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
			WoCompany plat = new WoCompany(Constants.TOP_COM_ID, "平台");
			if (res.isEmpty()) {
				res.add(plat);
			}
			else
				res.set(0, plat);
		}

		return res;
	}

}