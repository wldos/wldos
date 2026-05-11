/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.model;

/**
 * 可更新参数。
 *
 * @author Yuanxi Universe
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
