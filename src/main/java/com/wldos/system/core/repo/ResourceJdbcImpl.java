/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wldos.support.repo.FreeJdbcTemplate;
import com.wldos.support.util.ObjectUtil;
import com.wldos.system.vo.AuthInfo;
import com.wldos.system.core.entity.WoResource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

/**
 * 平台资源复杂查询实现类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public class ResourceJdbcImpl extends FreeJdbcTemplate implements ResourceJdbc {
	@Override
	public List<AuthInfo> queryAuthInfo(String appCode, Long userId) {

		StringBuffer sql = this.makeSql().append("and r.app_id=(select p.id from wo_app p where p.delete_flag='normal' and p.is_valid='1' and p.app_code=:appCode) order by r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Object> params = new HashMap<>();
		params.put("appCode", appCode);
		params.put("userId", userId);
		List<AuthInfo> authInfos = this.getNamedParamJdbcTemplate().query(sql.toString(), params, new BeanPropertyRowMapper<>(AuthInfo.class));

		return authInfos;
	}

	@Override
	public List<AuthInfo> queryAuthInfo(String appCode) {
		if (ObjectUtil.existsBlank(appCode)) {
			throw new RuntimeException("参数appId不可以为空……");
		}
		StringBuffer sql = new StringBuffer("select r.resource_code, r.resource_name, r.resource_path, r.resource_type, r.request_method from ")
				.append("wo_resource r where r.delete_flag='normal' and r.is_valid='1' and r.app_id=(select p.id from wo_app p where p.delete_flag='normal' and p.is_valid='1' and p.app_code=:appCode) ");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, String> params = new HashMap<String, String>();
		params.put("appCode", appCode);
		List<AuthInfo> authInfos = this.getNamedParamJdbcTemplate().query(sql.toString(), params, new BeanPropertyRowMapper<>(AuthInfo.class));

		return authInfos;
	}

	@Override
	public List<WoResource> queryResource(String type, Long userId) {
		StringBuffer sql = this.makeSql().append("and r.resource_type=:type  order by r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Object> params = new HashMap<>();
		params.put("type", type);
		params.put("userId", userId);
		List<WoResource> resources = this.getNamedParamJdbcTemplate().query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));

		return resources;
	}

	@Override
	public List<WoResource> queryResourceByRoleId(Long roleId) {
		StringBuffer sql = new StringBuffer("select r.* from wo_resource r ")
				.append(" join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id ")
				.append("where a.delete_flag='normal' and a.is_valid='1' and a.role_id=:roleId order by r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Long> params = new HashMap<>();
		params.put("roleId", roleId);
		List<WoResource> resources = this.getNamedParamJdbcTemplate().query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));

		return resources;
	}

	@Override
	public List<WoResource> queryResourceByInheritRoleId(Long roleId) {
		StringBuffer sql = new StringBuffer("select r.* from wo_resource r ")
				.append(" join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id join ")
				.append("(")
				.append("select r.parent_id, r.grand_id from(select m.id, m.parent_id, n.parent_id grand_id from wo_role m left join wo_role n on m.parent_id=n.id")
				.append(" where m.delete_flag='normal' and m.is_valid='1' and n.delete_flag='normal' and n.is_valid='1' and m.id=:roleId) r ")
				.append(") u on u.parent_id=a.role_id or u.grand_id=a.role_id ") // 需要关联用户的权限（用户-组织/主体-角色）
				.append("where a.delete_flag='normal' and a.is_valid='1' order by r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Long> params = new HashMap<>();
		params.put("roleId", roleId);
		List<WoResource> resources = this.getNamedParamJdbcTemplate().query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));

		return resources;
	}

	@Override
	public List<AuthInfo> queryAuthInfoForGuest(String appCode) {
		StringBuffer sql = new StringBuffer("select r.resource_code, r.resource_name, r.resource_path, r.resource_type, r.request_method from wo_resource r ")
				.append(" join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id join ")
				.append("(")
				.append("select r.id own_id from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_code='guest' ")
				.append(") u on a.role_id=u.own_id ") // 需要关联用户的权限（用户-组织/主体-角色）
				.append("where r.delete_flag='normal' and r.is_valid='1' and a.delete_flag='normal' and a.is_valid='1'  ")
				.append(" and r.app_id=(select p.id from wo_app p where p.delete_flag='normal' and p.is_valid='1' and p.app_code=:appCode) order by r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, String> params = new HashMap<String, String>();
		params.put("appCode", appCode);
		List<AuthInfo> authInfos = this.getNamedParamJdbcTemplate().query(sql.toString(), params, new BeanPropertyRowMapper<>(AuthInfo.class));

		return authInfos;
	}

	@Override
	public List<WoResource> queryResourceForGuest(String type) {
		if (ObjectUtil.existsBlank(type)) {
			throw new RuntimeException("type不可以为空……");
		}
		StringBuffer sql = new StringBuffer("select r.* from wo_resource r ")
				.append(" join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id join ")
				.append("(")
				.append("select r.id own_id from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_code='guest' ")
				.append(") u on a.role_id=u.own_id ") // 需要关联用户的权限（用户-组织/主体-角色）
				.append("where r.delete_flag='normal' and r.is_valid='1' and a.delete_flag='normal' and a.is_valid='1' and r.resource_type=:type order by r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		List<WoResource> resources = this.getNamedParamJdbcTemplate().query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));

		return resources;
	}

	private StringBuffer makeSql() {
		StringBuffer sql = new StringBuffer(1400);
		sql.append("select distinct r.* from wo_resource r ")
				.append("join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id ")
				.append("join ")
				.append("(")
				.append("select m.id, m.parent_id, n.parent_id grand_id from wo_role m left join wo_role n on m.parent_id=n.id where m.delete_flag='normal' and m.is_valid='1' ")
				.append("and m.id in (")
				.append("select r.id from wo_role r join wo_role_org o on r.id=o.role_id join wo_org_user u on o.org_id=u.org_id and o.arch_id=u.arch_id and o.com_id=u.com_id ")
				.append("where r.delete_flag='normal' and r.is_valid='1' and o.delete_flag='normal' and o.is_valid='1' and u.delete_flag='normal' and u.is_valid='1' ")
				.append("and u.user_id=:userId ")
				.append("union all ")
				.append("select r.id from wo_role r join wo_subject_association o on r.id=o.role_id join wo_subject_authentication u on o.subject_type_id=u.subject_type_id ")
				.append("where r.delete_flag='normal' and r.is_valid='1' and o.delete_flag='normal' and o.is_valid='1' and u.delete_flag='normal' and u.is_valid='1' ")
				.append("and u.user_id=:userId")
				.append(")")
				.append(") g on g.id=a.role_id or g.parent_id=a.role_id or g.grand_id = a.role_id ")
				//instr(',' || g.id || ',' || g.parent_id || ',' || g.grand_id || ',', ',' || a.role_id || ',') > 0 ") 某些数据情况下可能存在bug
				.append("where r.delete_flag='normal' and r.is_valid='1' and a.delete_flag='normal' and a.is_valid='1' ");

		return sql;
	}
}
