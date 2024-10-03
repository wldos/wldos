/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.core.repo;

import com.wldos.sys.core.entity.WoUser;

/**
 * 用户相关jdbc操作。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
public interface UserJdbc {
	/**
	 * 利用注册邮箱作为账号和密码创建一个平台普通用户。注册一个用户就是创建用户，然后挂到平台注册用户默认归属组(普通会员组)上，平台会员组
	 * 属于系统基础设施已经默认设置好
	 *
	 * @param mail
	 * @param passwd
	 * @return
	 */
	WoUser createUser(String mail, String passwd);
}
