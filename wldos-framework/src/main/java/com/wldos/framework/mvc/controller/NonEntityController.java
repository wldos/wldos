/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.wldos.framework.common.CommonOperation;
import io.github.wldos.framework.support.internal.Base;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.ResultJson;

import com.wldos.framework.mvc.service.NonEntityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * 抽象基础 controller，不允许直接继承。
 *
 * <p><b>继承 {@link Base} 的平台能力</b>（均为 {@code protected}，在子类中用 {@code this.xxx} 访问；定义见 SDK 源码或依赖 Sources）：</p>
 * <ul>
 *   <li>存储：{@link io.github.wldos.framework.support.internal.Base#store}{@code （}{@link io.github.wldos.framework.support.storage.IStore}{@code ，可选注入）}，
 *       如 {@code this.store.storeFileWithDigest(...)}、{@code this.store.getPublicUrl(...)}、读流 API 等（详见 {@link io.github.wldos.framework.support.storage.IStore}）。</li>
 *   <li>缓存 / Hook / JWT：{@link io.github.wldos.framework.support.internal.Base#cache}、{@link io.github.wldos.framework.support.internal.Base#wsHook}、{@link io.github.wldos.framework.support.internal.Base#jwtTool}</li>
 *   <li>主键 / Bean 解析：{@link io.github.wldos.framework.support.internal.Base#IDGen}、{@link io.github.wldos.framework.support.internal.Base#beanHelper}</li>
 *   <li>Redis / JDBC：{@link io.github.wldos.framework.support.internal.Base#stringRedisTemplate}、{@link io.github.wldos.framework.support.internal.Base#jdbcAggTemplate}、{@link io.github.wldos.framework.support.internal.Base#namedParamJdbcTemplate}</li>
 *   <li>运行与环境：{@link io.github.wldos.framework.support.internal.Base#webRoot}（{@code @PostConstruct} 初始化）、多租户/多域等配置字段见 {@link Base}</li>
 * </ul>
 *
 * <p><b>本层（Controller）额外注入</b>：{@link #service}、{@link #request}、{@link #response}、{@link #resJson}、{@link #commonOperate}。</p>
 *
 * @author Yuanxi Universe
 * @date 2021/5/5
 * @version 1.0
 * @see Base
 */
public abstract class NonEntityController<S extends NonEntityService> extends Base {

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
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