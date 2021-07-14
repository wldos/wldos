/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.controller;

import com.wldos.support.controller.web.ResultJson;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 无实体controller，用于没有数据仓(spring data repository)实现的场景。
 *
 * @Title NoRepoController
 * @Package com.wldos.support.controller
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/5
 * @Version 1.0
 */
public abstract class NoRepoController extends BaseController{
	@Autowired
	protected ResultJson resJson;
	protected Logger log = this.getLog();
}
