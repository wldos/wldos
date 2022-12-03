/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.service;

import com.wldos.base.service.BaseService;
import com.wldos.sys.base.entity.WoDomainResource;
import com.wldos.sys.base.repo.DomainResourceRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域资源管理service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DomainResourceService extends BaseService<DomainResourceRepo, WoDomainResource, Long> {

}
