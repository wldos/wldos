/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth2.service;

import com.wldos.auth2.entity.WoOauthLoginUser;
import com.wldos.auth2.model.OAuthUser;
import com.wldos.auth2.repo.OAuthLoginUserRepo;
import com.wldos.base.entity.EntityAssists;
import com.wldos.base.service.BaseService;
import com.wldos.sys.core.entity.WoUser;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 社会化登录service。
 *
 * @author 树悉猿
 * @date 2022/10/24
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OAuthLoginUserService extends BaseService<OAuthLoginUserRepo, WoOauthLoginUser, Long> {
	private final BeanCopier oAuthUserCopier = BeanCopier.create(OAuthUser.class, WoOauthLoginUser.class, false);

	public WoOauthLoginUser findByUnionId(String openId, String unionId) {
		// 查询是否已存在授权用户
		return this.entityRepo.queryByOpenIdAndUnionId(openId, unionId);
	}

	public WoOauthLoginUser createOAuthLoginUser(WoUser woUser, OAuthUser oAuthUser) {
		WoOauthLoginUser oauthLoginUser = new WoOauthLoginUser();
		oAuthUserCopier.copy(oAuthUser, oauthLoginUser, null);
		oauthLoginUser.setUserId(woUser.getId());
		EntityAssists.beforeInsert(oauthLoginUser, this.nextId(), woUser.getId(), woUser.getCreateIp(), true);
		this.save(oauthLoginUser);

		return oauthLoginUser;
	}
}
