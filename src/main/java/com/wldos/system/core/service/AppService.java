/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.PageQuery;
import com.wldos.system.core.entity.WoApp;
import com.wldos.system.core.repo.AppRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用管理service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppService extends BaseService<AppRepo, WoApp, Long> {

	public PageableResult<WoApp> execQueryForPage(Class<? extends Class> aClass, PageQuery pageQuery, Object[] toArray) {
		return this.execQueryForPage(aClass, pageQuery, toArray);
	}
}
