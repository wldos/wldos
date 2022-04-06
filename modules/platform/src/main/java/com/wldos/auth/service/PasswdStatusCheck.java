/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.service;

import com.wldos.auth.enums.PasswdStatusEnum;

/**
 * 密码强度检测。
 *
 * @author 树悉猿
 * @date 2021/10/18
 * @version 1.0
 */
public class PasswdStatusCheck {
	public static String check(String passwd) {
		if (passwd.length() <= 5)
			return PasswdStatusEnum.POOR.toString();

		if (passwd.length() <= 8)
			return PasswdStatusEnum.MEDIUM.toString();

		if (passwd.matches("\\d*"))
			return PasswdStatusEnum.POOR.toString();

		if (passwd.matches("[a-zA-z]+"))
			return PasswdStatusEnum.POOR.toString();

		if (passwd.matches("\\W+$"))
			return PasswdStatusEnum.POOR.toString();

		if (passwd.matches("\\D*"))
			return PasswdStatusEnum.MEDIUM.toString();

		if (passwd.matches("[\\d\\W]*"))
			return PasswdStatusEnum.MEDIUM.toString();

		if (passwd.matches("\\w*"))
			return PasswdStatusEnum.MEDIUM.toString();

		return PasswdStatusEnum.STRONG.toString();
	}
}
