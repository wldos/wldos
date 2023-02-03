/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.sys.base.entity.WoDomainApp;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

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
	void removeDomainApp(@Param("ids") List<Long> ids, @Param("domainId") Long domainId);

	boolean existsByAppIdAndDomainId(@Param("appId") Long appId, @Param("domainId") Long domainId);

	List<WoDomainApp> queryAllByDomainIdAndIsValidAndDeleteFlag(@Param("domainId") Long domainId, @Param("isValid") String isValid, @Param("deleteFlag") String deleteFlag);
}
