/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.List;

import com.wldos.system.core.entity.WoRoleOrg;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 角色组织仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface RoleOrgRepo extends PagingAndSortingRepository<WoRoleOrg, Long> {
	@Query("select r.* from wo_role_org r where r.delete_flag='normal' and r.is_valid='1' and r.org_id=:orgId")
	List<WoRoleOrg> queryRoleByOrgId(Long orgId);

	@Modifying
	@Query("delete from wo_role_org where org_id=:orgId and arch_id=:archId and com_id=:comId")
	void deleteByOrgIdAndArchIdAndComId(Long orgId, Long archId, Long comId);
}
