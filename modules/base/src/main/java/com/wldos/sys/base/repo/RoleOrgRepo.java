/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.sys.base.entity.WoRoleOrg;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 角色组织仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface RoleOrgRepo extends PagingAndSortingRepository<WoRoleOrg, Long> {
	@Query("select r.* from wo_role_org r where r.delete_flag='normal' and r.is_valid='1' and r.org_id=:orgId")
	List<WoRoleOrg> queryRoleByOrgId(@Param("orgId") Long orgId);

	@Modifying
	@Query("delete from wo_role_org where org_id=:orgId and arch_id=:archId and com_id=:comId")
	void deleteByOrgIdAndArchIdAndComId(@Param("orgId") Long orgId, @Param("archId") Long archId, @Param("comId") Long comId);
}
