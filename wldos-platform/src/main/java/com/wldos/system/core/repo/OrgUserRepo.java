/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.List;

import com.wldos.system.core.entity.WoOrgUser;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 用户归属组织仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/5/9
 * @version 1.0
 */
public interface OrgUserRepo extends PagingAndSortingRepository<WoOrgUser, Long> {
	@Modifying
	@Query("delete from wo_org_user where org_id=:orgId and user_id in (:ids)")
	void removeOrgStaff(List<Long> ids, Long orgId);

	@Query("select c.* from wo_org_user c where c.is_valid = :isValid and c.delete_flag = :deleteFlag and c.org_id=:orgId and c.arch_id=:archId and c.com_id=:comId and c.user_id in (:userIds)")
	List<WoOrgUser> queryAllByUserIds(Long orgId, Long archId, Long comId, List<Long> userIds, String isValid, String deleteFlag);
}
