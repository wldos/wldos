/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.utils.encrypt;

/**
 * 解密异常
 * 
 * @author 元悉宇宙
 * @date 2026/01/10
 */
public class DecryptionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DecryptionException(String message) {
		super(message);
	}

	public DecryptionException(String message, Throwable cause) {
		super(message, cause);
	}

}
