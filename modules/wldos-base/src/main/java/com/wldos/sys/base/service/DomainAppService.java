/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.base.service;

import java.util.List;
import java.util.stream.Collectors;

import com.wldos.base.RepoService;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.sys.base.entity.WoDomainApp;
import com.wldos.sys.base.repo.DomainAppRepo;

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
public class DomainAppService extends RepoService<DomainAppRepo, WoDomainApp, Long> {

	public boolean existsByAppIdAndDomainId(Long appId, Long domainId) {
		return this.entityRepo.existsByAppIdAndDomainId(appId, domainId);
	}

	public void removeDomainApp(List<Long> ids, Long domainId) {
		this.entityRepo.removeDomainApp(ids, domainId);
	}

	public List<Object> queryAppListByDomainId(Long domainId) {
		List<WoDomainApp> domainApps =
				this.entityRepo.queryAllByDomainIdAndIsValidAndDeleteFlag(domainId, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());

		return domainApps.parallelStream().map(WoDomainApp::getAppId).collect(Collectors.toList());
	}
}
