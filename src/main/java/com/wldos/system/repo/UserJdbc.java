/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import com.wldos.system.entity.WoUser;

/**
 * 用户相关jdbc操作。
 *
 * @Title UserJdbc
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/2
 * @Version 1.0
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
