/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support;

import com.wldos.support.controller.SnowflakeID;
import com.wldos.system.auth.JWTTool;
import com.wldos.system.storage.ICache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 基础工具类。
 *
 * @Title Base
 * @Package com.wldos.support
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/6/14
 * @Version 1.0
 */
@RefreshScope
@Component
public class Base {
	/** 缓存变量用，Redis替代方案 */
	@Autowired
	protected ICache cache;

	@Autowired
	private SnowflakeID snowflakeID;

	/** 主键生成器，加强雪花算法，分布式唯一 */
	protected long nextId() {
		return this.snowflakeID.nextId();
	}

	/** jwt全家桶工具 */
	@Autowired
	protected JWTTool jwtTool;

	/** Redis单机缓存模板 */
	@Autowired
	protected StringRedisTemplate stringRedisTemplate;

	@Value("${wldos.system.multi-tenancy.switch}")
	protected String multiTenancySwitch;

	/**
	 * 判断当前系统是否多租户模式
	 *
	 * @return
	 */
	protected boolean isMultiTenancy() {
		return this.multiTenancySwitch.equals("open");
	}

	// 定义application全局变量，存储hook
	protected void doAction(){
		// @todo 添加一过性hook函数的钩子埋点函数，多用于写操作校验
	}

	protected Object applyFilter() {
		// @todo 应用过滤钩子函数的埋点函数，多用于加工输出
		return null;
	}
}
