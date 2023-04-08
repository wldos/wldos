/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.common.Constants;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.auth.JWTTool;
import com.wldos.support.cache.ICache;
import com.wldos.support.storage.IStore;
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
 * 框架基础会话类。
 *
 * @author 树悉猿
 * @date 2021/6/14
 * @version 1.0
 */
@RefreshScope
@Component
class Base {
	/** 缓存变量用，Redis替代方案 */
	@Autowired
	protected ICache cache;

	@Autowired
	protected IDGen IDGen;

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

	/** 判断当前系统是否多租户模式 */
	@Value("${wldos_system_multitenancy_switch:true}")
	protected boolean isMultiTenancy;

	/** 判断当前系统是否多域(站点)模式 */
	@Value("${wldos_system_multidomain_switch:true}")
	protected boolean isMultiDomain;

	/** 平台域名 */
	@Value("${wldos_platform_domain:wldos.com}")
	protected String wldosDomain;

	/** 平台url */
	@Value("${wldos.platform.url:https://www.wldos.com}")
	protected String wldosUrl;

	/** 平台前端请求协议 */
	@Value("${wldos_req_protocol:https}")
	protected String reqProtocol;

	@Value("${token.access.header:X-CU-AccessToken-Default}")
	protected String tokenHeader;

	@Value("${wldos.domain.header:domain-default}")
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

	/** 日志对象log */
	@PostConstruct
	protected Logger getLog() {
		if (log == null) {
			log = LoggerFactory.getLogger(this.getClass());
		}
		return log;
	}

	/** 主键生成器，加强雪花算法，分布式唯一 */
	protected long nextId() {
		return this.IDGen.nextId();
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
			getLog().info("已刷新缓存：{}", key);
		}
	}

	protected void refreshCacheByPrefix(String keyPrefix) {
		this.cache.removeByPrefix(keyPrefix);
		getLog().info("已刷新缓存，前缀：{}", keyPrefix);
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

	// 定义application全局变量，存储hook
	protected void doAction() {
		// @todo 添加一过性hook函数的钩子埋点函数，多用于写操作校验
	}

	protected Object applyFilter() {
		// @todo 应用过滤钩子函数的埋点函数，多用于加工输出
		return null;
	}
}