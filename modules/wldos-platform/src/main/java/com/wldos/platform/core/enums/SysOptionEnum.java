/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

import com.wldos.common.Constants;

/**
 * 系统配置选项枚举。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public enum SysOptionEnum {

	DEFAULT_GROUP("默认用户组", "default_group"),
	UN_ACTIVE_GROUP("待激活用户组", "un_active_group"),
	OAUTH_LOGIN_WECHAT("社会化登录微信", Constants.OAUTH_SETTINGS_PREFIX + OAuthTypeEnum.WeChat.getValue()),
	OAUTH_LOGIN_QQ("社会化登录QQ", Constants.OAUTH_SETTINGS_PREFIX + OAuthTypeEnum.QQ.getValue()),
	OAUTH_LOGIN_WEIBO("社会化登录微博", Constants.OAUTH_SETTINGS_PREFIX + OAuthTypeEnum.WeiBo.getValue()),
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
		return "{title: '" + this.title + "', key: '" + this.key + "'}";
	}
}