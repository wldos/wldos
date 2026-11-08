/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 判断请求路径是否对应系统中真实注册的 Controller 端点，供 edge 分类识别"扫描/探测不存在路径"与
 * "攻击真实接口"两类场景。启动时构建只读路由索引；运行期对判定过的 URI 做 memo（存在路径长期保留，
 * 不存在路径受容量限制），不把索引刷新与插件生命周期绑定。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Component
public class EdgeRouteExistenceChecker {

	private final ApplicationContext applicationContext;

	private final Environment environment;

	private volatile EdgeRoutePatternIndex index;

	private final ConcurrentHashMap<String, Boolean> existsUriMemo = new ConcurrentHashMap<>(512);

	private final ConcurrentHashMap<String, Boolean> absentUriMemo = new ConcurrentHashMap<>(512);

	public EdgeRouteExistenceChecker(ApplicationContext applicationContext, Environment environment) {
		this.applicationContext = applicationContext;
		this.environment = environment;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		rebuildIndex();
	}

	/**
	 * 全量重建路由索引并清空 URI memo（与插件解耦；映射变更后需重启或主动调用本方法）。
	 */
	public synchronized void rebuildIndex() {
		EdgeRoutePatternIndex built = EdgeRoutePatternIndex.build(applicationContext);
		this.index = built;
		this.existsUriMemo.clear();
		this.absentUriMemo.clear();
		log.info("[edge.route-check] index built: patterns={}, exact={}, buckets={}, fallback={}",
				built.patternCount, built.exactCount, built.bucketCount, built.fallbackCount);
	}

	/**
	 * 路径是否命中系统中任一已注册 Controller 端点（不区分请求方法；方法不匹配也算"存在"，
	 * 因为这仍是应用真实拥有的资源，不是扫描/探测）。
	 */
	public boolean exists(String uri) {
		if (uri == null || uri.isEmpty()) {
			return false;
		}
		Boolean hit = existsUriMemo.get(uri);
		if (hit != null) {
			return hit.booleanValue();
		}
		Boolean absent = absentUriMemo.get(uri);
		if (absent != null) {
			return false;
		}
		EdgeRoutePatternIndex snapshot = this.index;
		if (snapshot == null) {
			return false;
		}
		boolean exists = snapshot.matches(uri);
		if (exists) {
			existsUriMemo.put(uri, Boolean.TRUE);
		}
		else if (absentUriMemo.size() < maxAbsentMemoEntries()) {
			absentUriMemo.put(uri, Boolean.FALSE);
		}
		else {
			absentUriMemo.clear();
			absentUriMemo.put(uri, Boolean.FALSE);
		}
		return exists;
	}

	private int maxAbsentMemoEntries() {
		int v = environment.getProperty("wldos.gateway.route-check.max-cache-entries", Integer.class, 10_000);
		return v > 0 ? v : 10_000;
	}
}
