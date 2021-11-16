/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.List;

import com.wldos.system.core.entity.WoDomainApp;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 域应用关联仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface DomainAppRepo extends PagingAndSortingRepository<WoDomainApp, Long> {
	@Modifying
	@Query("delete from wo_domain_app where domain_id=:domainId and app_id in (:ids)")
	void removeDomainApp(List<Long> ids, Long domainId);

	boolean existsByAppIdAndDomainId(Long appId, Long domainId);

	List<WoDomainApp> queryAllByDomainIdAndComIdAndIsValidAndDeleteFlag(Long domainId, Long comId, String isValid, String deleteFlag);
}
