/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.repo;

import com.wldos.sys.core.entity.WoUser;

/**
 * 用户相关jdbc操作。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
public interface UserJdbc {

	WoUser createUser(String mail, String passwd);
}
