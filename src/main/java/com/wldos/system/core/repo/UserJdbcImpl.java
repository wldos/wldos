/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import com.wldos.support.repo.FreeJdbcTemplate;
import com.wldos.support.util.ObjectUtil;
import com.wldos.system.core.entity.WoUser;

/**
 * 用户相关jdbc操作实现类。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
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
