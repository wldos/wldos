/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.auth.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 在线用户信息。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@Getter
@Setter
public class LoginInfo {
	/** token ID */
	private String id;

	private long userId;

	private String loginName;

	private String domain;

	private String loginIP;

	private String netLocation;

	private String userAgent;

	private String os;

	private long loginTime;

	public LoginInfo(String id, long userId, String loginName, String domain, String loginIP, String netLocation, String userAgent, String os, long loginTime) {
		this.id = id;
		this.userId = userId;
		this.loginName = loginName;
		this.domain = domain;
		this.loginIP = loginIP;
		this.netLocation = netLocation;
		this.userAgent = userAgent;
		this.os = os;
		this.loginTime = loginTime;
	}
}