/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.auth;

import com.wldos.common.Constants;
import com.wldos.common.exception.BaseException;

/**
 * 用户无效异常。
 *
 * @author 树悉猿
 * @date 2021-03-23
 * @version V1.0
 */
public class UserInvalidException extends BaseException {
    public UserInvalidException(String message) {
        super(message, Constants.EX_USER_PASS_INVALID_CODE);
    }
}