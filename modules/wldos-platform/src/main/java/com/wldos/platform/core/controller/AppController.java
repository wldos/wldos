/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.common.Constants;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.WoApp;
import com.wldos.platform.core.enums.AppOriginEnum;
import com.wldos.platform.core.enums.AppTypeEnum;

import com.wldos.platform.core.service.AppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用相关controller。
 * @todo 租户的小程序是应用管理的衍生，相当于开放平台，暂不支持。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@RestController
@RequestMapping("admin/sys/app")
public class AppController extends EntityController<AppService, WoApp> {
	/**
	 * 查询可管理的应用列表
	 *
	 * @param params 查询参数
	 * @return 应用分页数据
	 */
	@GetMapping("")
	public PageableResult<WoApp> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyTenantFilter(pageQuery);

		return this.service.execQueryForPage(new WoApp(), pageQuery);
	}

	/**
	 * 查询域关联的应用列表
	 *
	 * @param params 查询参数
	 * @return 应用分页数据
	 */
	@GetMapping("domain")
	public PageableResult<WoApp> listDomainApp(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		return this.service.queryAppForPage(pageQuery);
	}

	/**
	 * 可共选择的列表，用于给域预订应用
	 *
	 * @param params 查询参数
	 * @return 分页数据
	 */
	@GetMapping("select")
	public PageableResult<WoApp> listSelect(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getUserId())) { // @todo 暂不支持发布私有应用(小程序)，还没有想好如何设计API开放平台
			pageQuery.pushFilter(Constants.COMMON_KEY_APP_TYPE, AppTypeEnum.APP.toString(), AppTypeEnum.PRIVATE.toString());
		}

		return this.service.execQueryForPage(new WoApp(), pageQuery);
	}

	/**
	 * 应用类型枚举
	 *
	 * @return 枚举列表
	 */
	@GetMapping("type")
	public List<Map<String, Object>> fetchEnumAppType() {

		return (this.isPlatformAdmin(this.getTenantId()) ?
				Arrays.stream(AppTypeEnum.values())
				: Arrays.stream(AppTypeEnum.values()).filter(v -> !v.getValue().equals(Constants.ENUM_TYPE_APP_PLAT))).map(item -> {
			Map<String, Object> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
	}

	/**
     * 获取应用来源枚举列表（下拉列表）
     */
    @GetMapping("/origins")
    public List<Map<String, String>> getAppOrigins() {
		return Arrays.stream(AppOriginEnum.values()).map(item -> {
			Map<String, String> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
    }

	@Override
	protected void preAdd(WoApp entity) { // 创建应用时绑定租户id
		entity.setComId(this.getTenantId());
	}

	@Override
	protected void postAdd(WoApp entity) {
		this.refreshAuth();
	}

	@Override
	protected void postUpdate(WoApp entity) {
		this.refreshAuth();
	}

	@Override
	protected void postDelete(WoApp entity) {
		this.refreshAuth();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.refreshAuth();
	}
}