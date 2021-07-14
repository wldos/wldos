/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import java.util.List;

import com.wldos.system.entity.WoRoleOrg;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 角色组织仓库操作类。
 *
 * @Title RoleOrgRepo
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public interface RoleOrgRepo extends PagingAndSortingRepository<WoRoleOrg, Long> {
	@Query("select r.* from wo_role_org r where r.delete_flag='normal' and r.is_valid='1' and r.org_id=:orgId")
	List<WoRoleOrg> queryRoleByOrgId(Long orgId);

	@Modifying
	@Query("delete from wo_role_org where org_id=:orgId and arch_id=:archId and com_id=:comId")
	void deleteByOrgIdAndArchIdAndComId(Long orgId, Long archId, Long comId);
}
