/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wldos.common.Constants;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.sys.base.entity.WoResource;
import com.wldos.sys.base.repo.ResourceJdbc;
import com.wldos.sys.base.vo.AuthInfo;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * 平台资源复杂查询实现类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
@Slf4j
public class ResourceJdbcImpl implements ResourceJdbc {
	@Autowired
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	@Override
	public List<AuthInfo> queryAuthInfo(Long domainId, Long comId, String appCode, Long userId) {

		StringBuilder sql = this.makeSql(comId).append("and r.app_id=(select p.id from wo_app p where p.delete_flag='normal' and p.is_valid='1' and p.app_code=:appCode) ")
				.append("and exists(select 1 from wo_domain_resource d where d.is_valid='1' ")
				.append("and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) order by r.parent_id | r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);
		params.put("comId", comId); // 公司(租户)字段可以帮助降低子查询的复杂度
		params.put("appCode", appCode);
		params.put("userId", userId);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(AuthInfo.class));
	}

	@Override
	public List<AuthInfo> queryAuthInfo(String appCode) {
		if (ObjectUtils.existsBlank(appCode)) {
			throw new RuntimeException("参数appId不可以为空……");
		}
		StringBuilder sql = new StringBuilder("select r.resource_code, r.resource_name, r.resource_path, r.resource_type, r.request_method from ")
				.append("wo_resource r where r.delete_flag='normal' and r.is_valid='1' and r.app_id=(select p.id from wo_app p where p.delete_flag='normal' and p.is_valid='1' and p.app_code=:appCode) ");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, String> params = new HashMap<>();
		params.put("appCode", appCode);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(AuthInfo.class));
	}

	@Override
	public List<WoResource> queryResource(Long domainId, Long comId, String type, Long userId) {
		StringBuilder sql = this.makeSql(comId).append("and r.resource_type=:type and exists(select 1 from wo_domain_resource d where d.is_valid='1'")
				.append(" and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) order by r.parent_id | r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);
		params.put("comId", comId);
		params.put("type", type);
		params.put("userId", userId);
		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}

	@Override
	public List<WoResource> queryResourceByRoleId(Long roleId) {
		StringBuilder sql = new StringBuilder("select r.* from wo_resource r ")
				.append(" join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id ")
				.append("where a.delete_flag='normal' and a.is_valid='1' and a.role_id=:roleId order by r.parent_id | r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Long> params = new HashMap<>();
		params.put("roleId", roleId);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}

	@Override
	public List<WoResource> queryResourceByInheritRoleId(Long roleId) {
		StringBuilder sql = new StringBuilder("select r.* from wo_resource r ")
				.append(" join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id join ")
				.append("(")
				.append("select r.parent_id, r.grand_id from(select m.id, m.parent_id, n.parent_id grand_id from wo_role m left join wo_role n on m.parent_id=n.id")
				.append(" where m.delete_flag='normal' and m.is_valid='1' and n.delete_flag='normal' and n.is_valid='1' and m.id=:roleId) r ")
				.append(") u on u.parent_id=a.role_id or u.grand_id=a.role_id ") // 需要关联用户的权限（用户-组织/主体-角色）
				.append("where a.delete_flag='normal' and a.is_valid='1' order by r.parent_id | r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Long> params = new HashMap<>();
		params.put("roleId", roleId);

		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}

	@Override
	public List<AuthInfo> queryAuthInfoForGuest(Long domainId, String appCode) {
		StringBuilder sql = new StringBuilder("select r.resource_code, r.resource_name, r.resource_path, r.resource_type, r.request_method from wo_resource r ")
				.append(" join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id join ")
				.append("(select r.id own_id from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_code='guest' ")
				.append(") u on a.role_id=u.own_id ") // 需要关联用户的权限（用户-组织/主体-角色）
				.append("where r.delete_flag='normal' and r.is_valid='1' and a.delete_flag='normal' and a.is_valid='1' ")
				.append("and r.app_id=(select p.id from wo_app p where p.delete_flag='normal' and p.is_valid='1' and p.app_code=:appCode) ")
				.append("and exists(select 1 from wo_domain_resource d where d.is_valid='1' ")
				.append("and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) order by r.parent_id | r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
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
				.append(" join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id join ")
				.append("(")
				.append("select r.id own_id from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_code='guest' ")
				.append(") u on a.role_id=u.own_id ") // 需要关联用户的权限（用户-组织/主体-角色）
				.append("where r.delete_flag='normal' and r.is_valid='1' and a.delete_flag='normal' and a.is_valid='1' and r.resource_type=:type ")
				.append("and exists(select 1 from wo_domain_resource d where d.is_valid='1' ")
				.append("and d.delete_flag='normal' and d.domain_id=:domainId and d.app_id=r.app_id and d.resource_id=r.id) order by r.parent_id | r.display_order");

		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Object> params = new HashMap<>();
		params.put("type", type);
		params.put("domainId", domainId);
		return this.namedParamJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(WoResource.class));
	}

	private static final StringBuilder sql0 = new StringBuilder("select distinct r.* from wo_resource r ")
			.append("join wo_auth_role a on r.id=a.resource_id and r.app_id=a.app_id ")
			.append("join (")
			.append("select m.id, m.parent_id, n.parent_id grand_id from wo_role m left join wo_role n on m.parent_id=n.id where m.delete_flag='normal' and m.is_valid='1' ")
			.append("and m.id in (") // 实际享有的角色、父角色、爷角色
			.append("select r.id from wo_role r join wo_role_org o on r.id=o.role_id join wo_org_user u on o.org_id=u.org_id and o.arch_id=u.arch_id and o.com_id=u.com_id ")
			.append("where r.delete_flag='normal' and r.is_valid='1' and o.delete_flag='normal' and o.is_valid='1' and u.delete_flag='normal' and u.is_valid='1' ")
			.append("and u.user_id=:userId and u.com_id ");

	private static final StringBuilder sql1 = new StringBuilder("union all ")
			.append("select r.id from wo_role r join wo_subject_association o on r.id=o.role_id join wo_subject_authentication u on o.subject_type_id=u.subject_type_id ")
			.append("where r.delete_flag='normal' and r.is_valid='1' and o.delete_flag='normal' and o.is_valid='1' and u.delete_flag='normal' and u.is_valid='1' ")
			.append("and u.user_id=:userId") // 认证身份的角色
			.append(")) g on g.id=a.role_id or g.parent_id=a.role_id or g.grand_id = a.role_id ")
			//instr(',' || g.id || ',' || g.parent_id || ',' || g.grand_id || ',', ',' || a.role_id || ',') > 0 ") 某些数据情况下可能存在bug
			.append("where r.delete_flag='normal' and r.is_valid='1' and a.delete_flag='normal' and a.is_valid='1' ");

	private StringBuilder makeSql(Long comId) {
		return new StringBuilder(500).append(sql0).append(comId.equals(Constants.TOP_COM_ID) ? "=:comId " : "in (:comId," + Constants.TOP_COM_ID + " ) ")
				.append(sql1); // 所属组织的角色 + 平台默认角色 （不能因为加入x组织丢掉原来的老根本！）
	}
}