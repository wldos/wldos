/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import com.wldos.system.auth.dto.Tenant;
import com.wldos.system.entity.WoCompany;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 平台公司仓库操作类。
 *
 * @Title CompanyRepo
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public interface CompanyRepo extends PagingAndSortingRepository<WoCompany, Long> {
	/**
	 * 查询所在主企业信息（租户）
	 *
	 * @param userId
	 * @return
	 */
	@Query("SELECT c.* from wo_company c LEFT JOIN wo_com_user u on c.id=u.com_id where c.delete_flag='normal' and c.is_valid='1' and u.delete_flag='normal' and u.is_valid='1' and u.is_main='1' and u.user_id=:userId")
	Tenant queryTenantInfoByTAdminId(Long userId);
}
