/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.resource.impl;

import com.wldos.common.utils.ObjectUtils;
import com.wldos.platform.support.resource.ResourceOpener;
import com.wldos.platform.support.resource.entity.WoResource;
import com.wldos.platform.support.resource.vo.AuthInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认 ResourceOpener 实现类（开源版本）
 * 当 Agent 模块中的 ResourceOpenerImpl 不存在时使用此实现
 * 使用降级处理：简化权限查询逻辑，直接从数据库查询资源，不进行复杂的角色权限判断
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2026/01/10
 */
@ConditionalOnMissingClass("com.wldos.support.resource.ResourceOpenerImpl")
@Component("resourceOpener")
public class DefaultResourceOpenerImpl implements ResourceOpener {

	@Autowired
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	@Override
	public List<AuthInfo> queryAuthInfo(Long domainId, Long comId, String appCode, Long userId) {
		// 降级处理：简化查询，直接查询应用下的所有资源（不进行用户权限过滤）
		StringBuilder sql = new StringBuilder("select r.resource_code, r.resource_name, r.resource_path, r.resource_type, r.request_method from ")
				.append("wo_resource r where r.delete_flag='normal' and r.is_valid='1' ")
				.append("and r.app_id=(select p.id from wo_app p where p.delete_flag='normal' and p.is_valid='1' and p.app_code=:appCode) ")
				.append("and exists(select 1 from wo_domain_resource d where d.is_valid='1' ")
				.append("and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) ")
				.append("order by r.parent_id | r.display_order");

		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);
		params.put("appCode", appCode);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(AuthInfo.class));
	}

	@Override
	public List<AuthInfo> queryAuthInfo(String appCode) {
		if (ObjectUtils.existsBlank(appCode)) {
			throw new RuntimeException("参数appCode不可以为空……");
		}
		StringBuilder sql = new StringBuilder("select r.resource_code, r.resource_name, r.resource_path, r.resource_type, r.request_method from ")
				.append("wo_resource r where r.delete_flag='normal' and r.is_valid='1' ")
				.append("and r.app_id=(select p.id from wo_app p where p.delete_flag='normal' and p.is_valid='1' and p.app_code=:appCode) ");

		Map<String, String> params = new HashMap<>();
		params.put("appCode", appCode);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(AuthInfo.class));
	}

	@Override
	public List<WoResource> queryResource(Long domainId, Long comId, String type, Long userId) {
		// 降级处理：简化查询，直接查询域下的资源（不进行用户权限过滤）
		StringBuilder sql = new StringBuilder("select r.* from wo_resource r ")
				.append("where r.delete_flag='normal' and r.is_valid='1' and r.resource_type=:type ")
				.append("and exists(select 1 from wo_domain_resource d where d.is_valid='1' ")
				.append("and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) ")
				.append("order by r.parent_id | r.display_order");

		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);
		params.put("type", type);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}

	@Override
	public List<WoResource> queryResource(Long domainId, Long comId, String[] types, Long userId) {
		// 降级处理：简化查询，直接查询域下的资源（不进行用户权限过滤）
		StringBuilder sql = new StringBuilder("select r.* from wo_resource r ")
				.append("where r.delete_flag='normal' and r.is_valid='1' and r.resource_type in (:types) ")
				.append("and exists(select 1 from wo_domain_resource d where d.is_valid='1' ")
				.append("and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) ")
				.append("order by r.parent_id | r.display_order");

		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);
		params.put("types", Arrays.asList(types));

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}

	@Override
	public List<WoResource> queryResourceByRoleId(Long roleId) {
		StringBuilder sql = new StringBuilder("select r.* from wo_resource r ")
				.append("join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id ")
				.append("where a.delete_flag='normal' and a.is_valid='1' and a.role_id=:roleId ")
				.append("order by r.parent_id | r.display_order");

		Map<String, Long> params = new HashMap<>();
		params.put("roleId", roleId);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}

	@Override
	public List<WoResource> queryResourceByInheritRoleId(Long roleId) {
		// 降级处理：简化继承角色查询
		StringBuilder sql = new StringBuilder("select r.* from wo_resource r ")
				.append("join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id ")
				.append("join (")
				.append("select m.id, m.parent_id, n.parent_id grand_id from wo_role m left join wo_role n on m.parent_id=n.id ")
				.append("where m.delete_flag='normal' and m.is_valid='1' and n.delete_flag='normal' and n.is_valid='1' and m.id=:roleId) u ")
				.append("on u.parent_id=a.role_id or u.grand_id=a.role_id ")
				.append("where a.delete_flag='normal' and a.is_valid='1' ")
				.append("order by r.parent_id | r.display_order");

		Map<String, Long> params = new HashMap<>();
		params.put("roleId", roleId);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}

	@Override
	public List<AuthInfo> queryAuthInfoForGuest(Long domainId, String appCode) {
		StringBuilder sql = new StringBuilder("select r.resource_code, r.resource_name, r.resource_path, r.resource_type, r.request_method from wo_resource r ")
				.append("join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id ")
				.append("join (select r.id own_id from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_code='guest') u ")
				.append("on a.role_id=u.own_id ")
				.append("where r.delete_flag='normal' and r.is_valid='1' and a.delete_flag='normal' and a.is_valid='1' ")
				.append("and r.app_id=(select p.id from wo_app p where p.delete_flag='normal' and p.is_valid='1' and p.app_code=:appCode) ")
				.append("and exists(select 1 from wo_domain_resource d where d.is_valid='1' ")
				.append("and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) ")
				.append("order by r.parent_id | r.display_order");

		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);
		params.put("appCode", appCode);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(AuthInfo.class));
	}

	@Override
	public List<WoResource> queryResourceForGuest(Long domainId, String type) {
		if (ObjectUtils.existsBlank(type)) {
			throw new RuntimeException("type不可以为空……");
		}
		StringBuilder sql = new StringBuilder("select r.* from wo_resource r ")
				.append("join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id ")
				.append("join (select r.id own_id from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_code='guest') u ")
				.append("on a.role_id=u.own_id ")
				.append("where r.delete_flag='normal' and r.is_valid='1' and a.delete_flag='normal' and a.is_valid='1' and r.resource_type=:type ")
				.append("and exists(select 1 from wo_domain_resource d where d.is_valid='1' ")
				.append("and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) ")
				.append("order by r.parent_id | r.display_order");

		Map<String, Object> params = new HashMap<>();
		params.put("type", type);
		params.put("domainId", domainId);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}

	@Override
	public List<WoResource> queryResourceForGuest(Long domainId, String[] types) {
		if (types == null || types.length == 0) {
			throw new RuntimeException("type不可以为空……");
		}
		StringBuilder sql = new StringBuilder("select r.* from wo_resource r ")
				.append("join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id ")
				.append("join (select r.id own_id from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_code='guest') u ")
				.append("on a.role_id=u.own_id ")
				.append("where r.delete_flag='normal' and r.is_valid='1' and a.delete_flag='normal' and a.is_valid='1' and r.resource_type in (:types) ")
				.append("and exists(select 1 from wo_domain_resource d where d.is_valid='1' ")
				.append("and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) ")
				.append("order by r.parent_id | r.display_order");

		Map<String, Object> params = new HashMap<>();
		params.put("types", Arrays.asList(types));
		params.put("domainId", domainId);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}
}
