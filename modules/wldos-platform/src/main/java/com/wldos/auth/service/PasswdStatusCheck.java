/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.auth.service;

import com.wldos.auth.enums.PasswdStatusEnum;

/**
 * 密码强度检测。
 *
 * @author 元悉宇宙
 * @date 2021/10/18
 * @version 1.0
 */
public class PasswdStatusCheck {
	public static PasswdStatusEnum check(String passwd) {
		if (passwd.length() < 8)
			return PasswdStatusEnum.POOR;

		if (passwd.matches("\\d*"))
			return PasswdStatusEnum.POOR;

		if (passwd.matches("[a-zA-z]+"))
			return PasswdStatusEnum.POOR;

		if (passwd.matches("\\W+$"))
			return PasswdStatusEnum.POOR;

		if (passwd.matches("\\D*"))
			return PasswdStatusEnum.MEDIUM;

		if (passwd.matches("[\\d\\W]*"))
			return PasswdStatusEnum.MEDIUM;

		if (passwd.matches("\\w*"))
			return PasswdStatusEnum.MEDIUM;

		return PasswdStatusEnum.STRONG;
	}
}
