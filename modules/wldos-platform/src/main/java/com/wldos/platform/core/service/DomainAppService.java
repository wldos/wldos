/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.List;
import java.util.stream.Collectors;

import com.wldos.framework.mvc.service.EntityService;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.platform.core.dao.DomainAppDao;
import com.wldos.platform.core.entity.WoDomainApp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域应用service。
 *
 * @author 元悉宇宙
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DomainAppService extends EntityService<DomainAppDao, WoDomainApp, Long> {

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
