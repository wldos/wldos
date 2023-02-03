/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;


import com.wldos.sys.base.entity.WoAuthRole;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 角色资源授权仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface AuthRoleRepo extends PagingAndSortingRepository<WoAuthRole, Long> {
	@Modifying
	@Query("delete from wo_auth_role where role_id=:roleId")
	void deleteByRoleId(@Param("roleId") Long roleId);
}
