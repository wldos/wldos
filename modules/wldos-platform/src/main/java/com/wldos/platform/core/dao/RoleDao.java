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
import com.wldos.platform.core.entity.WoRole;

import com.wldos.platform.core.vo.RoleIdCode;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 角色仓库操作类。
 *
 * @author Yuanxi Universe
 * @date 2021/4/27
 * @version 1.0
 */
public interface RoleDao extends BaseDao<WoRole, Long> {
	/**
	 * 租户可指派的业务角色模板：{@code com_id=0} 为平台预置模板（跨租户共享消费），{@code com_id=:comId} 为
	 * 本租户自建；不返回其它租户自建的同类角色，避免跨租户挂接泄漏。
	 */
	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_type=:roleType and (r.com_id=0 or r.com_id=:comId)")
	List<WoRole> findAllTenant(@Param("roleType") String roleType, @Param("comId") Long comId);

	/**
	 * 组织授权候选专用：本租户自建（com_id=comId）+ 平台共享模板（com_id=0 且 domain_id 为空/等于目标站点）。
	 * 与上面 {@link #findAllTenant(String, Long)} 的区别在于不把"com_id=0 但 domain_id 钉死在某个具体站点"的行
	 * 当成平台共享模板——那种行只属于它自己站点，不该跨站点出现在别的租户/站点的授权候选列表里，
	 * 否则列表可选、提交时会被域校验拒绝。
	 */
	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_type=:roleType "
			+ "and (r.com_id=:comId or (r.com_id=0 and (r.domain_id is null or r.domain_id=:domainId)))")
	List<WoRole> findAllTenantForOrgAuth(
			@Param("roleType") String roleType, @Param("comId") Long comId, @Param("domainId") Long domainId);

	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.com_id=0 and r.role_code in (:roleCodes)")
	List<WoRole> findAllOperationalByRoleCodes(@Param("roleCodes") List<String> roleCodes);

	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.com_id=:comId and r.domain_id=:domainId and r.role_code in (:roleCodes)")
	List<WoRole> findOperationalByComIdAndDomainId(
			@Param("comId") Long comId,
			@Param("domainId") Long domainId,
			@Param("roleCodes") List<String> roleCodes);

	@Query("select count(1) from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.com_id=:comId and r.domain_id=:domainId and r.role_code in (:roleCodes)")
	long countOperationalByComIdAndDomainId(
			@Param("comId") Long comId,
			@Param("domainId") Long domainId,
			@Param("roleCodes") List<String> roleCodes);

	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.com_id=:comId and r.domain_id=:domainId and r.role_code=:roleCode limit 1")
	WoRole findByComIdAndDomainIdAndRoleCode(
			@Param("comId") Long comId,
			@Param("domainId") Long domainId,
			@Param("roleCode") String roleCode);

	@Query("SELECT id, role_code FROM wo_role WHERE role_code IN ( :roleCodes )")
	List<RoleIdCode> findAllByRoleCode(@Param("roleCodes") List<String> roleCodes);

	@Query("SELECT COUNT(1) FROM wo_role_org WHERE role_id = :roleId AND delete_flag = 'normal'")
	long countRoleOrgByRoleId(@Param("roleId") Long roleId);

	@Query("SELECT COUNT(1) FROM wo_auth_role WHERE role_id = :roleId AND delete_flag = 'normal'")
	long countAuthRoleByRoleId(@Param("roleId") Long roleId);

	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.com_id=0 and r.domain_id is null and r.role_code=:roleCode limit 1")
	WoRole findPlatformTemplateByRoleCode(@Param("roleCode") String roleCode);

	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.role_code in (:roleCodes) "
			+ "and not (r.com_id=0 and r.domain_id is null)")
	List<WoRole> findNonTemplateIdentityRoles(@Param("roleCodes") List<String> roleCodes);
}
