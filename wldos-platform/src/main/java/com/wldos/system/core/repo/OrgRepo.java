/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.List;

import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.vo.Org;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 组织结构仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface OrgRepo extends PagingAndSortingRepository<WoOrg, Long> {
	@Query("select o.* from wo_org o where o.delete_flag=:deleteFlag and o.is_valid=:isValid and o.org_type=:orgType")
	List<Org> findAllByOrgType(String deleteFlag, String isValid, String orgType);

	WoOrg findByOrgCodeAndIsValidAndDeleteFlag(String orgCode, String isValid, String deleteFlag);
}
