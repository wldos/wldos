/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.controller;

import com.wldos.support.controller.web.ResultJson;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 无实体controller。
 *
 * @author 树悉猿
 * @date 2021/5/5
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public abstract class NoRepoController extends BaseController{
	@Autowired
	protected ResultJson resJson;
}
