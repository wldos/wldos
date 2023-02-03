/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.common.utils.http.IpUtils;
import com.wldos.common.Constants;
import com.wldos.common.res.PageQuery;
import com.wldos.common.utils.domain.DomainUtils;
import com.wldos.base.Base;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 顶层controller，共享资源。
 *
 * @author 树悉猿
 * @date 2021/5/5
 * @version 1.0
 */
public abstract class BaseController extends Base{

	/** 如果service层需要request，可以传给service，不要在service直接获取*/
	@Autowired
	protected HttpServletRequest request;

	/** 如果service层需要response，可以传给service，不要在service直接获取 */
	@Autowired
	protected HttpServletResponse response;

	/**
	 * 获取请求用户id
	 */
	protected Long getCurUserId() {
		try {
			return Long.parseLong(this.request.getHeader(Constants.CONTEXT_KEY_USER_ID)); // 纯请求无状态
		} catch (NumberFormatException e) {
			getLog().info("获取用户id为空：{}", IpUtils.getClientIp(this.request));
			return Constants.GUEST_ID;
		}
	}

	/**
	 * 获取请求用户token
	 */
	protected String getToken() {
		return this.request.getHeader(this.tokenHeader); // 纯请求无状态
	}

	/**
	 * 获取当前用户IP
	 */
	protected String getUserIp() {
		return IpUtils.getClientIp(this.request);
	}


	/**
	 * 获取用户主企业id(租户)
	 */
	protected Long getTenantId() {
		try {
			return Long.parseLong(this.request.getHeader(Constants.CONTEXT_KEY_USER_TENANT));
		} catch (NumberFormatException e) {
			getLog().info("获取主企业id为空：{}", IpUtils.getClientIp(this.request));
			return Constants.TOP_COM_ID;
		}
	}

	/**
	 * 获取用户当前访问的域名
	 */
	protected String getDomain() {
		return DomainUtils.getDomain(this.request, this.domainHeader);
	}

	/**
	 * 获取用户当前访问域名的id
	 *
	 * @return 域id
	 */
	protected Long getDomainId() {
		try {
			return Long.parseLong(this.request.getHeader(Constants.CONTEXT_KEY_USER_DOMAIN));
		} catch (NumberFormatException e) {
			getLog().info("获取当前请求域id为空：{}", IpUtils.getClientIp(this.request));
			return 0L;
		}
	}

	/**
	 * 针对分页查询应用域隔离
	 *
	 * @param pageQuery 分页查询参数
	 */
	protected void applyDomainFilter(PageQuery pageQuery) {
		if (this.isMultiDomain) // 静默域隔离
			pageQuery.pushParam(Constants.COMMON_KEY_DOMAIN_COLUMN, this.getDomainId());
	}

	/**
	 * token超期时间
	 *
	 * @return expireTime
	 */
	protected long getTokenExpTime() {
		try {
			return Long.parseLong(this.request.getHeader(Constants.CONTEXT_KEY_TOKEN_EXPIRE_TIME));
		} catch (NumberFormatException e) {
			return 0L;
		}
	}
}