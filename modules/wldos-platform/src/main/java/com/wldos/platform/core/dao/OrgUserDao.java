/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoOrgUser;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 用户归属组织仓库操作类。
 *
 * @author Yuanxi Universe
 * @date 2021/5/9
 * @version 1.0
 */
public interface OrgUserDao extends BaseDao<WoOrgUser, Long> {
	@Modifying
	@Query("delete from wo_org_user where org_id=:orgId and user_id in (:ids)")
	void removeOrgStaff(List<Long> ids, Long orgId);

	@Query("select c.* from wo_org_user c where c.is_valid =:isValid and c.delete_flag =:deleteFlag and c.org_id=:orgId and c.user_id=:userId")
	WoOrgUser queryByUserIdAndOrgId(Long orgId, Long userId, String isValid, String deleteFlag);

	@Query("select c.* from wo_org_user c where c.user_id = :userId and c.is_valid = '1' and c.delete_flag = 'normal' "
			+ "and c.com_id is not null and c.com_id > 0 order by c.id asc limit 1")
	WoOrgUser findFirstValidMembershipByUserId(@Param("userId") Long userId);

	@Query("select c.* from wo_org_user c where c.is_valid =:isValid and c.delete_flag =:deleteFlag and c.org_id=:orgId and c.arch_id=:archId and c.com_id=:comId and c.user_id in (:userIds)")
	List<WoOrgUser> queryAllByUserIds(Long orgId, Long archId, Long comId, List<Long> userIds, String isValid, String deleteFlag);

	/**
	 * 查询某租户管理员在指定用户中存在的列表
	 *
	 * @param tAdminOrgId 租户管理员系统用户组id
	 * @param userComId 待查询的租户id
	 * @param userIds 待查询用户id
	 * @param isValid 是否有效
	 * @param deleteFlag 删除标识
	 * @return 租户管理员列表
	 */
	@Query("select c.* from wo_org_user c where c.is_valid =:isValid and c.delete_flag =:deleteFlag and c.org_id=:tAdminOrgId and c.user_com_id=:userComId and c.user_id in (:userIds)")
	List<WoOrgUser> queryAllByUserIds(Long tAdminOrgId, Long userComId, List<Long> userIds, String isValid, String deleteFlag);

	/**
	 * 查询指定用户存在租户管理员的列表
	 *
	 * @param tAdminOrgId 租户管理员系统用户组id
	 * @param userIds 待查询用户id
	 * @param isValid 是否有效
	 * @param deleteFlag 删除标识
	 * @return 租户管理员列表
	 */
	@Query("select c.* from wo_org_user c where c.is_valid =:isValid and c.delete_flag =:deleteFlag and c.org_id=:tAdminOrgId and c.user_id in (:userIds)")
	List<WoOrgUser> queryAllByUserIds(Long tAdminOrgId, List<Long> userIds, String isValid, String deleteFlag);

	/**
	 * 指定组织若为「至少保留一名成员」的系统保留身份组（admin/tadmin/dadmin/can_trust），取出其全部成员；
	 * 用于移除成员前校验「不能移除到只剩 0 人」。
	 *
	 * @param platComId 平台系统组id（固定为 {@link io.github.wldos.common.Constants#TOP_COM_ID}）
	 * @param orgCode 目标组织的 org_code（由调用方先查出，逐一匹配四个受保护身份组常量）
	 * @param orgId 待移除成员的组织id
	 * @return 该组织确实是对应身份组时返回其全部成员，否则返回空
	 */
	@Query("select u.* from wo_org_user u where exists (select 1 from wo_org g where g.id=u.org_id "
			+ "and g.arch_id=u.arch_id and g.com_id=u.com_id and g.delete_flag='normal' and g.is_valid='1' and g.com_id=:platComId and g.org_code=:orgCode and g.id=:orgId)")
	List<WoOrgUser> existsAdmin(Long platComId, String orgCode, Long orgId);

	/** 平台模板组织（无 site）上的有效成员关系 */
	@Query("select u.* from wo_org_user u inner join wo_org o on u.org_id = o.id "
			+ "where u.delete_flag='normal' and u.is_valid='1' and o.delete_flag='normal' and o.is_valid='1' "
			+ "and o.domain_id is null and o.org_code in (:orgCodes)")
	List<WoOrgUser> findValidMembershipsOnKindTemplateOrgs(@Param("orgCodes") List<String> orgCodes);

	/** 平台身份组织上的旧成员关系（应以身份登记册为准） */
	@Query("select u.* from wo_org_user u inner join wo_org o on u.org_id = o.id "
			+ "where u.delete_flag='normal' and u.is_valid='1' and o.delete_flag='normal' and o.is_valid='1' "
			+ "and o.com_id = 0 and (o.domain_id is null or o.domain_id <= 0) and o.org_code in (:orgCodes)")
	List<WoOrgUser> findValidMembershipsOnIdentityOrgs(@Param("orgCodes") List<String> orgCodes);

	@Query("select count(1) from wo_org_user u where u.delete_flag='normal' and u.is_valid='1' and u.org_id=:orgId")
	long countNormalMembersByOrgId(@Param("orgId") Long orgId);

}
