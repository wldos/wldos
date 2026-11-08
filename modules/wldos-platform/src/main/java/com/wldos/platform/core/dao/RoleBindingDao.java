/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoRoleBinding;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 角色多态绑定 Dao。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface RoleBindingDao extends BaseDao<WoRoleBinding, Long> {

	@Query("SELECT * FROM wo_role_binding WHERE target_type = :targetType AND target_id = :targetId AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoRoleBinding> findValidByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

	@Query("SELECT * FROM wo_role_binding WHERE role_id = :roleId AND target_type = :targetType AND target_id = :targetId AND delete_flag = 'normal' AND is_valid = '1' LIMIT 1")
	WoRoleBinding findValidOne(@Param("roleId") Long roleId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

	@Query("SELECT * FROM wo_role_binding WHERE com_id = :comId AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoRoleBinding> findValidByCom(@Param("comId") Long comId);

	/** 占位主键段（2050…）的角色绑定 */
	@Query("SELECT b.* FROM wo_role_binding b WHERE b.delete_flag = 'normal' AND b.is_valid = '1' "
			+ "AND b.id >= 2050000000000000000 AND b.id <= 2059999999999999999")
	List<WoRoleBinding> findValidPlaceholderBindings();

	/** 占位 id 且已有同键正式绑定（apply 时软删占位行） */
	@Query("SELECT b.* FROM wo_role_binding b WHERE b.delete_flag = 'normal' AND b.is_valid = '1' "
			+ "AND b.id >= 2050000000000000000 AND b.id <= 2059999999999999999 "
			+ "AND EXISTS (SELECT 1 FROM wo_role_binding b2 WHERE b2.delete_flag = 'normal' AND b2.is_valid = '1' "
			+ "AND b2.role_id = b.role_id AND b2.target_type = b.target_type AND b2.target_id = b.target_id "
			+ "AND (b2.id < 2050000000000000000 OR b2.id > 2059999999999999999))")
	List<WoRoleBinding> findValidDuplicatePlaceholderBindings();

	/** 占位 id 且无同键正式绑定（apply 时换发真实 id） */
	@Query("SELECT b.* FROM wo_role_binding b WHERE b.delete_flag = 'normal' AND b.is_valid = '1' "
			+ "AND b.id >= 2050000000000000000 AND b.id <= 2059999999999999999 "
			+ "AND NOT EXISTS (SELECT 1 FROM wo_role_binding b2 WHERE b2.delete_flag = 'normal' AND b2.is_valid = '1' "
			+ "AND b2.role_id = b.role_id AND b2.target_type = b.target_type AND b2.target_id = b.target_id "
			+ "AND b2.id <> b.id)")
	List<WoRoleBinding> findValidOrphanPlaceholderBindings();

	@Query("SELECT COUNT(1) FROM wo_role_binding WHERE role_id = :roleId AND delete_flag = 'normal' AND is_valid = '1'")
	long countValidByRoleId(@Param("roleId") Long roleId);
}
