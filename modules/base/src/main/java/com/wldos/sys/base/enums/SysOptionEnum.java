/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

import com.wldos.common.Constants;

/**
 * 系统配置选项枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum SysOptionEnum {

	/** 新注册用户默认属组：普通会员组，对应普通会员角色。 */
	DEFAULT_GROUP("默认用户组", "default_group"),
	/** 待激活用户组，开启邮箱激活时新注册用户默认组 */
	UN_ACTIVE_GROUP("待激活用户组", "un_active_group"),
	/** 社会化登录微信参数配置 */
	OAUTH_LOGIN_WECHAT("社会化登录微信", Constants.OAUTH_SETTINGS_PREFIX + OAuthTypeEnum.WeChat.getValue()),
	/** 社会化登录QQ参数配置 */
	OAUTH_LOGIN_QQ("社会化登录QQ", Constants.OAUTH_SETTINGS_PREFIX + OAuthTypeEnum.QQ.getValue()),
	/** 社会化登录微博参数配置 */
	OAUTH_LOGIN_WEIBO("社会化登录微博", Constants.OAUTH_SETTINGS_PREFIX + OAuthTypeEnum.WeiBo.getValue()),
	/** other */
	OTHER("其他", "other");

	private final String title;
	private final String key;

	SysOptionEnum(String title, String key) {
		this.title = title;
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return  "{title: '" + this.title + "', key: '" + this.key + "'}";
	}
}
