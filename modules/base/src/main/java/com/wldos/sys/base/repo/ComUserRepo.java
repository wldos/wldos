/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.sys.base.entity.WoComUser;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 用户归属公司仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/5/9
 * @version 1.0
 */
public interface ComUserRepo extends PagingAndSortingRepository<WoComUser, Long> {
	@Modifying
	@Query("delete from wo_com_user where com_id=:orgId and user_id in (:ids)")
	void removeComStaff(@Param("ids") List<Long> ids, @Param("comId")Long comId);

	/**
	 * 批量查询用户与公司的关联关系
	 *
	 * @param userIds 用户id
	 * @param isValid 是否有效
	 * @param deleteFlag 是否删除
	 * @return 关联关系
	 */
	@Query("select c.* from wo_com_user c where c.is_valid = :isValid and c.delete_flag = :deleteFlag and c.user_id in (:userIds)")
	List<WoComUser> queryAllByUserIds(@Param("userIds") List<Long> userIds, @Param("isValid") String isValid, @Param("deleteFlag") String deleteFlag);
}
