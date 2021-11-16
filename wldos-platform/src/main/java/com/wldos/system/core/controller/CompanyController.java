/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.controller;

import java.util.Map;

import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.system.core.service.CompanyService;
import com.wldos.system.vo.Company;
import com.wldos.system.core.entity.WoCompany;

import org.springframework.web.bind.annotation.GetMapping;
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
@RestController
@RequestMapping("admin/sys/com")
public class CompanyController extends RepoController<CompanyService, WoCompany> {

	@GetMapping("")
	public PageableResult<Company> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		return this.service.execQueryForTree(new Company(), new WoCompany(), pageQuery, Constants.TOP_COM_ID);
	}
}
