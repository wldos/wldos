/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.sys.base.entity.WoRole;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 角色仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface RoleRepo extends PagingAndSortingRepository<WoRole, Long> {
	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_type=:roleType")
	List<WoRole> findAllTenant(@Param("roleType") String roleType);
}
