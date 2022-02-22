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
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;
import com.wldos.common.Constants;
import com.wldos.sys.core.entity.WoOrg;
import com.wldos.sys.base.enums.OrgTypeEnum;
import com.wldos.sys.core.service.OrgService;
import com.wldos.sys.core.vo.Org;
import com.wldos.sys.core.vo.OrgRoleTree;

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
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("admin/sys/org")
public class OrgController extends RepoController<OrgService, WoOrg> {
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 分页参数
	 * @return 组织列表
	 */
	@GetMapping("")
	public PageableResult<Org> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyTenantFilter(pageQuery);

		return this.service.execQueryForTree(new Org(), new WoOrg(), pageQuery, Constants.TOP_ORG_ID);
	}

	/**
	 * 新增组织时，带上公司
	 *
	 * @param entity 组织实体
	 */
	@Override
	protected void preAdd(WoOrg entity) {
		entity.setComId(this.getTenantId());
	}

	/**
	 * 查询某个组织授予的角色（不继承）
	 *
	 * @param authOrg 授权组织参数
	 * @return 组织关联的角色列表和总角色列表
	 */
	@GetMapping("role")
	public OrgRoleTree queryAuthRole(@RequestParam Map<String, String> authOrg) {

		// 查询当前组织关联的角色列表
		return this.service.queryAllRoleTreeByOrgId(Long.parseLong(authOrg.get("orgId")), this.getCurUserId());
	}

	/**
	 * 给组织关联系统角色，对于实组织，关联角色仅针对直属人员生效，对于虚组织(角色组)，虚组织不在组织人员树中展示。
	 *
	 * @param roleOrg 角色授权参数
	 */
	@PostMapping("auth")
	public String authOrg(@RequestBody Map<String, Object> roleOrg) {
		Long orgId = Long.parseLong(roleOrg.get("orgId").toString());
		Long archId = Long.parseLong(roleOrg.get("archId").toString());
		Long comId = Long.parseLong(roleOrg.get("comId").toString());
		List<String> roleIds = (List<String>) roleOrg.get("roleIds");
		Long curUserId = this.getCurUserId();
		String uip = this.getUserIp();

		this.service.authOrg(roleIds, orgId, archId, comId, curUserId, uip);

		return this.resJson.ok("ok");
	}

	/**
	 * 给组织添加成员，一般系统级的用户组都是在登录、付费升级等业务应用中自动添加成员，此处为租户给自己的组织新增成员。
	 *
	 * @param orgUser 添加人员参数
	 * @return 添加结果
	 */
	@PostMapping("user")
	public String userOrg(@RequestBody Map<String, Object> orgUser) {
		Long orgId = Long.parseLong(orgUser.get("orgId").toString());
		Long archId = Long.parseLong(orgUser.get("archId").toString());
		Long comId = Long.parseLong(orgUser.get("comId").toString());
		List<String> userIds = (List<String>) orgUser.get("userIds");
		Long curUserId = this.getCurUserId();
		String uip = this.getUserIp();

		String message = this.service.userOrg(userIds, orgId, archId, comId, curUserId, uip);

		return this.resJson.ok(message);
	}

	@DeleteMapping("staffDel")
	public Boolean removeOrgStaff(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<String>) params.get("ids")).stream().map(Long::parseLong).collect(Collectors.toList());
		Long orgId = Long.parseLong(ObjectUtils.string(params.get("orgId")));
		this.service.removeOrgStaff(ids, orgId);

		return Boolean.TRUE;
	}

	@Override
	protected void postAdd(WoOrg org) {
		this.refreshAuth();
	}

	@Override
	protected void postUpdate(WoOrg org) {
		this.refreshAuth();
	}

	@Override
	protected void postDelete(WoOrg org) {
		this.refreshAuth();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.refreshAuth();
	}

	/**
	 * 查询组织列表时追加平台根组织
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	@Override
	protected List<WoOrg> doFilter(List<WoOrg> res) {
		if (this.isPlatformAdmin(this.getTenantId())) {
			WoOrg plat = new WoOrg(Constants.TOP_ORG_ID, "平台");
			if (res.isEmpty()) {
				res.add(plat);
			}else
				res.set(0, plat);
		}

		return res;
	}

	/**
	 * 组织类型枚举
	 *
	 * @return 枚举列表
	 */
	@GetMapping("type")
	public List<Map<String, Object>> fetchEnumAppType() {
		// 只有平台侧可以操作、查看系统用户组
		return (this.isPlatformAdmin(this.getTenantId()) ?
					Arrays.stream(OrgTypeEnum.values())
					: Arrays.stream(OrgTypeEnum.values()).filter(v -> !v.getValue().equals(Constants.ENUM_TYPE_ORG_PLAT))).map(item -> {
			Map<String, Object> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
	}
}
