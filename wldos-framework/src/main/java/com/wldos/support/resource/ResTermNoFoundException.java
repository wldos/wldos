/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.resource;

/**
 * 资源分类未找到异常。
 *
 * @author 元悉宇宙
 * @date 2021/8/6
 * @version 1.0
 */
public class ResTermNoFoundException extends RuntimeException {

	public ResTermNoFoundException(String message) {
		super(message);
	}
}
