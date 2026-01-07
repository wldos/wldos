/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.common.utils.ObjectUtils;
import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.AppDao;
import com.wldos.platform.core.dao.OptionsDao;
import com.wldos.platform.support.system.OptionsOpener;
import com.wldos.platform.support.system.entity.WoOptions;
import com.wldos.platform.support.system.enums.OptionTypeEnum;
import com.wldos.framework.common.SaveOptions;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * 系统配置初始化service。
 *
 * @author 元悉宇宙
 * @date 2021/7/13
 * @since 1.0
 */
@Slf4j
@Service
public class OptionsNoRepoService extends NonEntityService implements OptionsOpener {

	private final AppDao appRepo;

	private final OptionsDao optionsRepo;

	public OptionsNoRepoService(AppDao appRepo, OptionsDao optionsRepo) {
		this.appRepo = appRepo;
		this.optionsRepo = optionsRepo;
	}

	public List<WoOptions> getAllSysOptionsByOptionType(String optionType) {
		return this.optionsRepo.findAllByOptionType(optionType);
	}

	private final String APP_CODE_SYSTEM = "sys_option";

	public List<WoOptions> getSystemOptions() {
		return this.optionsRepo.findAllByAppCode(this.APP_CODE_SYSTEM);
	}

	public Map<String, String> configSysOptions(Map<String, Object> config) {
		List<WoOptions> insertOptions = new ArrayList<>();
		List<WoOptions> updateOptions =
		config.entrySet().stream().map(c -> {
			WoOptions option = this.optionsRepo.findByOptionKey(c.getKey());
			if (option == null) {
				insertOptions.add(WoOptions.of(this.nextId(), c.getKey(), ObjectUtils.string(c.getValue()), OptionTypeEnum.AUTO_RELOAD.getValue(), this.APP_CODE_SYSTEM));
				return null;
			}
			option.setOptionValue(ObjectUtils.string(c.getValue()));
			return option;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (!updateOptions.isEmpty()) {
			// 1.存在则更新，2.不存在则创建
			this.commonOperate.dynamicBatchUpdateByEntities(updateOptions, false);
		}

		if (!insertOptions.isEmpty()) {
			for (WoOptions option : insertOptions) {
				this.saveOtherEntity(option, SaveOptions.forImport());
			}
		}

		return this.optionsRepo.findAllByAppCode(this.APP_CODE_SYSTEM).stream().collect(Collectors.toMap(WoOptions::getOptionKey, WoOptions::getOptionValue, (k1, k2) -> k1));
	}
}