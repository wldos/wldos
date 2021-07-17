/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.cache;

import java.util.concurrent.TimeUnit;

/**
 * 定制缓存接口，缓存验证码等运行时交互数据，本缓存的实现目的是为了解决认证、权限相关的资源缓存问题，这些资源要么属于共享比如系统菜单，要么属于全局唯一比如验证码和token。
 * 另外最重要的是：本缓存仅供登录认证、授权和请求鉴权等认证中心使用，这些模块本框架默认属于认证中心，在分布式环境中他们可以组成公共的SDK，以代理(Proxy)的方式纳入到每个微服务（ServiceMesh架构）或者网关服务中（中心化架构）。
 * 这些缓存资源的特点是：需要保存至少2次请求的生命周期(比如验证码的取码与验码是两次独立请求)。
 * 为什么不用redis，因为Redis的作用不在于缓存，如果你想使用Redis，重写这个接口的实现就是了。
 * 实现理念：一切以实际业务需求和系统的整体架构为依据，我们只实现1，如果你需要10，那么继续造9个就是了！
 *
 * @author 树悉猿
 * @date 2021/5/7
 * @version 1.0
 */
public interface ICache {
	/**
	 * 设置缓存
	 *
	 * @param key 缓存key
	 * @param value 缓存对象
	 * @param sourceDuration 缓存时长，超过将删除
	 * @param sourceUnit 缓存时长时间单位，目前支持分钟、毫秒、小时，建议分钟
	 */
	void set(String key, Object value, long sourceDuration, TimeUnit sourceUnit);

	Object get(String key);

	void remove(String key);

	long size();

	void clear();
}
