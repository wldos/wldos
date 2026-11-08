/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;


import com.wldos.framework.mvc.dao.BaseDao;

import com.wldos.platform.core.entity.WoAuthRole;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 角色资源授权仓库操作类。
 *
 * @author Yuanxi Universe
 * @date 2021/4/27
 * @version 1.0
 */
public interface AuthRoleDao extends BaseDao<WoAuthRole, Long> {
	@Modifying
	@Query("delete from wo_auth_role where role_id=:roleId and domain_id is null and com_id is null")
	void deleteBaselineByRoleId(@Param("roleId") Long roleId);

	@Modifying
	@Query("delete from wo_auth_role where role_id=:roleId and domain_id is not null")
	void deleteDomainOverlaysByRoleId(@Param("roleId") Long roleId);

	@Modifying
	@Query("delete from wo_auth_role where role_id=:roleId")
	void deleteAllByRoleId(@Param("roleId") Long roleId);

	@Modifying
	@Query("delete from wo_auth_role where role_id=:roleId and domain_id=:domainId and com_id is null")
	void deleteOverlayByRoleIdAndDomainId(@Param("roleId") Long roleId, @Param("domainId") Long domainId);

	@Modifying
	@Query("delete from wo_auth_role where role_id=:roleId and com_id=:comId and domain_id is null")
	void deleteOverlayByRoleIdAndComId(@Param("roleId") Long roleId, @Param("comId") Long comId);

	@Query("SELECT COUNT(1) FROM wo_auth_role WHERE role_id = :roleId AND delete_flag = 'normal'")
	long countByRoleId(@Param("roleId") Long roleId);

	@Query("SELECT COUNT(1) FROM wo_auth_role WHERE resource_id = :resourceId AND app_id = :appId AND delete_flag = 'normal' AND is_valid = '1'")
	long countActiveByResourceIdAndAppId(@Param("resourceId") Long resourceId, @Param("appId") Long appId);

	@Query("SELECT r.role_name FROM wo_auth_role a INNER JOIN wo_role r ON r.id = a.role_id AND r.delete_flag = 'normal' WHERE a.resource_id = :resourceId AND a.app_id = :appId AND a.delete_flag = 'normal' AND a.is_valid = '1' LIMIT 5")
	java.util.List<String> listActiveRoleNamesByResourceIdAndAppId(@Param("resourceId") Long resourceId, @Param("appId") Long appId);

	@Modifying
	@Query("delete from wo_auth_role where resource_id=:resId and app_id=:appId")
	void deleteByResourceIdAndAppId(@Param("resId") Long resId, @Param("appId") Long appId);

	@Modifying
	@Query("delete from wo_auth_role where app_id=:id")
	void deleteByAppId(@Param("id") Long id);

	@Query("select a.* from wo_auth_role a where a.role_id=:roleId and a.resource_id=:resourceId and a.app_id=:appId and a.delete_flag='normal' limit 1")
	WoAuthRole findByRoleIdAndResourceIdAndAppId(@Param("roleId") Long roleId, @Param("resourceId") Long resourceId, @Param("appId") Long appId);

	@Query("select a.* from wo_auth_role a where a.resource_id=:resourceId and a.app_id=:appId and a.delete_flag='normal' and a.is_valid='1'")
	java.util.List<WoAuthRole> findAllActiveByResourceIdAndAppId(@Param("resourceId") Long resourceId, @Param("appId") Long appId);

	@Query("select a.* from wo_auth_role a where a.app_id=:appId and a.delete_flag=:deleteFlag")
	java.util.List<WoAuthRole> findAllByAppIdAndDeleteFlag(@Param("appId") Long appId, @Param("deleteFlag") String deleteFlag);

	@Query("select a.* from wo_auth_role a where a.role_id=:roleId and a.delete_flag='normal'")
	java.util.List<WoAuthRole> findAllByRoleId(@Param("roleId") Long roleId);

	@Modifying
	@Query("UPDATE wo_auth_role SET app_id = :newAppId WHERE resource_id = :resourceId AND app_id = :oldAppId AND delete_flag = 'normal'")
	int updateAppIdByResourceIdAndOldAppId(
			@Param("resourceId") Long resourceId,
			@Param("oldAppId") Long oldAppId,
			@Param("newAppId") Long newAppId);

	@Modifying
	@Query("UPDATE wo_auth_role SET app_id = :newAppId WHERE resource_id = :resourceId AND app_id IS NULL AND delete_flag = 'normal'")
	int updateAppIdByResourceIdWhereAppIdIsNull(@Param("resourceId") Long resourceId, @Param("newAppId") Long newAppId);
}
