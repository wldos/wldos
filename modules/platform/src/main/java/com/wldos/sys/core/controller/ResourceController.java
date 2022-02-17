/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
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
import com.wldos.common.utils.TreeUtils;
import com.wldos.sys.base.entity.WoResource;
import com.wldos.sys.base.service.ResourceService;
import com.wldos.sys.base.vo.AuthRes;
import com.wldos.sys.base.vo.DomRes;
import com.wldos.sys.base.vo.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资源相关controller。租户没有权限维护平台上构成平台的原始结构和资源（如：应用、资源、角色、用户），对平台租给租户的可见资源（如：可租用应用、已租用应用下的角色），租户只能在其组织内配置
 * 或取消，租户还可以任意维护自建资源（如：租户内体系、租户内组织、自建业务流程、租户的小程序）。
 * 租户租用应用，然后把应用下的角色授予到内部的组织，相应组织下的人员便具备了访问该应用的一定权限，表现为可以看到应用下的一些可见资源，如果有写权限用户可以对可见资源展开操作行为，具体是什么
 * 权限取决于该人员从其组织继承的应用角色在应用发布时所做的权限设置。上面描述的是租用一个应用时自顶向下的权限生效原理，那么面对租户租用的多个应用，平台顶层是如何定义角色，又是如何在用户层解耦的呢？
 * 这个问题有两个层面：1.平台业务角色定义。2.用户角色解耦。第一个问题，平台定义的业务角色是一种资源收敛容器，这些个收敛容器是基于场景的，一个场景会包含若干资源收敛的角色，那么你可能会想是否存在跨场景的角色呢？
 * 答案是存在，比如在平台层面，所有用户区分为注册会员、VIP会员、管理员等角色，这些平台级的角色就是跨场景的，是否能跨场景，取决于场景是否存在包容关系，在平台层面平台就是个网站或者app，平台直面用户的所有资源的
 * 使用构成平台应用场景，这个场景包含了平台上所有要直面用户的各应用实现的场景，比如：平台上的登陆、写作、阅读、聊天等应用场景都是互联网平台的子应用。到这里，以上所述并没有考虑业务应用，就是那种需要租户租用的收费
 * 应用，比如平台上发布了一个财务记账应用，A租户订购了它，同时A租户又订购了网上编辑室的应用，针对这样的两个应用，平台依然是采用资源收敛的方式定义的业务角色吗？答案是取决于运营，技术上不存在问题，就是说可以跨应用
 * 定义一套角色，也可以分应用定义各自的角色，完全从实施工作量、业务场景是否恰当、运营逻辑和易用性等方面考虑。
 * 然后说第二个问题，平台如何保障某租户的一个员工在披荆斩棘、网上冲浪时不逾矩的，这本质是一个技术问题，平台依据租户、应用、角色、用户的组织展开了多因子约束运算，在应用的角色、角色的权限以及用户的角色之间取了交集
 * 保证用户不会脱离其权限范围，并在约束范围内平滑访问各应用。
 *
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

		List<AuthRes> authResList = resources.parallelStream().map(res -> {
			AuthRes authRes = new AuthRes();
			authRes.setId(res.getId());
			authRes.setParentId(res.getParentId());
			authRes.setTitle(res.getResourceName());
			authRes.setKey(res.getId().toString());

			return authRes;
		}).collect(Collectors.toList());

		return TreeUtils.build(authResList, Constants.MENU_ROOT_ID);
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
}
