/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.service;

import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.DomainOptionDao;
import com.wldos.platform.core.entity.WoDomainOption;

import io.github.wldos.common.utils.ObjectUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域级选项服务（与全局 {@link OptionsService} 配合：域优先、全局回退）。
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DomainOptionService extends EntityService<DomainOptionDao, WoDomainOption, Long> {

	@Transactional(readOnly = true)
	public String findOptionValue(Long domainId, String optionKey) {
		if (domainId == null || ObjectUtils.isBlank(optionKey)) {
			return "";
		}
		WoDomainOption row = this.entityRepo.findByDomainIdAndOptionKey(domainId, optionKey);
		if (row == null || ObjectUtils.isBlank(row.getOptionValue())) {
			return "";
		}
		return row.getOptionValue();
	}
}
