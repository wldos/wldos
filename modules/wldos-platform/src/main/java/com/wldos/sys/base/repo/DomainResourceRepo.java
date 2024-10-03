/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.framework.repo.BaseRepo;
import com.wldos.sys.base.entity.WoDomainResource;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 平台域资源仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface DomainResourceRepo extends BaseRepo<WoDomainResource, Long>, DomainResourceJdbc {

	/**
	 * 查询某域的关联业务分类的所有资源
	 *
	 * @return 域的动态路由资源
	 */
	@Query("select r.* from wo_domain_resource r where r.domain_id=:domainId and r.is_valid = '1' and r.delete_flag = 'normal' and r.term_type_id != 0")
	List<WoDomainResource> queryDomainResWithTerm(@Param("domainId") Long domainId);

	boolean existsByResourceIdAndAppIdAndDomainId(@Param("ResId") Long ResId, @Param("appId") Long appId, @Param("domainId") Long domainId);

	@Query("select 'static' module_name, r.id resource_id, 0 term_type_id, r.app_id, :domId domain_id, r.is_valid, r.create_by, r.create_ip, r.create_time, r.delete_flag, r.remark, r.update_by, "
			+ "r.update_ip, r.update_time from wo_resource r where r.id=:resId "
			+ "and not exists (select 1 from wo_domain_resource s where s.resource_id=r.id and s.app_id=r.app_id and s.is_valid='1' and s.delete_flag='normal' and s.domain_id=:domId)")
	WoDomainResource queryDomResById(@Param("resId") Long resId, @Param("domId") Long domId);

	/**
	 * 查询某域的资源
	 *
	 * @param domainId 域Id
	 * @param resIds 域选择的资源ids
	 * @return 域的资源
	 */
	@Query("select r.* from wo_domain_resource r where r.domain_id=:domainId and r.resource_id in (:resIds) and r.is_valid = '1' and r.delete_flag = 'normal'")
	List<WoDomainResource> queryDomainRes(@Param("domainId") Long domainId, @Param("resIds") List<Long> resIds);

	/**
	 * 查询某域的资源
	 *
	 * @param domainId 域Id
	 * @return 域的资源
	 */
	@Query("select r.* from wo_domain_resource r where r.domain_id=:domainId and r.is_valid = '1' and r.delete_flag = 'normal'")
	List<WoDomainResource> queryDomainRes(@Param("domainId") Long domainId);

	List<WoDomainResource> queryAllByDomainIdAndIsValidAndDeleteFlag(@Param("domainId") Long domainId, @Param("isValid") String isValid, @Param("deleteFlag") String deleteFlag);

	@Modifying
	@Query("delete from wo_domain_resource where domain_id=:domainId and resource_id in (:ids)")
	void removeDomainRes(@Param("ids") List<Long> ids, @Param("domainId") Long domainId);
}
