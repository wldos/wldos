/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.resource;

/**
 * 资源分类未找到异常。
 *
 * @author 树悉猿
 * @date 2021/8/6
 * @version 1.0
 */
public class ResTermNoFoundException extends RuntimeException {

	public ResTermNoFoundException(String message) {
		super(message);
	}
}
