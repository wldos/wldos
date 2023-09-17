/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.sys.base.entity.WoDomain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 租户域管理仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface DomainRepo extends PagingAndSortingRepository<WoDomain, Long> {

	List<WoDomain> findAllByDeleteFlagAndIsValid(@Param("deleteFlag") String deleteFlag, @Param("isValid") String isValid);

	/**
	 * 根据域名查询域信息
	 *
	 * @param siteDomain 域名
	 * @param isValid 是否有效
	 * @param deleteFlag 是否删除
	 * @return 域实例
	 */
	WoDomain findBySiteDomainAndIsValidAndDeleteFlag(@Param("siteDomain") String siteDomain, @Param("isValid") String isValid, @Param("deleteFlag") String deleteFlag);
}
