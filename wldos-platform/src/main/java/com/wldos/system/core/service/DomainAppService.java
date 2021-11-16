/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.List;
import java.util.stream.Collectors;

import com.wldos.support.enums.DeleteFlagEnum;
import com.wldos.support.enums.ValidStatusEnum;
import com.wldos.support.service.BaseService;
import com.wldos.system.core.entity.WoDomainApp;
import com.wldos.system.core.repo.DomainAppRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域应用service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DomainAppService extends BaseService<DomainAppRepo, WoDomainApp, Long> {

	public boolean existsByAppIdAndDomainId(Long appId, Long domainId) {
		return this.entityRepo.existsByAppIdAndDomainId(appId, domainId);
	}

	public void removeDomainApp(List<Long> ids, Long domainId) {
		this.entityRepo.removeDomainApp(ids, domainId);
	}

	public List<Object> queryAppListByDomainId(Long domainId, Long comId) {
		List<WoDomainApp> domainApps =
				this.entityRepo.queryAllByDomainIdAndComIdAndIsValidAndDeleteFlag(domainId, comId, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());

		return domainApps.parallelStream().map(WoDomainApp::getAppId).collect(Collectors.toList());
	}
}
