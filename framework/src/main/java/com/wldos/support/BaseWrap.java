/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support;

import com.wldos.base.Base;
import com.wldos.base.controller.IDGen;
import com.wldos.base.entity.GetBeanHelper;
import com.wldos.support.auth.JWTTool;
import com.wldos.support.cache.ICache;
import com.wldos.support.storage.IStore;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 框架基础包装类，用于增强底层实现类，配合CommonJdbcOperate和FreeJdbcTemplate接口可以扩展公共操作，从而在无须改动底层实现类的前提下增强框架能力。
 *
 * @author 树悉猿
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

	public IDGen getSnowflakeID() {
		return IDGen;
	}
}