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
 * 资源分类未找到异常。
 *
 * @author 树悉猿
 * @date 2021/8/6
 * @version 1.0
 */
public class ResourceTermTypeNoFoundException extends RuntimeException {

	public ResourceTermTypeNoFoundException(String message) {
		super(message);
	}
}
