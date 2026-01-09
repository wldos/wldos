/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.auth.vo;

import com.wldos.platform.support.resource.vo.AuthInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 资源请求权限检查器。
 *
 * @author 元悉宇宙
 * @date 2021/4/26
 * @version 1.0
 */
@Getter
@Setter
public class AuthVerify {
	private AuthInfo authInfo;

	/**
	 * 是否有权限
	 */
	private boolean auth;
}
