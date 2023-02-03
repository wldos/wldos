/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.model;

/**
 * 可更新参数。
 *
 * @author 树悉猿
 * @date 2022/5/21
 * @version 1.0
 */
public interface ModifyParams {
	/**
	 * 获取旧值
	 *
	 * @return 旧值
	 */
	String getOld();
	/**
	 * 获取新值
	 *
	 * @return 新值
	 */
	String getNew();
}
