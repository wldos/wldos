/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.auth2.repo;

import com.wldos.auth2.entity.WoOauthLoginUser;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 社会化登录用户仓库操作类。
 *
 * @author 树悉猿
 * @date 2022/10/24
 * @version 1.0
 */
public interface OAuthLoginUserRepo extends PagingAndSortingRepository<WoOauthLoginUser, Long> {

	WoOauthLoginUser queryByOpenIdAndUnionId(@Param("openId") String openId, @Param("unionId") String unionId);
}
