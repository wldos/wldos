/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.Collection;
import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoTenantAdmin;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 租户管理员登记册 Dao。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface TenantAdminDao extends BaseDao<WoTenantAdmin, Long> {

	@Query("SELECT * FROM wo_tenant_admin WHERE user_id = :userId AND delete_flag = 'normal' LIMIT 1")
	WoTenantAdmin findByUserId(@Param("userId") Long userId);

	@Query("SELECT * FROM wo_tenant_admin WHERE com_id = :comId AND user_id = :userId AND delete_flag = 'normal' LIMIT 1")
	WoTenantAdmin findByComIdAndUserId(@Param("comId") Long comId, @Param("userId") Long userId);

	@Query("SELECT * FROM wo_tenant_admin WHERE user_id IN (:userIds) AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoTenantAdmin> findByUserIds(@Param("userIds") Collection<Long> userIds);

	@Query("SELECT * FROM wo_tenant_admin WHERE delete_flag = 'normal' AND is_valid = '1' ORDER BY create_time DESC")
	List<WoTenantAdmin> findAllValid();

	@Query("SELECT count(1) FROM wo_tenant_admin WHERE com_id = :comId AND delete_flag = 'normal' AND is_valid = '1'")
	long countByComId(@Param("comId") Long comId);
}
