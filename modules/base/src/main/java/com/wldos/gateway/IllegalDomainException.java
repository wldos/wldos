/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.gateway;

import com.wldos.common.Constants;
import com.wldos.common.exception.BaseException;

/**
 * 非法域名请求异常。
 *
 * @author 树悉猿
 * @date 2021/8/6
 * @version 1.0
 */
public class IllegalDomainException extends BaseException {

	public IllegalDomainException(String message) {
		super(message, Constants.TOKEN_FORBIDDEN_CODE);
	}
}
