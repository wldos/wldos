/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.config;

import java.util.List;
import java.util.stream.Collectors;

import com.wldos.support.util.ObjectUtil;
import com.wldos.system.sysenum.DeleteFlagEnum;
import com.wldos.system.sysenum.ValidStatusEnum;
import com.wldos.system.entity.WoApp;
import com.wldos.system.repo.AppRepo;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * 系统配置项service。
 *
 * @Title OptionsService
 * @Package com.wldos.support.config
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/7/13
 * @Version 1.0
 */
@Service
@Slf4j
public class OptionsService {

	private final AppRepo appRepo;

	private final OptionsRepo optionsRepo;

	public OptionsService(AppRepo appRepo, OptionsRepo optionsRepo) {
		this.appRepo = appRepo;
		this.optionsRepo = optionsRepo;
	}

	public List<WoOptions> getAllByAppType() {
		List<WoApp> apps = this.appRepo.findAllByDeleteFlagEqualsAndIsValidEquals(DeleteFlagEnum.normal.toString(), ValidStatusEnum.valid.toString());
		if (ObjectUtil.isBlank(apps))
			return null;
		List<String> appTypes =  apps.parallelStream().map(app -> app.getAppCode()).collect(Collectors.toList());
		List<WoOptions> options = this.optionsRepo.findAllByAppTypeIn(appTypes);
		log.info("加载数据库系统{},配置文件: {} ", appTypes, options);
		return options;
	}
}
