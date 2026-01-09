/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoRole;

import com.wldos.platform.core.vo.RoleIdCode;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 角色仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface RoleDao extends BaseDao<WoRole, Long> {
	@Query("select r.* from wo_role r where r.delete_flag='normal' and r.is_valid='1' and r.role_type=:roleType")
	List<WoRole> findAllTenant(@Param("roleType") String roleType);

	@Query("SELECT id, role_code FROM wo_role WHERE role_code IN ( :roleCodes )")
	List<RoleIdCode> findAllByRoleCode(@Param("roleCodes") List<String> roleCodes);
}
