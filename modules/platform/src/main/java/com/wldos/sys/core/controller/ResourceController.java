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

import com.wldos.base.controller.RepoController;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.PageQuery;
import com.wldos.common.Constants;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.TreeUtils;
import com.wldos.common.vo.TreeSelectOption;
import com.wldos.sys.base.entity.WoResource;
import com.wldos.sys.base.service.ResourceService;
import com.wldos.sys.base.vo.AuthRes;
import com.wldos.sys.base.vo.DomRes;
import com.wldos.sys.base.vo.Resource;
import com.wldos.sys.base.vo.ResSimple;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资源相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@RestController
@RequestMapping("admin/sys/res")
public class ResourceController extends RepoController<ResourceService, WoResource> {
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 参数
	 * @return 分页
	 */
	@GetMapping("")
	public PageableResult<Resource> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		return this.service.execQueryForTree(new Resource(), new WoResource(), pageQuery, Constants.MENU_ROOT_ID);
	}

	/**
	 * 所有资源树
	 *
	 * @return 系统资源树
	 */
	@GetMapping("tree")
	public List<AuthRes> allResTree() {
		List<WoResource> resources = this.service.findAll();

		List<AuthRes> authResList = resources.parallelStream().map(res ->
				AuthRes.of(res.getResourceName(), res.getId().toString(), res.getId(), res.getParentId())).collect(Collectors.toList());

		return TreeUtils.build(authResList, Constants.MENU_ROOT_ID);
	}

	/**
	 * 获取菜单树状列表
	 * (常规分层, [{title: '', value: '', key: '', children: [{...}...]},])
	 *
	 * @return 菜单树状列表
	 */
	@GetMapping("treeSelect")
	public List<TreeSelectOption> resMenuLayerTree() {
		return this.service.queryLayerMenuTree();
	}

	/**
	 * 可供选择的资源，用于给域预订资源
	 *
	 * @param params 查询参数
	 * @return 分页数据
	 */
	@GetMapping("select")
	public PageableResult<DomRes> listSelect(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		Long domainId = Long.parseLong(params.get("domainId").toString());

		return this.service.queryResByDomainId(domainId, pageQuery);
	}

	// @todo 改造点三，实现封装的菜单创建功能，考虑来源：应用路由（静态、动态）、链接（功能）、数据对象（具体数据内容）。
	// 逻辑：只有全局管理员或域管理员(二级运营方)等运营方可以创建菜单、资源，创建菜单时根据选择的来源自动呈现对应的表单，菜单需要设置关联的域，域管理员创建时自动绑定当前域。

	@Override
	protected void preAdd(WoResource resource) { // 自定义链接新增资源时，自动增加排序
		Long parentId = resource.getParentId();

		Long order = this.service.queryMaxOrder(parentId);

		resource.setDisplayOrder(ObjectUtils.nvlToZero(order) + 1L);
	}

	@Override
	protected void postAdd(WoResource resource) {
		this.refreshAuth();
	}

	@Override
	protected void postUpdate(WoResource resource) {
		this.refreshAuth();
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
			}else
				res.set(0, plat);
		}

		return res;
	}

	/**
	 * 按模板新建菜单
	 *
	 * @param resSimple 资源模板
	 * @return 是否
	 */
	@PostMapping("addSimple")
	public String addSimpleMenu(@RequestBody ResSimple resSimple) {
		this.service.addSimpleMenu(resSimple, this.getCurUserId(), this.getUserIp());
		this.refreshAuth();
		return resJson.ok(Boolean.TRUE);
	}
}