/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.support.util.IpUtils;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.storage.IStore;
import com.wldos.support.Base;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 顶层controller，共享资源。
 *
 * @Title BaseController
 * @Package com.wldos.support.controller
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/5
 * @Version 1.0
 */
@Slf4j
public abstract class BaseController extends Base{
	@Value("${token.access.header}")
	protected String tokenHeader;

	@Value("${wldos.context}")
	protected String wldosContext;

	@Autowired
	protected IStore store;

	/** 如果service层需要request，可以传给service，不要在service直接获取*/
	@Autowired
	protected HttpServletRequest request;

	/** 如果service层需要response，可以传给service，不要在service直接获取 */
	@Autowired
	protected HttpServletResponse response;


	/** 日志对象log */
	protected static Logger getLog() {
		return log;
	}

	/**
	 * 获取请求用户id
	 *
	 * @return
	 */
	protected Long getCurUserId() {
		return Long.parseLong(this.request.getHeader(PubConstants.CONTEXT_KEY_USER_ID)); // 纯请求无状态
	}

	/**
	 * 获取请求用户登录名
	 *
	 * @return
	 */
	protected String getCurUserName() {
		return this.request.getHeader(PubConstants.CONTEXT_KEY_USER_NAME); // 纯请求无状态
	}

	/**
	 * 获取请求用户token
	 *
	 * @return
	 */
	protected String getToken() {
		return this.request.getHeader(this.tokenHeader); // 纯请求无状态
	}

	/**
	 * 获取当前用户IP
	 *
	 * @return
	 */
	protected String getUserIp() {
		return IpUtils.getClientIp(this.request);
	}


	/**
	 * 获取用户主企业id(租户)
	 *
	 * @return
	 */
	protected Long getTenantId() {
		return Long.parseLong(this.request.getHeader(PubConstants.CONTEXT_KEY_USER_TENANT));
	}
}
