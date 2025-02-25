/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoRoleOrg;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 角色组织仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface RoleOrgDao extends BaseDao<WoRoleOrg, Long> {
	@Query("select r.* from wo_role_org r where r.delete_flag='normal' and r.is_valid='1' and r.org_id=:orgId")
	List<WoRoleOrg> queryRoleByOrgId(@Param("orgId") Long orgId);

	@Modifying
	@Query("delete from wo_role_org where org_id=:orgId and arch_id=:archId and com_id=:comId")
	void deleteByOrgIdAndArchIdAndComId(@Param("orgId") Long orgId, @Param("archId") Long archId, @Param("comId") Long comId);
}
