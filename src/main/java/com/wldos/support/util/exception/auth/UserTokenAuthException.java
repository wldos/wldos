/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util.exception.auth;


import com.wldos.support.util.constant.PubConstants;
import com.wldos.support.util.exception.AuthException;

public class UserTokenAuthException extends AuthException {
    public UserTokenAuthException(String message) {
        super(message, PubConstants.EX_USER_INVALID_CODE);
    }
}
