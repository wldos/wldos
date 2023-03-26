/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.cache.ICache;
import com.wldos.sys.base.entity.WoApp;
import com.wldos.sys.base.entity.WoOptions;
import com.wldos.sys.base.repo.AppRepo;
import com.wldos.sys.base.repo.OptionsRepo;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * 系统配置初始化service。
 *
 * @author 树悉猿
 * @date 2021/7/13
 * @since 1.0
 */
@Slf4j
@Service
public class OptionsNoRepoService {

	private ICache cache;

	private final AppRepo appRepo;

	private final OptionsRepo optionsRepo;


	public OptionsNoRepoService(ICache cache, AppRepo appRepo, OptionsRepo optionsRepo) {
		this.cache = cache;
		this.appRepo = appRepo;
		this.optionsRepo = optionsRepo;
	}

	public List<WoOptions> getAllByAppType() {
		List<WoApp> apps = this.appRepo.findAllByDeleteFlagEqualsAndIsValidEquals(DeleteFlagEnum.NORMAL.toString(), ValidStatusEnum.VALID.toString());
		if (ObjectUtils.isBlank(apps))
			return new ArrayList<>();
		List<String> appTypes = apps.parallelStream().map(WoApp::getAppCode).collect(Collectors.toList());
		return this.optionsRepo.findAllByAppTypeIn(appTypes);
	}
}