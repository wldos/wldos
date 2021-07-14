/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import com.wldos.support.repo.FreeJdbcTemplate;
import com.wldos.support.util.ObjectUtil;
import com.wldos.system.entity.WoUser;

/**
 * 用户相关jdbc操作实现类。
 *
 * @Title UserJdbcImpl
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/2
 * @Version 1.0
 */
public class UserJdbcImpl extends FreeJdbcTemplate implements UserJdbc{

	@Override
	public WoUser createUser(String mail, String passwd) {
		if (ObjectUtil.existsBlank(mail, passwd)) {
			throw new RuntimeException("mail、passwd不可以为空……");
		}
		WoUser user = new WoUser();

		return null;
	}
}
