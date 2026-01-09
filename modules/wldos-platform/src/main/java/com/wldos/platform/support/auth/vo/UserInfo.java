/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.auth.vo;

import com.wldos.framework.support.auth.vo.JWT;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录成功返回的用户基础信息。
 *
 * @author 元悉宇宙
 * @date 2021/4/30
 * @version 1.0
 */
@Getter
@Setter
public class UserInfo {
	private Long id;

	private String username;

	private String nickname;

	private String status;

	private String remark;

	private String avatar;

	private Long tenantId;

	private Long domainId;

	public UserInfo() {
	}

	public UserInfo(JWT jwt) {
		this.id = jwt.getUserId();
		this.tenantId = jwt.getTenantId();
		this.domainId = jwt.getDomainId();
	}
}