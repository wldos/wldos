/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.service;

import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.DomainOptionDao;
import com.wldos.platform.core.entity.WoDomainOption;
import com.wldos.platform.core.enums.SysOptionEnum;

import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.utils.ObjectUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域级选项服务（与全局 {@link OptionsService} 配合：域优先、全局回退）。
 * 
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
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
		if (row == null || ObjectUtils.isBlank(row.getOptionValue())
				|| !ValidStatusEnum.VALID.toString().equals(row.getIsValid())) {
			return "";
		}
		return row.getOptionValue();
	}

	@Transactional(readOnly = true)
	public WoDomainOption findOptionRow(Long domainId, String optionKey) {
		if (domainId == null || ObjectUtils.isBlank(optionKey)) {
			return null;
		}
		return this.entityRepo.findByDomainIdAndOptionKey(domainId, optionKey);
	}

	public void saveOrDeleteOption(Long domainId, Long comId, SysOptionEnum optionEnum, String optionValue) {
		if (domainId == null || optionEnum == null) {
			return;
		}
		String key = optionEnum.getKey();
		String trimmed = optionValue == null ? "" : optionValue.trim();
		WoDomainOption row = this.entityRepo.findByDomainIdAndOptionKey(domainId, key);
		if (trimmed.isEmpty()) {
			if (row != null && row.getId() != null) {
				this.entityRepo.deleteById(row.getId());
			}
			return;
		}
		if (row == null) {
			row = new WoDomainOption();
			row.setDomainId(domainId);
			row.setOptionKey(key);
			row.setOptionName(optionEnum.getTitle());
			row.setOptionType("auto_reload");
			row.setAppCode("sys_option");
		}
		if (row.getComId() == null && comId != null) {
			row.setComId(comId);
		}
		row.setOptionValue(trimmed);
		this.saveOrUpdate(row);
	}
}
