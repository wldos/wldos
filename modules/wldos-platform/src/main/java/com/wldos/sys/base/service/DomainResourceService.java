/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.service;

import com.wldos.framework.service.RepoService;
import com.wldos.sys.base.entity.WoDomainResource;
import com.wldos.sys.base.repo.DomainResourceRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域资源管理service。
 *
 * @author 元悉宇宙
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DomainResourceService extends RepoService<DomainResourceRepo, WoDomainResource, Long> {

}
