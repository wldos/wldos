/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;


import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.framework.mvc.dao.RepoExt;

import com.wldos.platform.core.entity.WoAuthRole;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 角色资源授权仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface AuthRoleDao extends BaseDao<WoAuthRole, Long>, RepoExt {
	@Modifying
	@Query("delete from wo_auth_role where role_id=:roleId")
	void deleteByRoleId(@Param("roleId") Long roleId);
}
