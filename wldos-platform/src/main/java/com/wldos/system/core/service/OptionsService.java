/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import com.wldos.support.service.BaseService;
import com.wldos.system.core.entity.WoOptions;
import com.wldos.system.core.repo.OptionsRepo;

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
public class OptionsService  extends BaseService<OptionsRepo, WoOptions, Long> {

}
