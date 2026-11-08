/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import io.github.wldos.platform.support.rbac.DataScopeQueryHelper;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoDomainResource;
import com.wldos.platform.core.service.DomainService;
import io.github.wldos.platform.support.domain.vo.DomainResource;
import io.github.wldos.platform.support.rbac.DataScopeProfile;

import com.wldos.platform.core.service.ProvisionScenarioService;
import com.wldos.platform.core.vo.DomainMountVisibilityBody;
import com.wldos.platform.core.vo.ProvisionScenarioSaveBody;
import com.wldos.platform.core.vo.ProvisionScenarioView;
import com.wldos.platform.core.vo.ResTreeOverlayBody;
import com.wldos.platform.core.vo.SiteNavDirBody;
import com.wldos.platform.core.vo.SiteNavDirResult;
import io.github.wldos.common.vo.SelectOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "域名管理")
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("admin/sys/domain")
public class DomainController extends EntityController<DomainService, WoDomain> {

	@Autowired
	private ProvisionScenarioService provisionScenarioService;

	@ApiOperation(value = "入驻场景列表", notes = "含模板站挂载就绪状态")
	@GetMapping("provision-scenarios")
	public List<ProvisionScenarioView> listProvisionScenarios() {
		assertPlatformAdminForScenarioManage();
		return this.provisionScenarioService.listScenariosWithStatus();
	}

	@ApiOperation(value = "入驻场景下拉", notes = "入驻向导可选场景")
	@GetMapping("provision-scenarios/select")
	public List<SelectOption> selectProvisionScenarios() {
		return this.provisionScenarioService.listEnabledScenarioOptions();
	}

	@ApiOperation(value = "保存入驻场景", notes = "超管维护场景登记 JSON")
	@PutMapping("provision-scenarios")
	@OpLog(action = "保存入驻场景", resourceType = "domain")
	public Result<Boolean> saveProvisionScenarios(@RequestBody ProvisionScenarioSaveBody body) {
		assertPlatformAdminForScenarioManage();
		this.provisionScenarioService.saveScenarios(body != null ? body.getScenarios() : null);
		return Result.ok(Boolean.TRUE);
	}

	private void assertPlatformAdminForScenarioManage() {
		if (!this.isAdmin(this.getUserId())) {
			throw new BaseException("仅平台超管可管理入驻场景", ResultCode.FORBIDDEN.getCode());
		}
	}

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
	public PageData<WoDomain> listQuery(@RequestParam Map<String, Object> params) {
		PageQuery pageQuery = new PageQuery(params);
		Long userId = this.getUserId();
		// 站点管理员只看自己绑定的域；租户管理员/超管要看到本租户全部域，走 comId 直滤，不叠加当前会话所在域
		if (this.commonOperate.isDomainAdmin(userId) && !this.commonOperate.isTenantAdmin(userId)) {
			this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, WoDomain.class);
		} else {
			this.applyScope(pageQuery, DataScopeProfile.ADMIN_TENANT, WoDomain.class);
		}
		DataScopeQueryHelper.remapDomainIdToPrimaryId(pageQuery);
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
		@ApiImplicitParam(name = "domainId", value = "域名ID", dataTypeClass = Long.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "filter", value = "列筛选 JSON，是否隐藏传 mountValid：0=是、1=否", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("resList")
	public PageData<DomainResource> listDomainRes(@RequestParam Map<String, Object> params) {
		PageQuery pageQuery = new PageQuery(params);
		Long domainId = Long.parseLong(params.get("domainId").toString());
		this.assertBoundDomainAccess(domainId);
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
	@OpLog(action = "域订阅应用", resourceType = "domain", resourceId = "#domainApp['domainId']")
	public Result domainApp(@ApiParam(value = "预订应用参数", required = true) @RequestBody Map<String, Object> domainApp) {
		Long domainId = Long.parseLong(domainApp.get("domainId").toString());
		this.assertBoundDomainAccess(domainId);
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
	@OpLog(action = "域取消订阅应用", resourceType = "domain", resourceId = "#params['domainId']")
	public Boolean removeDomainApp(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<Object>) params.get("ids")).stream().map(id -> Long.parseLong(ObjectUtils.string(id))).collect(Collectors.toList());
		Long domainId = Long.parseLong(ObjectUtils.string(params.get("domainId")));
		this.assertBoundDomainAccess(domainId);

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
	@OpLog(action = "域订阅资源", resourceType = "domain", resourceId = "#domainRes['domainId']")
	public Result domainRes(@ApiParam(value = "预订资源参数", required = true) @RequestBody Map<String, Object> domainRes) {
		Long domainId = Long.parseLong(domainRes.get("domainId").toString());
		this.assertBoundDomainAccess(domainId);
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
	@OpLog(action = "域取消订阅资源", resourceType = "domain", resourceId = "#params['domainId']")
	public Boolean removeDomainRes(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<Object>) params.get("ids")).stream().map(id -> Long.parseLong(ObjectUtils.string(id))).collect(Collectors.toList());
		Long domainId = Long.parseLong(ObjectUtils.string(params.get("domainId")));
		this.assertBoundDomainAccess(domainId);

		this.service.removeDomainRes(ids, domainId);
		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain); // 配置完毕，刷新域名缓存

		return Boolean.TRUE;
	}

	@ApiOperation(value = "站点资源挂载显隐", notes = "保留绑定，仅切换 wo_domain_resource.is_valid；不改动角色授权")
	@PostMapping("resVisibility")
	@OpLog(action = "站点资源显隐", resourceType = "domain", resourceId = "#body.domainId")
	public Boolean setDomainResVisibility(@ApiParam(value = "显隐参数", required = true) @RequestBody DomainMountVisibilityBody body) {
		this.assertBoundDomainAccess(body.getDomainId());
		this.service.setDomainResourceVisibility(body, this.getUserId(), this.getUserIp());
		WoDomain domain = this.service.findById(body.getDomainId());
		this.service.refreshDomain(domain);
		return Boolean.TRUE;
	}

	@ApiOperation(value = "站点应用挂载显隐", notes = "隐藏应用时级联同站同应用下全部域资源挂载；显示应用不自动恢复曾被单独隐藏的资源")
	@PostMapping("appVisibility")
	@OpLog(action = "站点应用显隐", resourceType = "domain", resourceId = "#body.domainId")
	public Boolean setDomainAppVisibility(@ApiParam(value = "显隐参数", required = true) @RequestBody DomainMountVisibilityBody body) {
		this.assertBoundDomainAccess(body.getDomainId());
		this.service.setDomainAppVisibility(body, this.getUserId(), this.getUserIp());
		WoDomain domain = this.service.findById(body.getDomainId());
		this.service.refreshDomain(domain);
		return Boolean.TRUE;
	}

	@ApiOperation(value = "域资源配置", notes = "配置域资源")
	@PostMapping("resConf")
	@OpLog(action = "域资源配置", resourceType = "domain", resourceId = "#dRes.domainId")
	public Boolean domainResConf(@ApiParam(value = "域资源配置", required = true) @RequestBody WoDomainResource dRes) {
		this.assertBoundDomainAccess(dRes.getDomainId());
		this.service.domainResConf(dRes);
		Long domainId = dRes.getDomainId();
		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain); // 配置完毕，刷新域名缓存

		return Boolean.TRUE;
	}

	@ApiOperation(value = "站点菜单布局树", notes = "查询本站已挂载 menu 资源的 overlay 编辑树")
	@ApiImplicitParam(name = "domainId", value = "站点 ID", dataTypeClass = Long.class, paramType = "query", required = true)
	@GetMapping("siteMenuTree")
	public List<DomainResource> siteMenuTree(@RequestParam Long domainId) {
		this.assertBoundDomainAccess(domainId);
		return this.service.querySiteMenuTree(domainId);
	}

	@ApiOperation(value = "批量保存菜单布局", notes = "保存本站 menu overlay（parent/order/label）")
	@PostMapping("resTreeOverlay")
	@OpLog(action = "站点菜单布局", resourceType = "domain", resourceId = "#body.domainId")
	public Boolean resTreeOverlay(@ApiParam(value = "overlay 批量保存", required = true) @RequestBody ResTreeOverlayBody body) {
		this.assertBoundDomainAccess(body.getDomainId());
		this.service.saveResTreeOverlay(body);
		WoDomain domain = this.service.findById(body.getDomainId());
		this.service.refreshDomain(domain);
		return Boolean.TRUE;
	}

	@ApiOperation(value = "新建站点菜单分组", notes = "创建 scope_level=domain 的 nav_dir 并挂载本站")
	@PostMapping("siteNavDir")
	@OpLog(action = "新建站点菜单分组", resourceType = "domain", resourceId = "#body.domainId")
	public SiteNavDirResult siteNavDir(@ApiParam(value = "新建分组", required = true) @RequestBody SiteNavDirBody body) {
		this.assertBoundDomainAccess(body.getDomainId());
		SiteNavDirResult result = this.service.createSiteNavDir(body, this.getUserId(), this.getUserIp());
		WoDomain domain = this.service.findById(body.getDomainId());
		this.service.refreshDomain(domain);
		return result;
	}

	@ApiOperation(value = "更新站点菜单分组", notes = "更新 scope_level=domain 的 nav_dir 名称与 overlay")
	@PutMapping("siteNavDir")
	@OpLog(action = "更新站点菜单分组", resourceType = "domain", resourceId = "#body.domainId")
	public SiteNavDirResult updateSiteNavDir(@ApiParam(value = "更新分组", required = true) @RequestBody SiteNavDirBody body) {
		this.assertBoundDomainAccess(body.getDomainId());
		SiteNavDirResult result = this.service.updateSiteNavDir(body, this.getUserId(), this.getUserIp());
		WoDomain domain = this.service.findById(body.getDomainId());
		this.service.refreshDomain(domain);
		return result;
	}

	@ApiOperation(value = "删除站点菜单分组", notes = "删除 scope_level=domain 的 nav_dir；有子菜单或存在角色授权时拒绝")
	@DeleteMapping("siteNavDir")
	@OpLog(action = "删除站点菜单分组", resourceType = "domain", resourceId = "#body.domainId")
	public Boolean deleteSiteNavDir(@ApiParam(value = "删除分组", required = true) @RequestBody SiteNavDirBody body) {
		this.assertBoundDomainAccess(body.getDomainId());
		this.service.deleteSiteNavDir(body.getDomainId(), body.getResourceId(), this.getUserId(), this.getUserIp());
		WoDomain domain = this.service.findById(body.getDomainId());
		this.service.refreshDomain(domain);
		return Boolean.TRUE;
	}

	@ApiOperation(value = "域管理员列表", notes = "查询站点绑定的域级管理员，支持分页与按昵称/账号/邮箱/状态查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "domainId", value = "站点ID", dataTypeClass = Long.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("admin")
	public Result listDomainAdmins(@RequestParam Long domainId, @RequestParam Map<String, Object> params) {
		this.assertBoundDomainAccess(domainId);
		params.remove("domainId");
		PageQuery pageQuery = new PageQuery(params);
		return Result.ok(this.service.listDomainAdmins(domainId, pageQuery));
	}

	@ApiOperation(value = "绑定域管理员", notes = "为站点绑定域级管理员用户")
	@PostMapping("admin")
	@OpLog(action = "绑定域管理员", resourceType = "domain", resourceId = "#body['domainId']")
	public Result bindDomainAdmin(@RequestBody Map<String, Object> body) {
		this.assertTenantOrPlatformForDomainAdminBind();
		Long domainId = Long.parseLong(body.get("domainId").toString());
		this.assertBoundDomainAccess(domainId);
		Long userId = Long.parseLong(body.get("userId").toString());
		this.service.bindDomainAdmin(domainId, userId, this.resolveDomainComId(domainId), this.getUserId(), this.getUserIp());
		return Result.ok(true);
	}

	@ApiOperation(value = "批量绑定域管理员", notes = "为站点批量绑定域级管理员用户，候选人清单勾选后一次提交")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "domainId", value = "站点ID", dataTypeClass = Long.class, paramType = "body", required = true),
		@ApiImplicitParam(name = "userIds", value = "用户ID列表", dataTypeClass = List.class, paramType = "body", required = true)
	})
	@PostMapping("admins")
	@OpLog(action = "批量绑定域管理员", resourceType = "domain", resourceId = "#body['domainId']")
	public Result bindDomainAdmins(@RequestBody Map<String, Object> body) {
		this.assertTenantOrPlatformForDomainAdminBind();
		Long domainId = Long.parseLong(body.get("domainId").toString());
		this.assertBoundDomainAccess(domainId);
		List<?> rawUserIds = (List<?>) body.get("userIds");
		List<Long> userIds = rawUserIds == null ? Collections.emptyList()
				: rawUserIds.stream().map(id -> Long.parseLong(id.toString())).collect(Collectors.toList());
		String message = this.service.bindDomainAdmins(domainId, userIds, this.resolveDomainComId(domainId), this.getUserId(), this.getUserIp());
		return Result.ok(message);
	}

	/**
	 * 域管理员绑定的租户归属以站点自身归属租户（{@code wo_domain.comId}）为准，站点找不到时兜底为操作者所在租户。
	 */
	private Long resolveDomainComId(Long domainId) {
		WoDomain domain = this.service.findById(domainId);
		return domain != null ? domain.getComId() : this.getTenantId();
	}

	@ApiOperation(value = "解绑域管理员", notes = "移除站点域级管理员绑定")
	@DeleteMapping("admin")
	@OpLog(action = "解绑域管理员", resourceType = "domain", resourceId = "#body['id']")
	public Result unbindDomainAdmin(@RequestBody Map<String, Object> body) {
		Long id = Long.parseLong(body.get("id").toString());
		this.service.unbindDomainAdmin(id, this.getUserId(), this.getUserIp());
		return Result.ok(true);
	}

	@ApiOperation(value = "批量解绑域管理员", notes = "批量移除站点域级管理员绑定，按绑定记录id")
	@ApiImplicitParam(name = "ids", value = "绑定记录ID列表", dataTypeClass = List.class, paramType = "body", required = true)
	@DeleteMapping("admins")
	@OpLog(action = "批量解绑域管理员", resourceType = "domain")
	public Result unbindDomainAdmins(@RequestBody Map<String, Object> body) {
		List<?> rawIds = (List<?>) body.get("ids");
		List<Long> ids = rawIds == null ? Collections.emptyList()
				: rawIds.stream().map(id -> Long.parseLong(id.toString())).collect(Collectors.toList());
		this.service.unbindDomainAdmins(ids, this.getUserId(), this.getUserIp());
		return Result.ok(true);
	}


	/**
	 * 获取平台根域名(返回值字符串时必须是json格式)
	 */
	@ApiOperation(value = "平台根域名", notes = "获取平台根域名")
	@GetMapping("root")
	public String platformDomain() {
		return this.resJson.ok(Constants.COMMON_KEY_PLATFORM_DOMAIN, this.getPlatDomain());
	}

	@Override
	protected void preAdd(WoDomain entity) {
		if (this.isAdmin(this.getUserId()) && entity.getComId() != null) {
			// 超管显式指定归属租户（含平台轴 comId=0）
		}
		else {
			entity.setComId(this.getTenantId());
		}
		this.service.validateDomainUniqueness(entity);
	}

	@GetMapping("get")
	@Override
	public Object getEntityById(@RequestParam long id) {
		WoDomain domain = this.service.findById(id);
		if (domain != null) {
			this.assertEntityRowScope(domain);
			this.assertBoundDomainAccess(domain.getId());
		}
		return domain;
	}

	@Override
	protected void preUpdate(WoDomain entity) {
		if (entity != null && entity.getId() != null) {
			WoDomain existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.assertBoundDomainAccess(existing.getId());
			}
		}
		this.service.validateDomainUniqueness(entity);
	}

	@Override
	protected void preDelete(WoDomain entity) {
		if (entity != null && entity.getId() != null) {
			WoDomain existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.assertBoundDomainAccess(existing.getId());
				this.service.assertDeletable(existing);
			}
		}
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		if (ids == null) {
			return;
		}
		for (Object raw : ids) {
			Long id = Long.parseLong(raw.toString());
			WoDomain existing = this.service.findById(id);
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.assertBoundDomainAccess(existing.getId());
				this.service.assertDeletable(existing);
			}
		}
	}

	@Override
	protected void postAdd(WoDomain domain) {
		this.service.provisionSystemKinds(domain);
		this.service.refreshDomain(domain);
		this.refreshAuth();
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

	private void assertTenantOrPlatformForDomainAdminBind() {
		Long userId = this.getUserId();
		if (userId == null || this.isAdmin(userId) || this.commonOperate.isTenantAdmin(userId)) {
			return;
		}
		if (this.commonOperate.isDomainAdmin(userId)) {
			throw new BaseException("站点管理员无权绑定站点管理员", ResultCode.FORBIDDEN.getCode());
		}
	}
}
