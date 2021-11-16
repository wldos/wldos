/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import com.wldos.system.auth.dto.Tenant;
import com.wldos.system.core.entity.WoCompany;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 平台公司仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface CompanyRepo extends PagingAndSortingRepository<WoCompany, Long> {
	@Query("SELECT c.* from wo_company c LEFT JOIN wo_com_user u on c.id=u.com_id where c.delete_flag='normal' and c.is_valid='1' and u.delete_flag='normal' and u.is_valid='1' and u.is_main='1' and u.user_id=:userId")
	Tenant queryTenantInfoByTAdminId(Long userId);
}
