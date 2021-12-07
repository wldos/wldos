/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.system.core.entity.WoArchitecture;
import com.wldos.system.core.service.ArchitectureService;
import com.wldos.system.enums.ArchTypeEnum;
import com.wldos.system.vo.Architecture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 体系结构相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@RestController
@RequestMapping("admin/sys/arch")
public class ArchitectureController extends RepoController<ArchitectureService, WoArchitecture> {

	@GetMapping("")
	public PageableResult<Architecture> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
		}

		return this.service.execQueryForTree(new Architecture(), new WoArchitecture(), pageQuery, Constants.TOP_ARCH_ID);
	}

	@Override
	protected void preAdd(WoArchitecture entity) {
		entity.setComId(this.getTenantId());
	}

	@Override
	protected List<WoArchitecture> doFilter(List<WoArchitecture> res) {

		if (this.isPlatformAdmin(this.getTenantId())) {
			WoArchitecture plat = new WoArchitecture(Constants.TOP_ARCH_ID, "平台");
			if (res.isEmpty()) {
				res.add(plat);
			}else
				res.set(0, plat);
		}

		return res;
	}

	@GetMapping("type")
	public List<Map<String, Object>> fetchEnumAppType() {

		return Arrays.stream(ArchTypeEnum.values()).map(item -> {
			Map<String, Object> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
	}
}
