/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.List;

import com.wldos.system.core.entity.WoDomainResource;
import com.wldos.system.core.entity.WoResource;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 平台域资源仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface DomainResourceRepo extends PagingAndSortingRepository<WoDomainResource, Long> {

	@Query("select r.* from wo_domain_resource r where r.domain_id=(select d.id from wo_domain d where d.site_domain=:domain) and r.app_id in (:appIds) and r.resource_id in (:resIds) and r.is_valid = '1' and r.delete_flag = 'normal' and r.module_name != 'static'")
	List<WoDomainResource> queryDomainDynamicRoutes(String domain, List<Long> appIds, List<Long> resIds);

	@Query("select r.* from wo_domain_resource r where r.domain_id=:domainId and r.is_valid = '1' and r.delete_flag = 'normal' and r.module_name != 'static'")
	List<WoDomainResource> queryDomainDynamicRoutes(Long domainId);

	@Query("select r.* from wo_domain_resource r where r.domain_id=:domainId and r.is_valid = '1' and r.delete_flag = 'normal' and r.term_type_id != 0")
	List<WoDomainResource> queryDomainResWithTerm(Long domainId);

	boolean existsByResourceIdAndAppIdAndDomainId(Long ResId, Long appId, Long domainId);

	@Query("select 'static' module_name, r.resource_path route_path, r.id resource_id, 0 term_type_id, r.app_id, :domId domain_id, r.is_valid, r.create_by, r.create_ip, r.create_time, r.delete_flag, r.remark, r.update_by, "
			+ "r.update_ip, r.update_time from wo_resource r where r.id=:resId "
			+ "and not exists (select 1 from wo_domain_resource s where s.resource_id=r.id and s.app_id=r.app_id and s.is_valid='1' and s.delete_flag='normal' and s.domain_id=:domId)")
	WoDomainResource queryDomResById(Long resId, Long domId);

	@Query("select r.* from wo_domain_resource r where r.domain_id=:domainId and r.resource_id in (:resIds) and r.is_valid = '1' and r.delete_flag = 'normal'")
	List<WoDomainResource> queryDomainRes(Long domainId, List<Long> resIds);

	List<WoDomainResource> queryAllByDomainIdAndIsValidAndDeleteFlag(Long domainId, String isValid, String deleteFlag);

	@Modifying
	@Query("delete from wo_domain_resource where domain_id=:domainId and resource_id in (:ids)")
	void removeDomainRes(List<Long> ids, Long domainId);
}
