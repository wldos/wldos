/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.framework.repo.BaseRepo;
import com.wldos.support.system.entity.WoOptions;

import org.springframework.data.repository.query.Param;

/**
 * 系统配置项仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/7/13
 * @version 1.0
 */
public interface OptionsRepo extends BaseRepo<WoOptions, Long> {

	List<WoOptions> findAllByOptionTypeIn(@Param("optionType") List<String> optionType);

	List<WoOptions> findAllByOptionType(@Param("optionType") String optionType);

	List<WoOptions> findAllByOptionKey(@Param("optionKey") String optionKey);

	WoOptions findByOptionKey(@Param("optionKey") String optionKey);

	List<WoOptions> findAllByAppCode(@Param("appCode") String appCode);
}
