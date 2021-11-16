/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.support.cache.ICache;
import com.wldos.support.enums.DeleteFlagEnum;
import com.wldos.support.enums.ValidStatusEnum;
import com.wldos.support.util.ObjectUtil;
import com.wldos.system.core.entity.WoApp;
import com.wldos.system.core.entity.WoOptions;
import com.wldos.system.core.repo.AppRepo;
import com.wldos.system.core.repo.OptionsRepo;
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
		if (ObjectUtil.isBlank(apps))
			return new ArrayList<>();
		List<String> appTypes =  apps.parallelStream().map(WoApp::getAppCode).collect(Collectors.toList());
		List<WoOptions> options = this.optionsRepo.findAllByAppTypeIn(appTypes);
		log.info("加载数据库系统{}, 配置文件: {} ", appTypes, options);
		return options;
	}
}
