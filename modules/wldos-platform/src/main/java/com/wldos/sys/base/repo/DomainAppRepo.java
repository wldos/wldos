/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.framework.repo.BaseRepo;
import com.wldos.sys.base.entity.WoDomainApp;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 域应用关联仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface DomainAppRepo extends BaseRepo<WoDomainApp, Long> {
	@Modifying
	@Query("delete from wo_domain_app where domain_id=:domainId and app_id in (:ids)")
	void removeDomainApp(@Param("ids") List<Long> ids, @Param("domainId") Long domainId);

	boolean existsByAppIdAndDomainId(@Param("appId") Long appId, @Param("domainId") Long domainId);

	List<WoDomainApp> queryAllByDomainIdAndIsValidAndDeleteFlag(@Param("domainId") Long domainId, @Param("isValid") String isValid, @Param("deleteFlag") String deleteFlag);
}
