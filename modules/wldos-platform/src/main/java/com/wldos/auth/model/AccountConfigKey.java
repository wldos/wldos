/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.auth.model;

/**
 * 账户相关配置选项key。
 *
 * @author 树悉猿
 * @date 2021/9/23
 * @version 1.0
 */
public final class AccountConfigKey {
	/** 用户群组 */
	public static final String USER_KEY_GROUP = "group";

	/** 用户标签 */
	public static final String USER_KEY_TAGS = "tags";

	/** 密码强度 */
	public static final String SECURITY_KEY_PASS_STATUS = "passStatus";

	/** 密保手机 */
	public static final String SECURITY_KEY_MOBILE = "mobile";

	/** 密保问题 */
	public static final String SECURITY_KEY_SEC_QUEST = "secQuest";

	/** 备用邮箱 */
	public static final String SECURITY_KEY_BAK_EMAIL = "bakEmail";

	/** mfa设备 */
	public static final String SECURITY_KEY_MFA = "mfa";

	/** 淘宝账号 */
	public static final String BIND_KEY_TB = "tb";

	/** 支付宝账号 */
	public static final String BIND_KEY_ZFB = "zfb";

	/** 钉钉账号 */
	public static final String BIND_KEY_DD = "dd";

	/** 微信账号 */
	public static final String BIND_KEY_WX = "wx";

	/** 抖音账号 */
	public static final String BIND_KEY_DY = "dy";

	/** QQ账号 */
	public static final String BIND_KEY_QQ = "qq";

	/** 微博账号 */
	public static final String BIND_KEY_WB = "wb";

	/** 用户消息设置 */
	public static final String MES_KEY_USER_MES_STATUS = "userMesStatus";

	/** 系统消息设置 */
	public static final String MES_KEY_SYS_MES_STATUS = "sysMesStatus";

	/** 待办消息设置 */
	public static final String MES_KEY_TODO_MES_STATUS = "todoMesStatus";
}
