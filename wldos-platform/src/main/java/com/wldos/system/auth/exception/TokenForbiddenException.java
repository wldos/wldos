/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth.exception;

import com.wldos.support.Constants;

public class TokenForbiddenException extends AuthException {
    public TokenForbiddenException(String message) {
        super(message, Constants.TOKEN_FORBIDDEN_CODE);
    }
}
