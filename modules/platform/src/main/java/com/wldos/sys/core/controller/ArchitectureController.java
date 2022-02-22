/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.base.controller.RepoController;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.PageQuery;
import com.wldos.common.Constants;
import com.wldos.sys.base.entity.WoArchitecture;
import com.wldos.sys.base.enums.ArchTypeEnum;
import com.wldos.sys.base.service.ArchitectureService;
import com.wldos.sys.base.vo.Architecture;

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
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 查询参数
	 * @return 体系结构列表
	 */
	@GetMapping("")
	public PageableResult<Architecture> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyTenantFilter(pageQuery);

		return this.service.execQueryForTree(new Architecture(), new WoArchitecture(), pageQuery, Constants.TOP_ARCH_ID);
	}

	/**
	 * 新增体系时，带上公司，这里的公司是指租户管理员的主公司
	 *
	 * @param entity 体系实体
	 */
	@Override
	protected void preAdd(WoArchitecture entity) {
		entity.setComId(this.getTenantId());
	}

	/**
	 * 查询体系时追加平台根体系
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
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

	/**
	 * 组织体系结构类型枚举，也作组织结构类型，不同于组织机构类型(ComTypeEnum)。。
	 *
	 * @return 枚举列表
	 */
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
