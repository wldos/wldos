/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.base.service;

import java.util.List;

import com.wldos.base.RepoService;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.system.entity.WoOptions;
import com.wldos.sys.base.repo.OptionsRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统配置项service。
 *
 * @author 树悉猿
 * @date 2021/7/13
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OptionsService extends RepoService<OptionsRepo, WoOptions, Long> {

	/**
	 * 根据配置key获取配置属性
	 *
	 * @param key 配置项键值
	 * @return 配置属性
	 */
	public String findSettingsByKey(String key) {
		// 配置应进缓存 todo 根据key值设置 修改此配置项时删掉对应key的缓存
		List<WoOptions> optionsList = this.entityRepo.findAllByOptionKey(key);
		if (ObjectUtils.isBlank(optionsList))
			return "";
		return optionsList.get(0).getOptionValue();
	}

	/**
	 * 根据key获取系统选项
	 *
	 * @param key 选项key
	 * @return 选项
	 */
	public WoOptions findByKey(String key) {
		List<WoOptions> options = this.entityRepo.findAllByOptionKey(key);
		return ObjectUtils.isBlank(options) ? null : options.get(0);
	}
}
