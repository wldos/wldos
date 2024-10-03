/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.service;

import com.wldos.framework.service.RepoService;
import com.wldos.sys.base.entity.WoArchitecture;
import com.wldos.sys.base.repo.ArchitectureRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 体系结构service。
 *
 * @author 元悉宇宙
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ArchitectureService extends RepoService<ArchitectureRepo, WoArchitecture, Long> {
}
