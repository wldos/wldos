/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.auth;

import com.wldos.common.Constants;
import com.wldos.common.exception.BaseException;

/**
 * token非法异常。
 *
 * @author 元悉宇宙
 * @date 2021-03-23
 * @version V1.0
 */
public class TokenForbiddenException extends BaseException {
	public TokenForbiddenException(String message) {
		super(message, Constants.TOKEN_FORBIDDEN_CODE);
	}
}