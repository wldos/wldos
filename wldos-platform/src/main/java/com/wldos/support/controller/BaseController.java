/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.support.util.IpUtils;
import com.wldos.support.Constants;
import com.wldos.support.util.domain.DomainUtil;
import com.wldos.system.storage.IStore;
import com.wldos.support.Base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 顶层controller。
 *
 * @author 树悉猿
 * @date 2021/5/5
 * @version 1.0
 */
public abstract class BaseController extends Base{
	@Value("${token.access.header}")
	protected String tokenHeader;

	@Value("${wldos.domain.header}")
	protected String domainHeader;

	@Value("${wldos.context}")
	protected String wldosContext;

	@Autowired
	protected IStore store;

	@Autowired
	protected HttpServletRequest request;

	@Autowired
	protected HttpServletResponse response;

	protected Long getCurUserId() {
		try {
			return Long.parseLong(this.request.getHeader(Constants.CONTEXT_KEY_USER_ID));
		} catch (NumberFormatException e) {
			getLog().info("获取用户id为空：{}", IpUtils.getClientIp(this.request));
			return Constants.GUEST_ID;
		}
	}

	protected String getToken() {
		return this.request.getHeader(this.tokenHeader);
	}

	protected String getUserIp() {
		return IpUtils.getClientIp(this.request);
	}

	protected Long getTenantId() {
		try {
			return Long.parseLong(this.request.getHeader(Constants.CONTEXT_KEY_USER_TENANT));
		} catch (NumberFormatException e) {
			getLog().info("获取主企业id为空：{}", IpUtils.getClientIp(this.request));
			return Constants.TOP_COM_ID;
		}
	}

	protected String getDomain() {
		return DomainUtil.getDomain(this.request, this.domainHeader);
	}

	protected long getTokenExpTime() {
		try {
			return Long.parseLong(this.request.getHeader(Constants.CONTEXT_KEY_TOKEN_EXPIRE_TIME));
		} catch (NumberFormatException e) {
			return 0L;
		}
	}
}
