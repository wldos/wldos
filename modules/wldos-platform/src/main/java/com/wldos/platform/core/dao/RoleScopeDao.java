/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoRoleScope;

public interface RoleScopeDao extends BaseDao<WoRoleScope, Long> {

	@Query("SELECT * FROM wo_role_scope WHERE role_id = :roleId AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoRoleScope> findByRoleId(@Param("roleId") Long roleId);

	@Query("SELECT * FROM wo_role_scope WHERE role_id = :roleId "
			+ "AND ((:comId IS NULL AND :domainId IS NULL AND com_id IS NULL AND domain_id IS NULL) "
			+ "OR (:comId IS NOT NULL AND :domainId IS NULL AND com_id = :comId AND domain_id IS NULL) "
			+ "OR (:domainId IS NOT NULL AND com_id IS NULL AND domain_id = :domainId)) "
			+ "AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoRoleScope> findByRoleIdAndJurisdiction(
			@Param("roleId") Long roleId,
			@Param("comId") Long comId,
			@Param("domainId") Long domainId);

	@Modifying
	@Query("DELETE FROM wo_role_scope WHERE role_id = :roleId")
	void deleteByRoleId(@Param("roleId") Long roleId);

	@Modifying
	@Query("DELETE FROM wo_role_scope WHERE role_id = :roleId "
			+ "AND ((:comId IS NULL AND :domainId IS NULL AND com_id IS NULL AND domain_id IS NULL) "
			+ "OR (:comId IS NOT NULL AND :domainId IS NULL AND com_id = :comId AND domain_id IS NULL) "
			+ "OR (:domainId IS NOT NULL AND com_id IS NULL AND domain_id = :domainId))")
	void deleteByRoleIdAndJurisdiction(
			@Param("roleId") Long roleId,
			@Param("comId") Long comId,
			@Param("domainId") Long domainId);

	@Query("SELECT COUNT(1) FROM wo_role_scope WHERE role_id = :roleId AND delete_flag = 'normal'")
	long countByRoleId(@Param("roleId") Long roleId);

	@Query("SELECT COUNT(1) FROM wo_role_scope WHERE dimension_code = :dimensionCode "
			+ "AND delete_flag = 'normal' AND is_valid = '1'")
	long countByDimensionCode(@Param("dimensionCode") String dimensionCode);
}
