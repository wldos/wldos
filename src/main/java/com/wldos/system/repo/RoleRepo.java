/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import java.util.List;

import com.wldos.system.entity.WoRole;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 角色仓库操作类。
 *
 * @Title WoRoleRepo
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public interface RoleRepo extends PagingAndSortingRepository<WoRole, Long> {
	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_type=:roleType")
	List<WoRole> findAllTenant(String roleType);
}
