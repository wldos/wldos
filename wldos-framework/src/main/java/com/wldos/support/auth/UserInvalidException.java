/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.auth;

import com.wldos.common.Constants;
import com.wldos.common.exception.BaseException;

/**
 * 用户无效异常。
 *
 * @author 元悉宇宙
 * @date 2021-03-23
 * @version V1.0
 */
public class UserInvalidException extends BaseException {
	public UserInvalidException(String message) {
		super(message, Constants.EX_USER_PASS_INVALID_CODE);
	}
}