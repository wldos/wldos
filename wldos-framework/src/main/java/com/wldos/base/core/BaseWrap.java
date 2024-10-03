/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.base.core;

import com.wldos.base.tools.GetBeanHelper;
import com.wldos.support.God;
import com.wldos.support.auth.JWTTool;
import com.wldos.support.cache.ICache;
import com.wldos.support.storage.IStore;
import org.slf4j.Logger;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 元悉宇宙
 * @date 2021/6/14
 * @version 1.0
 */
@Component
public class BaseWrap extends Base implements God {
	private final BaseWrap baseWrap;

	public BaseWrap() {
		this.baseWrap = this;
	}

	public BaseWrap(BaseWrap baseWrap) {
		if (baseWrap == null)
			this.baseWrap = new BaseWrap();
		else
			this.baseWrap = baseWrap;
		this.cache = this.baseWrap.cache;
		this.jwtTool = this.baseWrap.jwtTool;
		this.IDGen = this.baseWrap.IDGen;
		this.isMultiTenancy = this.baseWrap.isMultiTenancy;
		this.isMultiDomain = this.baseWrap.isMultiDomain;
		this.wldosDomain = this.baseWrap.wldosDomain;
		this.tokenHeader = this.baseWrap.tokenHeader;
		this.domainHeader = this.baseWrap.domainHeader;
		this.store = this.baseWrap.store;
		this.beanHelper = this.baseWrap.beanHelper;
		this.stringRedisTemplate = this.baseWrap.stringRedisTemplate;
		this.jdbcAggTemplate = this.baseWrap.jdbcAggTemplate;
		this.namedParamJdbcTemplate = this.baseWrap.namedParamJdbcTemplate;
	}

	public BaseWrap getBaseWrap() {
		return baseWrap;
	}

	public ICache getCache() {
		return cache;
	}

	public JWTTool getJwtTool() {
		return jwtTool;
	}

	public IStore getStore() {
		return store;
	}

	public GetBeanHelper getBeanHelper() {
		return beanHelper;
	}

	public StringRedisTemplate getStringRedisTemplate() {
		return stringRedisTemplate;
	}

	public boolean isMultiTenancy() {
		return isMultiTenancy;
	}

	public boolean isMultiDomain() {
		return isMultiDomain;
	}

	public String getWldosDomain() {
		return wldosDomain;
	}

	public String getTokenHeader() {
		return tokenHeader;
	}

	public String getDomainHeader() {
		return domainHeader;
	}

	public JdbcAggregateTemplate getJdbcAggTemplate() {
		return jdbcAggTemplate;
	}

	public NamedParameterJdbcTemplate getNamedParamJdbcTemplate() {
		return namedParamJdbcTemplate;
	}

	public com.wldos.base.tools.IDGen getSnowflakeID() {
		return IDGen;
	}

	protected Logger getLog() {
		return super.getLog();
	}

	/** 主键生成器，加强雪花算法，分布式唯一 */
	public long nextId() {
		return super.nextId();
	}

	/**
	 * 是否平台侧管理
	 *
	 * @param tenantId 当前用户租户id
	 * @return 是否平台侧管理
	 */
	protected boolean isPlatformAdmin(Long tenantId) {
		return super.isPlatformAdmin(tenantId);
	}

	protected void refreshCache(String... keys) {
		super.refreshCache(keys);
	}

	protected void refreshCacheByPrefix(String keyPrefix) {
		super.refreshCacheByPrefix(keyPrefix);
	}

	/** 刷新权限体系：前缀为auth的所有缓存 */
	protected void refreshAuth() {
		super.refreshAuth();
	}
}