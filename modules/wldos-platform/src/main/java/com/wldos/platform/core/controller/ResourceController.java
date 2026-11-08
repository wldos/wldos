/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.wldos.common.Constants;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.common.utils.TreeUtils;
import io.github.wldos.common.vo.TreeSelectOption;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.service.AdminScopeService;
import com.wldos.platform.core.service.ResourceProbeService;
import com.wldos.platform.core.service.ResourceService;
import com.wldos.platform.core.support.SiteNavDirSupport;
import com.wldos.platform.core.vo.AuthRes;
import com.wldos.platform.core.vo.DomRes;
import com.wldos.platform.core.vo.ResSimple;
import com.wldos.platform.core.vo.Resource;
import com.wldos.platform.core.vo.ResourceApiCoverageItemVO;
import io.github.wldos.platform.support.resource.entity.WoResource;
import io.github.wldos.platform.support.resource.enums.MenuNodeKindEnum;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;

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

import javax.validation.Valid;

/**
 * 资源相关controller。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "资源管理")
@RestController
@RequestMapping("admin/sys/res")
public class ResourceController extends EntityController<ResourceService, WoResource> {

	private final AdminScopeService adminScopeService;

	@Autowired
	private ResourceProbeService resourceProbeService;

	public ResourceController(AdminScopeService adminScopeService) {
		this.adminScopeService = adminScopeService;
	}
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 参数
	 * @return 分页
	 */
	@ApiOperation(value = "资源列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "resourceName", value = "资源名称（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageData<Resource> listQuery(@RequestParam Map<String, Object> params) {
		PageQuery pageQuery = new PageQuery(params);
		PageData<Resource> page = this.service.queryResourceTree(pageQuery);
		if (!this.isAdmin(this.getUserId())) {
			Set<Long> allowed = this.adminScopeService.grantableResourceIds(
					this.getDomainId(), this.getTenantId(), this.getUserId());
			page.setRows(this.filterResourceTree(page.getRows(), allowed));
		}
		return page;
	}

	@GetMapping("get")
	@Override
	public Object getEntityById(@RequestParam long id) {
		WoResource resource = this.service.findById(id);
		if (resource != null) {
			this.assertResourceGrant(resource);
		}
		return resource;
	}

	/**
	 * 所有资源树
	 *
	 * @return 系统资源树
	 */
	@ApiOperation(value = "资源树", notes = "获取所有资源树")
	@GetMapping("tree")
	public List<AuthRes> allResTree() {
		List<WoResource> resources = this.service.listGrantCatalogResources();
		if (!this.isAdmin(this.getUserId())) {
			Set<Long> allowed = this.adminScopeService.grantableResourceIds(
					this.getDomainId(), this.getTenantId(), this.getUserId());
			resources = resources.stream().filter(r -> allowed.contains(r.getId())).collect(Collectors.toList());
		}

		List<AuthRes> authResList = resources.parallelStream().map(res ->
				AuthRes.of(res.getResourceName(), res.getId().toString(), res.getId(), res.getParentId(), res.getDisplayOrder())).collect(Collectors.toList());

		return TreeUtils.build(authResList, Constants.MENU_ROOT_ID);
	}

	/**
	 * 获取菜单树状列表
	 * (常规分层, [{title: '', value: '', key: '', children: [{...}...]},])
	 *
	 * @return 菜单树状列表
	 */
	@ApiOperation(value = "菜单树状列表", notes = "获取菜单树状列表")
	@GetMapping("treeSelect")
	public List<TreeSelectOption> resMenuLayerTree() {
		List<TreeSelectOption> tree = this.service.queryLayerMenuTree();
		if (this.isAdmin(this.getUserId())) {
			return tree;
		}
		Set<Long> allowed = this.adminScopeService.grantableResourceIds(
				this.getDomainId(), this.getTenantId(), this.getUserId());
		return this.filterTreeSelectByGrantable(tree, allowed);
	}

	/**
	 * 可供选择的资源，用于给域预订资源
	 *
	 * @param params 查询参数
	 * @return 分页数据
	 */
	@ApiOperation(value = "可选资源列表", notes = "可供选择的资源，用于给域预订资源")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "域名ID", dataTypeClass = Long.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "resourceName", value = "资源名称（模糊）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "resourcePath", value = "资源路径（模糊）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "domainMounted", value = "站点挂载状态：1=已添加，0=未添加", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("select")
	public PageData<DomRes> listSelect(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		Long domainId = Long.parseLong(params.get("domainId").toString());

		return this.service.queryResByDomainId(domainId, pageQuery);
	}

	// @todo 改造点三，实现封装的菜单创建功能，考虑来源：应用路由（静态、动态）、链接（功能）、数据对象（具体数据内容）。
	// 逻辑：只有全局管理员或域管理员(二级运营方)等运营方可以创建菜单、资源，创建菜单时根据选择的来源自动呈现对应的表单，菜单需要设置关联的域，域管理员创建时自动绑定当前域。

	/** 展示顺序上限，见 {@link io.github.wldos.common.Constants#DISPLAY_ORDER_MAX} */
	private static final long DISPLAY_ORDER_MAX = Constants.DISPLAY_ORDER_MAX;

	@Override
	protected void preAdd(WoResource resource) {
		Long displayOrder = resource.getDisplayOrder();
		if (displayOrder == null || displayOrder <= 0) {
			Long parentId = resource.getParentId();
			Long order = this.service.queryMaxOrder(parentId);
			long nextOrder = ObjectUtils.nvlToZero(order) + 1L;
			resource.setDisplayOrder(Math.min(nextOrder, DISPLAY_ORDER_MAX));
		}
		else {
			resource.setDisplayOrder(Math.min(displayOrder, DISPLAY_ORDER_MAX));
		}

		this.assertResourceConfig(resource);
	}

	@Override
	protected void postAdd(WoResource resource) {
		this.service.appendScopedAdminIdentityRoleAuth(resource, this.getTenantId(), this.getDomainId());
		this.refreshAuth();
	}

	@Override
	protected void preUpdate(WoResource resource) {
		WoResource existing = null;
		if (resource != null && resource.getId() != null) {
			existing = this.service.findById(resource.getId());
			if (existing != null) {
				this.assertNotSiteOwnedNavDirForGenericCrud(existing);
				this.assertResourceGrant(existing);
				this.service.cascadeAppIdOnChange(existing, resource, this.getUserId(), this.getUserIp());
			}
		}
		this.assertResourceConfig(resource);
	}

	@Override
	protected void postUpdate(WoResource resource) {
		this.service.appendScopedAdminIdentityRoleAuth(resource, this.getTenantId(), this.getDomainId());
		this.refreshAuth();
	}

	@Override
	protected void preDelete(WoResource entity) {
		if (entity != null && entity.getId() != null) {
			WoResource existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertNotSiteOwnedNavDirForGenericCrud(existing);
				this.assertResourceGrant(existing);
			}
		}
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		if (ids == null) {
			return;
		}
		for (Object raw : ids) {
			WoResource existing = this.service.findById(Long.parseLong(raw.toString()));
			if (existing != null) {
				this.assertNotSiteOwnedNavDirForGenericCrud(existing);
				this.assertResourceGrant(existing);
			}
		}
	}

	private void assertNotSiteOwnedNavDirForGenericCrud(WoResource resource) {
		if (SiteNavDirSupport.isSiteOwnedNavDir(resource)) {
			throw new BaseException("站点自建菜单分组请在「站点管理 → 菜单布局」中维护");
		}
	}

	private void assertResourceGrant(WoResource resource) {
		if (resource == null || this.isAdmin(this.getUserId())) {
			return;
		}
		if (!this.adminScopeService.canGrantResource(this.getUserId(), resource)) {
			throw new BaseException("无权访问该资源");
		}
	}

	private List<Resource> filterResourceTree(List<Resource> nodes, Set<Long> allowed) {
		if (nodes == null || nodes.isEmpty()) {
			return nodes;
		}
		return nodes.stream()
				.map(node -> {
					if (node.getChildren() != null && !node.getChildren().isEmpty()) {
						node.setChildren(this.filterResourceTree(node.getChildren(), allowed));
					}
					return node;
				})
				.filter(node -> allowed.contains(node.getId())
						|| (node.getChildren() != null && !node.getChildren().isEmpty()))
				.collect(Collectors.toList());
	}

	private List<TreeSelectOption> filterTreeSelectByGrantable(List<TreeSelectOption> nodes, Set<Long> allowed) {
		if (nodes == null || nodes.isEmpty()) {
			return nodes;
		}
		return nodes.stream()
				.map(node -> {
					if (node.getChildren() != null && !node.getChildren().isEmpty()) {
						node.setChildren(this.filterTreeSelectByGrantable(node.getChildren(), allowed));
					}
					return node;
				})
				.filter(node -> node.getId() == null
						|| allowed.contains(node.getId())
						|| (node.getChildren() != null && !node.getChildren().isEmpty()))
				.collect(Collectors.toList());
	}

	/**
	 * 资源配置真值校验：失败抛 {@link BaseException} 拒绝保存。
	 * <p>"建议提示"型反馈（与 Spring 端点等价但模板写法不同）作为 warn 日志记录但不拦截。
	 */
	private void assertResourceConfig(WoResource r) {
		if (r != null && (MenuNodeKindEnum.isRoute(r.getMenuNodeKind()) || MenuNodeKindEnum.isNavDir(r.getMenuNodeKind()))) {
			r.setMenuRegion(null);
		}
		String msg = this.resourceProbeService.validate(
				r.getResourceType(), r.getResourcePath(), r.getRequestMethod(), r.getMenuNodeKind(), r.getAppId());
		if (msg == null) return;
		// 以"提示："开头的为弱提示（弱匹配建议、appCode 首段与所属 app 不一致等），不阻断保存；其余视为错误
		if (msg.startsWith("提示：")) {
			return;
		}
		throw new BaseException(msg);
	}

	/**
	 * 资源配置实时校验接口：前端 onChange / onBlur 调用，避免到保存才报错。
	 *
	 * @param body 含 {@code resourceType}、{@code resourcePath}、{@code requestMethod}
	 * @return 通过返回 {@code Result.ok(true)}；不通过返回 {@code Result.ok(message)}（200 + 提示文案）
	 */
	@ApiOperation(value = "检查遗漏 API 资源", notes = "核对资源池内 menu 页面所引用的 API 是否已声明为 button 资源；仅超级管理员可用")
	@ApiImplicitParam(name = "appCode", value = "应用编码，默认 admin", dataTypeClass = String.class, paramType = "query", example = "admin")
	@GetMapping("api-coverage-check")
	public Result<List<ResourceApiCoverageItemVO>> checkApiCoverage(
			@RequestParam(defaultValue = "admin") String appCode) {
		if (!this.isAdmin(this.getUserId())) {
			throw new BaseException("仅超级管理员可执行全平台 API 资源检查");
		}
		return Result.ok(this.service.checkApiCoverage(appCode));
	}

	@ApiOperation(value = "资源配置校验", notes = "校验 (resourcePath, requestMethod) 是否能命中真实 Controller 端点")
	@PostMapping("probe")
	public Result probe(@RequestBody Map<String, String> body) {
		String type = body == null ? null : body.get("resourceType");
		String path = body == null ? null : body.get("resourcePath");
		String method = body == null ? null : body.get("requestMethod");
		String menuNodeKind = body == null ? null : body.get("menuNodeKind");
		String appIdRaw = body == null ? null : body.get("appId");
		Long appId = null;
		if (!ObjectUtils.isBlank(appIdRaw)) {
			try {
				appId = Long.parseLong(appIdRaw.trim());
			}
			catch (NumberFormatException ignore) {
				appId = null;
			}
		}
		String msg = this.resourceProbeService.validate(type, path, method, menuNodeKind, appId);
		if (msg == null) {
			return Result.ok(Boolean.TRUE);
		}
		// 用 ok + 文案，前端按 message 长度/内容显示警告，不阻塞输入流
		return Result.ok(msg);
	}

	@Override
	protected void postDelete(WoResource resource) {
		this.refreshAuth();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.refreshAuth();
	}

	/**
	 * 查询资源列表时追加平台根资源
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	@Override
	protected List<WoResource> doFilter(List<WoResource> res) {
		if (this.isPlatformAdmin(this.getTenantId())) {
			WoResource plat = new WoResource(Constants.MENU_ROOT_ID, "根资源");
			if (res.isEmpty()) {
				res.add(plat);
			}
			else{
				res.add(plat);
				res.sort(Comparator.comparing(WoResource::getId));
			}
		}

		return res;
	}


	/**
	 * 按模板新建菜单
	 *
	 * @param resSimple 资源模板
	 * @return 是否
	 */
	@ApiOperation(value = "按模板新建菜单", notes = "按模板新建菜单")
	@PostMapping("addSimple")
	@OpLog(action = "按模板新建菜单", resourceType = "resource", resourceId = "#resSimple.id")
	public Result addSimpleMenu(@ApiParam(value = "资源模板", required = true) @Valid @RequestBody ResSimple resSimple) {
		this.service.addSimpleMenu(resSimple, this.getUserId(), this.getUserIp());
		this.refreshAuth();
		return Result.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "刷新鉴权缓存", notes = "清 Redis WLDOS_AUTH:* 与进程内 compiledCache（双层）")
	@PostMapping("auth-cache/refresh")
	@OpLog(action = "手动刷新鉴权缓存", resourceType = "resource", resourceId = "authCache")
	public Result<Boolean> refreshAuthCache() {
		if (!this.isAdmin(this.getUserId())) {
			throw new BaseException("仅平台超级管理员可用", ResultCode.FORBIDDEN.getCode());
		}
		this.refreshAuth();
		return Result.ok(Boolean.TRUE);
	}
}