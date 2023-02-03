/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base.controller;

import com.wldos.common.res.ResultJson;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 无实体controller，用于没有数据仓(spring data repository)实现的场景。
 *
 * @author 树悉猿
 * @date 2021/5/5
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public abstract class NoRepoController extends BaseController {
	@Autowired
	protected ResultJson resJson;
}