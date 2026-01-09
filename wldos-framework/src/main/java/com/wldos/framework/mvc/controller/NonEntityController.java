/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.framework.support.internal.Base;
import com.wldos.framework.common.CommonOperation;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.ResultJson;

import com.wldos.framework.mvc.service.NonEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * 抽象基础controller，不允许直接继承。
 *
 * @author 元悉宇宙
 * @date 2021/5/5
 * @version 1.0
 */
public abstract class NonEntityController<S extends NonEntityService> extends Base {

	@Autowired
	protected S service;

	/** 如果service层需要request，可以传给service，不要在service直接获取*/
	@Autowired
	protected HttpServletRequest request;

	/** 如果service层需要response，可以传给service，不要在service直接获取 */
	@Autowired
	protected HttpServletResponse response;

	@Autowired
	protected ResultJson resJson;

	/**
	 * 通用的jdbc和业务操作
	 */
	@Autowired
	@Lazy
	@SuppressWarnings({ "all" })
	protected CommonOperation commonOperate;

	/**
	 * 获取请求用户id
	 */
	protected Long getUserId() {
		return this.commonOperate.getUserId();
	}

	/**
	 * 获取请求用户token
	 */
	protected String getToken() {
		return this.commonOperate.getToken();
	}

	/**
	 * 获取当前用户IP
	 */
	protected String getUserIp() {
		return this.commonOperate.getUserIp();
	}

	/** 是否多租户模式 */
	public boolean isMultiTenancy() {
		return this.commonOperate.isMultiTenancy();
	}

	/**
	 * 获取用户主企业id(租户)
	 */
	protected Long getTenantId() {
		return this.commonOperate.getTenantId();
	}

	/**
	 * 获取平台根域名
	 */
	protected String getPlatDomain() {
		return this.commonOperate.getPlatDomain();
	}
	/** 是否多站模式 */
	public boolean isMultiDomain() {
		return this.commonOperate.isMultiDomain();
	}
	/**
	 * 获取用户当前访问的域名
	 */
	protected String getDomain() {
		return this.commonOperate.getDomain();
	}

	/**
	 * 获取用户当前访问域名的id
	 *
	 * @return 域id
	 */
	protected Long getDomainId() {
		return this.commonOperate.getDomainId();
	}

	/**
	 * 针对分页查询应用域隔离
	 *
	 * @param pageQuery 分页查询参数
	 */
	protected void applyDomainFilter(PageQuery pageQuery) {
		this.commonOperate.applyDomainFilter(pageQuery);
	}

	/**
	 * 实体表分页查询应用租户隔离
	 *
	 * @param pageQuery 分页查询
	 */
	protected void applyTenantFilter(PageQuery pageQuery) {
		this.commonOperate.applyTenantFilter(pageQuery);
	}

	/**
	 * token超期时间
	 *
	 * @return expireTime
	 */
	protected long getTokenExpTime() {
		return this.commonOperate.getTokenExpTime();
	}

	/**
	 * 安全起见，实时查询当前用户是否超级管理员
	 *
	 * @param userId 用户id
	 * @return 是否管理员
	 */
	public boolean isAdmin(Long userId) {
		return this.commonOperate.isAdmin(userId);
	}

	/**
	 * 实时查询当前用户是否可信者
	 *
	 * @param userId 用户id
	 * @return 是否可信者
	 */
	public boolean isCanTrust(Long userId) {
		return this.commonOperate.isCanTrust(userId);
	}
}