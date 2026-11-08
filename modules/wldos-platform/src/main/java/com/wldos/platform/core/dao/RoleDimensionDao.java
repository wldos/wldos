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
import com.wldos.platform.core.entity.WoRoleDimension;

/**
 * 角色扩展维选用 DAO。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface RoleDimensionDao extends BaseDao<WoRoleDimension, Long> {

	@Query("SELECT * FROM wo_role_dimension WHERE role_id = :roleId "
			+ "AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoRoleDimension> findByRoleId(@Param("roleId") Long roleId);

	@Modifying
	@Query("DELETE FROM wo_role_dimension WHERE role_id = :roleId")
	void deleteByRoleId(@Param("roleId") Long roleId);

	@Query("SELECT COUNT(1) FROM wo_role_dimension WHERE dimension_code = :dimensionCode "
			+ "AND delete_flag = 'normal' AND is_valid = '1'")
	long countByDimensionCode(@Param("dimensionCode") String dimensionCode);
}
