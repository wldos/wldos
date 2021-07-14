/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.service;

import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.PageQuery;
import com.wldos.system.entity.WoApp;
import com.wldos.system.repo.AppRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用管理service。
 *
 * @Title AppService
 * @Package com.wldos.system.service
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/28
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppService extends BaseService<AppRepo, WoApp, Long> {

	public PageableResult<WoApp> execQueryForPage(Class<? extends Class> aClass, PageQuery pageQuery, Object[] toArray) {
		return this.execQueryForPage(aClass, pageQuery, toArray);
	}
}
