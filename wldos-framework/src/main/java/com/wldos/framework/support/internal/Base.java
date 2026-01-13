/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.internal;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.File;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.framework.common.CommonOperation;
import com.wldos.framework.common.GetBeanHelper;
import com.wldos.common.Constants;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.framework.support.auth.JWTTool;
import com.wldos.framework.support.cache.ICache;
import com.wldos.framework.support.hook.WSHook;
import com.wldos.framework.support.storage.IStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 框架基础会话类，组织平台扩展接口，不允许直接继承。
 *
 * @author 元悉宇宙
 * @date 2021/6/14
 * @version 1.0
 */
@RefreshScope
@Component
public class Base {
	/** 缓存变量用，Redis替代方案 */
	@Autowired
	protected ICache cache;

	@Autowired
	protected com.wldos.framework.common.IDGen IDGen;

	/** wldos hook管理器 */
	@Autowired
	protected WSHook wsHook;

	/** jwt全家桶工具 */
	@Autowired
	protected JWTTool jwtTool;

	@Autowired
	protected IStore store;

	@Autowired
	protected GetBeanHelper beanHelper;

	/** Redis单机缓存模板 */
	@Autowired
	protected StringRedisTemplate stringRedisTemplate;

	/** 系统根目录 */
	protected String webRoot;

	/** 平台域名 */
	@Value("${wldos_platform_domain:gitee.com/wldos/wldos}")
	protected String wldosDomain;

	/** 判断当前系统是否多租户模式 */
	@Value("${wldos_system_multitenancy_switch:true}")
	protected boolean isMultiTenancy;

	/** 判断当前系统是否多域(站点)模式 */
	@Value("${wldos_system_multidomain_switch:true}")
	protected boolean isMultiDomain;

	/** 平台url */
	@Value("${wldos.platform.url:}")
	protected String wldosUrl;

	/** 平台前端请求协议，默认http */
	@Value("${wldos_req_protocol:http}")
	protected String reqProtocol;

	@Value("${token.access.header:"+Constants.TOKEN_ACCESS_HEADER+"}")
	protected String tokenHeader;

	@Value("${wldos.domain.header:"+Constants.WLDOS_DOMAIN_HEADER+"}")
	protected String domainHeader;

	/**
	 *针对领域模型实现的jdbc操作模板。与之相对的是经典jdbc模板jdbcTemplate。
	 * 支持实体级的CRUD。
	 */
	@Autowired
	protected JdbcAggregateTemplate jdbcAggTemplate;

	/**
	 * 基于经典jdbc模板包装的命名参数jdbc操作模板。支持对象装配、驼峰和下划线的自动映射，如果实体bean名称或属性名称与数据库不是映射
	 * 关系，可以用{@code @Table @Column}等命名策略注解声明数据库物理定义的表名或列名。
	 * 针对复杂业务场景，构建领域关联的和动态参数交互的动态聚合查询。
	 * 只要getJdbcOperations()能够用，就不要使用getJdbcTemplate()
	 */
	@Autowired
	protected NamedParameterJdbcTemplate namedParamJdbcTemplate;

	protected Logger log;
	
	@PostConstruct
	protected void init() {
		initializeWebRoot();
	}

	/** 获取日志对象 */
	protected Logger getLog() {
		if (log == null) {
			log = LoggerFactory.getLogger(this.getClass());
		}
		return log;
	}

	private void initializeWebRoot() {
		String rootPath = System.getProperty("wldos.platform.root", "");
		if (rootPath != null && new File(rootPath).exists()) {
			this.setWebRoot(rootPath);
		} else {
			this.getLog().warn("Invalid webRoot path: {}", rootPath);
		}
	}

	/** 主键生成器，加强雪花算法，分布式唯一 */
	protected long nextId() {
		return com.wldos.framework.common.IDGen.nextId();
	}

	/**
	 * 获取平台根域名
	 */
	protected String getPlatDomain() {
		return this.wldosDomain;
	}

	/**
	 * 是否平台侧管理
	 *
	 * @param tenantId 当前用户租户id
	 * @return 是否平台侧管理
	 */
	protected boolean isPlatformAdmin(Long tenantId) {
		return tenantId.equals(Constants.TOP_COM_ID);
	}

	protected void refreshCache(String... keys) {
		for (String key : keys) {
			this.cache.remove(key);
			this.getLog().debug("已刷新缓存：{}", key);
		}
	}

	protected void refreshCacheByPrefix(String keyPrefix) {
		this.cache.removeByPrefix(keyPrefix);
		getLog().debug("已刷新缓存，前缀：{}", keyPrefix);
	}

	/** 刷新权限体系：前缀为auth的所有缓存 */
	protected void refreshAuth() {
		this.refreshCacheByPrefix(RedisKeyEnum.WLDOS_AUTH.toString());
	}

	protected boolean isRightUser(Long userId, String key, CommonOperation commonOperate) {
		String value = ObjectUtils.string(this.cache.get(key));
		List<Long> adminIds;
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {

				adminIds = RedisKeyEnum.WLDOS_ADMIN.toString().equals(key) ? commonOperate.listSuperAdmin() : commonOperate.listTrustMan();

				if (ObjectUtils.isBlank(adminIds))
					return false;

				value = om.writeValueAsString(adminIds);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return adminIds.contains(userId);
			}

			adminIds = om.readValue(value, new TypeReference<List<Long>>() {});
			return adminIds.contains(userId);
		}
		catch (JsonProcessingException e) {
			this.getLog().error("json解析异常={} {}", value, e.getMessage());
			return false;
		}
	}

	public String getWebRoot() {
		return webRoot;
	}

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}
}