/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base;

import com.wldos.common.exception.BaseException;

/**
 * jdbc执行异常。
 *
 * @author 树悉猿
 * @date 2021/7/15
 * @since 1.0
 */
public class JdbcToolsException extends BaseException {

	public JdbcToolsException(String s, Exception e) {
		super(s, e);
	}

	@SuppressWarnings("unused")
	public JdbcToolsException(String s) {
		super(s);
	}
}
