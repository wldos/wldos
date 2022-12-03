/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.service;

import com.wldos.base.service.BaseService;
import com.wldos.sys.base.entity.WoArchitecture;
import com.wldos.sys.base.repo.ArchitectureRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 体系结构service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ArchitectureService extends BaseService<ArchitectureRepo, WoArchitecture, Long> {
}
