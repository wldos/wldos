/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.dao;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoObjectScopeBind;

/**
 * 对象范围维绑定 DAO。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface ObjectScopeBindDao extends BaseDao<WoObjectScopeBind, Long> {

	@Query("SELECT COUNT(1) FROM wo_object_scope_bind WHERE dimension_code = :dimensionCode "
			+ "AND delete_flag = 'normal' AND is_valid = '1'")
	long countByDimensionCode(@Param("dimensionCode") String dimensionCode);
}
