/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.auth;

import io.github.wldos.common.exception.BaseException;

/**
 * 已颁发有效 token 但账号尚未完成激活（如邮箱激活）时，访问需正式会员权限的资源抛出此异常。
 * <p>定义在框架扩展包而非 SDK，便于项目侧频繁调整文案/码值而无需升级中央仓库构件。</p>
 */
public class AccountNotActivatedException extends BaseException {

	/** 与前端 {@code request} 内约定一致 */
	public static final int CODE = 460;

	public AccountNotActivatedException(String message) {
		super(message, CODE);
	}
}
