/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.core.repo;

import com.wldos.common.utils.ObjectUtils;
import com.wldos.sys.core.entity.WoUser;

/**
 * 用户相关jdbc操作实现类。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
public class UserJdbcImpl implements UserJdbc {

	@Override
	public WoUser createUser(String mail, String passwd) {
		if (ObjectUtils.existsBlank(mail, passwd)) {
			throw new RuntimeException("mail、passwd不可以为空……");
		}
		WoUser user = new WoUser();

		return null;
	}
}
