/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.system.core.service.CompanyService;
import com.wldos.system.vo.Company;
import com.wldos.system.core.entity.WoCompany;

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
@SuppressWarnings({"unchecked"})
@RestController
@RequestMapping("admin/sys/com")
public class CompanyController extends RepoController<CompanyService, WoCompany> {

	@GetMapping("")
	public PageableResult<Company> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		return this.service.execQueryForTree(new Company(), new WoCompany(), pageQuery, Constants.TOP_COM_ID);
	}

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
		Long userComId = Long.parseLong(ObjectUtil.string(params.get("userComId")));
		this.service.removeTenantAdmin(ids, userComId);

		return Boolean.TRUE;
	}

	@Override
	protected List<WoCompany> doFilter(List<WoCompany> res) {

		if (this.isPlatformAdmin(this.getTenantId())) {
			WoCompany plat = new WoCompany(Constants.TOP_COM_ID, "平台");
			if (res.isEmpty()) {
				res.add(plat);
			}else
				res.set(0, plat);
		}

		return res;
	}
}
