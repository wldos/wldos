/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support;

import javax.annotation.PostConstruct;

import com.wldos.support.controller.SnowflakeID;
import com.wldos.system.auth.JWTTool;
import com.wldos.support.cache.ICache;
import com.wldos.system.enums.RedisKeyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 基础工具类。
 *
 * @author 树悉猿
 * @date 2021/6/14
 * @version 1.0
 */
@RefreshScope
@Component
public class Base {

	@Autowired
	protected ICache cache;

	@Autowired
	private SnowflakeID snowflakeID;

	@Autowired
	protected JWTTool jwtTool;

	@Autowired
	protected StringRedisTemplate stringRedisTemplate;

	@Value("${wldos.system.multi-tenancy.switch}")
	protected String multiTenancySwitch;

	@Value("${wldos.platform.domain}")
	protected String wldosDomain;

	protected Logger log;

	@PostConstruct
	protected Logger getLog() {
		if (log == null) {
			log = LoggerFactory.getLogger(this.getClass());
		}
		return log;
	}

	protected long nextId() {
		return this.snowflakeID.nextId();
	}

	protected boolean isMultiTenancy() {
		return this.multiTenancySwitch.equals("open");
	}

	protected String getPlatDomain() {
		return this.wldosDomain;
	}

	protected boolean isPlatformAdmin(Long tenantId) {
		return tenantId.equals(Constants.TOP_COM_ID);
	}

	protected void refreshCache(String ...keys) {
		for (String key : keys) {
			this.cache.remove(key);
			getLog().info("已刷新缓存：{}", key);
		}
	}

	protected void refreshCacheByPrefix(String keyPrefix) {
		this.cache.removeByPrefix(keyPrefix);
		getLog().info("已刷新缓存，前缀：{}", keyPrefix);
	}

	protected void refreshAuth() {
		this.refreshCacheByPrefix(RedisKeyEnum.WLDOS_AUTH.toString());
	}

	protected void doAction(){
	}

	protected Object applyFilter() {
		return null;
	}
}
