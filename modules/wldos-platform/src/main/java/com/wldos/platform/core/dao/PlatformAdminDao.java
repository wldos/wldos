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
import com.wldos.platform.core.entity.WoPlatformAdmin;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 平台超管登记册 Dao。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface PlatformAdminDao extends BaseDao<WoPlatformAdmin, Long> {

	@Query("SELECT * FROM wo_platform_admin WHERE user_id = :userId AND delete_flag = 'normal' LIMIT 1")
	WoPlatformAdmin findByUserId(@Param("userId") Long userId);

	@Query("SELECT * FROM wo_platform_admin WHERE user_id IN (:userIds) AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoPlatformAdmin> findByUserIds(@Param("userIds") Collection<Long> userIds);

	@Query("SELECT * FROM wo_platform_admin WHERE delete_flag = 'normal' AND is_valid = '1' ORDER BY create_time DESC")
	List<WoPlatformAdmin> findAllValid();
}
