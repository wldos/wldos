/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoDomainApp;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 域应用关联仓库操作类。
 *
 * @author Yuanxi Universe
 * @date 2021/4/27
 * @version 1.0
 */
public interface DomainAppDao extends BaseDao<WoDomainApp, Long> {
	@Modifying
	@Query("delete from wo_domain_app where domain_id=:domainId and app_id in (:ids)")
	void removeDomainApp(@Param("ids") List<Long> ids, @Param("domainId") Long domainId);

	boolean existsByAppIdAndDomainId(@Param("appId") Long appId, @Param("domainId") Long domainId);

	@Query("select * from wo_domain_app where domain_id=:domainId and app_id=:appId and delete_flag='normal' limit 1")
	WoDomainApp findByDomainIdAndAppId(@Param("domainId") Long domainId, @Param("appId") Long appId);

	@Modifying
	@Query("update wo_domain_app set is_valid=:isValid, update_by=:updateBy, update_ip=:updateIp, update_time=current_timestamp "
			+ "where domain_id=:domainId and app_id in (:appIds) and delete_flag='normal'")
	int updateVisibilityByDomainAndAppIds(
			@Param("domainId") Long domainId,
			@Param("appIds") List<Long> appIds,
			@Param("isValid") String isValid,
			@Param("updateBy") Long updateBy,
			@Param("updateIp") String updateIp);

	List<WoDomainApp> queryAllByDomainIdAndIsValidAndDeleteFlag(@Param("domainId") Long domainId, @Param("isValid") String isValid, @Param("deleteFlag") String deleteFlag);

	@Query("select count(1) from wo_domain_app where domain_id=:domainId and delete_flag='normal'")
	long countByDomainId(@Param("domainId") Long domainId);

	@Query("select app_id from wo_domain_app where domain_id=:domainId and delete_flag='normal'")
	List<Long> listAppIdsByDomainId(@Param("domainId") Long domainId);

    @Modifying
    @Query("delete from wo_domain_app where app_id=:appId")
    void deleteByAppId(@Param("appId") Long appId);
}
