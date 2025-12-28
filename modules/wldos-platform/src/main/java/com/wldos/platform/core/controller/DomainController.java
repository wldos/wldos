/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.common.Constants;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoDomainResource;
import com.wldos.platform.core.service.DomainService;
import com.wldos.platform.support.domain.vo.DomainResource;

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
 * 域相关controller。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "域名管理")
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("admin/sys/domain")
public class DomainController extends EntityController<DomainService, WoDomain> {
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 查询参数
	 * @return 分页数据
	 */
	@ApiOperation(value = "域名列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "domainName", value = "域名（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageableResult<WoDomain> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyTenantFilter(pageQuery);

		return this.service.queryDomainList(pageQuery);
	}

	/**
	 * 查询域关联的资源列表
	 *
	 * @param params 查询参数
	 * @return 应用分页数据
	 */
	@ApiOperation(value = "域资源列表", notes = "查询域关联的资源列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "域名ID", dataTypeClass = Long.class, paramType = "query", required = true)
	})
	@GetMapping("resList")
	public PageableResult<DomainResource> listDomainRes(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		Long domainId = Long.parseLong(params.get("domainId").toString());
		return this.service.queryDomainRes(domainId, pageQuery);
	}

	/**
	 * 批量添加应用
	 *
	 * @param domainApp 预订应用参数
	 * @return 反馈结果
	 */
	@ApiOperation(value = "批量添加应用", notes = "批量添加应用")
	@PostMapping("app")
	public Result domainApp(@ApiParam(value = "预订应用参数", required = true) @RequestBody Map<String, Object> domainApp) {
		Long domainId = Long.parseLong(domainApp.get("domainId").toString());
		Long comId = Long.parseLong(domainApp.get("comId").toString());
		List<String> appIds = (List<String>) domainApp.get("ids");
		Long curUserId = this.getUserId();
		String uip = this.getUserIp();

		String message = this.service.domainApp(appIds, domainId, comId, curUserId, uip);

		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain); // 配置完毕，刷新域名缓存

		return Result.ok(message);
	}


	/**
	 * 批量删除某域下的应用
	 *
	 * @param params 域id和要删除的应用id
	 * @return 删除结果
	 */
	@ApiOperation(value = "批量删除应用", notes = "批量删除某域下的应用")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "ids", value = "应用ID列表", dataTypeClass = List.class, paramType = "body", required = true),
		@ApiImplicitParam(name = "domainId", value = "域名ID", dataTypeClass = Long.class, paramType = "body", required = true)
	})
	@DeleteMapping("appDel")
	public Boolean removeDomainApp(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<Object>) params.get("ids")).stream().map(id -> Long.parseLong(ObjectUtils.string(id))).collect(Collectors.toList());
		Long domainId = Long.parseLong(ObjectUtils.string(params.get("domainId")));

		this.service.removeDomainApp(ids, domainId);
		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain); // 配置完毕，刷新域名缓存

		return Boolean.TRUE;
	}

	/**
	 * 批量添加资源
	 *
	 * @param domainRes 预订资源
	 * @return 反馈结果
	 */
	@ApiOperation(value = "批量添加资源", notes = "批量添加资源")
	@PostMapping("res")
	public Result domainRes(@ApiParam(value = "预订资源参数", required = true) @RequestBody Map<String, Object> domainRes) {
		Long domainId = Long.parseLong(domainRes.get("domainId").toString());
		List<String> resIds = (List<String>) domainRes.get("ids");

		String message = this.service.domainRes(resIds, domainId, this.getUserId(), this.getUserIp());

		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain); // 配置完毕，刷新域名缓存

		return Result.ok(message);
	}


	/**
	 * 批量删除某域下的资源
	 *
	 * @param params 域id和要删除的域资源id（不同于resourceId，是domainResId）
	 * @return 删除结果
	 */
	@ApiOperation(value = "批量删除资源", notes = "批量删除某域下的资源")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "ids", value = "域资源ID列表（domainResId，不同于resourceId）", dataTypeClass = List.class, paramType = "body", required = true),
		@ApiImplicitParam(name = "domainId", value = "域名ID", dataTypeClass = Long.class, paramType = "body", required = true)
	})
	@DeleteMapping("resDel")
	public Boolean removeDomainRes(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<Object>) params.get("ids")).stream().map(id -> Long.parseLong(ObjectUtils.string(id))).collect(Collectors.toList());
		Long domainId = Long.parseLong(ObjectUtils.string(params.get("domainId")));

		this.service.removeDomainRes(ids, domainId);
		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain); // 配置完毕，刷新域名缓存

		return Boolean.TRUE;
	}

	@ApiOperation(value = "域资源配置", notes = "配置域资源")
	@PostMapping("resConf")
	public Boolean domainResConf(@ApiParam(value = "域资源配置", required = true) @RequestBody WoDomainResource dRes) {
		this.service.domainResConf(dRes);
		Long domainId = dRes.getDomainId();
		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain); // 配置完毕，刷新域名缓存

		return Boolean.TRUE;
	}

	/**
	 * 获取平台根域名(返回值字符串时必须是json格式)
	 */
	@ApiOperation(value = "平台根域名", notes = "获取平台根域名")
	@GetMapping("root")
	public String platformDomain() {
		return this.resJson.ok(Constants.COMMON_KEY_PLATFORM_DOMAIN, this.getPlatDomain());
	}

	/**
	 * 新增域名时，带上公司
	 *
	 * @param entity 域名实体
	 */
	@Override
	protected void preAdd(WoDomain entity) {
		entity.setComId(this.getTenantId());
	}

	@Override
	protected void postAdd(WoDomain domain) {
		this.service.refreshDomain(domain);
	}

	@Override
	protected void postUpdate(WoDomain domain) {
		this.service.refreshDomain(domain);
	}

	@Override
	protected void postDelete(WoDomain domain) {
		this.service.refreshDomain(domain);
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		ids.parallelStream().forEach(id ->
				this.service.refreshDomain(this.service.findById((Long) id))
		);
	}
}
