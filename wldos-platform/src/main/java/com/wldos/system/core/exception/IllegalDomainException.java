/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.system.core.exception;

import com.wldos.support.Constants;
import com.wldos.system.auth.exception.AuthException;

/**
 * 非法域名请求异常。
 *
 * @author 树悉猿
 * @date 2021/8/6
 * @version 1.0
 */
public class IllegalDomainException extends AuthException {

	public IllegalDomainException(String message) {
		super(message, Constants.TOKEN_FORBIDDEN_CODE);
	}
}
