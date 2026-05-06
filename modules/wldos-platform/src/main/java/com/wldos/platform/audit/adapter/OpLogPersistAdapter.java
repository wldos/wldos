/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.adapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.wldos.framework.common.IDGen;
import io.github.wldos.common.Constants;
import io.github.wldos.common.utils.DateUtils;
import io.github.wldos.framework.support.audit.IOpLogContextResolver;
import io.github.wldos.framework.support.audit.OpEvent;
import com.wldos.framework.support.audit.port.OpLogPersistPort;
import com.wldos.platform.audit.entity.WoOpLog;
import com.wldos.platform.audit.service.OpLogService;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志持久化适配器（端口实现）。
 *
 * <p>异步消费侧的"最后一公里"：从队列拿到一批 {@link OpEvent} 后，
 * <ol>
 *   <li>批量调 {@link IOpLogContextResolver} 解析操作者名 / 资源名（事件发生时刻的快照），
 *       回填到 event；</li>
 *   <li>{@link BeanCopier} 拷到 {@link WoOpLog} 实体；</li>
 *   <li>批量 INSERT 落库。</li>
 * </ol>
 *
 * <p>异步线程做 DB 反查不影响业务请求延迟；批量 IN 查询无 N+1；
 * 名字一旦落库即"冻结"，业务命名后续变化不影响历史日志，满足合规级审计要求。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Slf4j
@Component
public class OpLogPersistAdapter implements OpLogPersistPort {

	/**
	 * 同名同类型字段批量拷贝。{@code durationMs}（{@code long}→{@code Integer}）类型不一致会被自动跳过，
	 * 由 {@link #toEntity(OpEvent)} 手工补齐。
	 */
	private static final BeanCopier COPIER = BeanCopier.create(OpEvent.class, WoOpLog.class, false);

	/** 资源人性化名称超长截断阈值，与 {@code wo_op_log.resource_name} 列宽一致。 */
	private static final int RESOURCE_NAME_MAX_LEN = 120;

	@Autowired
	private OpLogService opLogService;

	/**
	 * 收集所有 {@link IOpLogContextResolver} bean。{@link ObjectProvider} 包一层避免"零模块部署"启动失败。
	 */
	private final ObjectProvider<List<IOpLogContextResolver>> resolversProvider;

	@Autowired
	public OpLogPersistAdapter(ObjectProvider<List<IOpLogContextResolver>> resolversProvider) {
		this.resolversProvider = resolversProvider;
	}

	@Override
	public void saveOne(OpEvent event) {
		if (event == null) return;
		enrichSnapshotNames(Collections.singletonList(event));
		this.opLogService.insertOne(toEntity(event));
	}

	@Override
	public void saveBatch(List<OpEvent> events) {
		if (events == null || events.isEmpty()) return;
		enrichSnapshotNames(events);
		List<WoOpLog> rows = new ArrayList<>(events.size());
		for (OpEvent e : events) {
			if (e == null) continue;
			rows.add(toEntity(e));
		}
		if (rows.isEmpty()) return;
		this.opLogService.insertBatch(rows);
	}

	/* ============================================================
	 *  快照填充：批量调 SPI 解析"事件发生时刻"的人性化名称
	 * ============================================================ */

	/**
	 * 批量回填 {@code userName} 与 {@code resourceName}：
	 * <ul>
	 *   <li>{@code userName} 为空 / null 才查；切面里若已通过自定义渠道填好就尊重；</li>
	 *   <li>{@code resourceName} 同理；切面侧 SpEL 显式给出的也尊重；</li>
	 *   <li>整批 events 按用户 / 资源类型分组，每个分组只触发一次 IN 查询；</li>
	 *   <li>任何 resolver 抛错被吞掉记 warn，不阻塞落库——审计的优先级是"宁可缺名也要落库"。</li>
	 * </ul>
	 */
	private void enrichSnapshotNames(List<OpEvent> events) {
		if (events == null || events.isEmpty()) return;
		List<IOpLogContextResolver> resolvers = this.resolversProvider.getIfAvailable();
		if (resolvers == null || resolvers.isEmpty()) return;

		Set<Long> userIds = new HashSet<>();
		Map<String, Set<String>> resourceIds = new HashMap<>();
		for (OpEvent e : events) {
			if (e == null) continue;
			Long uid = e.getUserId();
			if (uid != null && !Constants.GUEST_ID.equals(uid)
					&& isBlank(e.getUserName())) {
				userIds.add(uid);
			}
			if (isBlank(e.getResourceName())) {
				String type = e.getResourceType();
				String rid = e.getResourceId();
				if (type != null && !type.isEmpty() && rid != null && !rid.isEmpty()) {
					resourceIds.computeIfAbsent(type, k -> new HashSet<>()).add(rid);
				}
			}
		}
		if (userIds.isEmpty() && resourceIds.isEmpty()) return;

		Map<Long, String> userNameMap = new HashMap<>();
		Map<String, Map<String, String>> resourceNameMap = new HashMap<>();
		for (IOpLogContextResolver resolver : resolvers) {
			if (resolver == null) continue;
			try {
				if (!userIds.isEmpty()) {
					Map<Long, String> partial = resolver.resolveUserNames(userIds);
					if (partial != null && !partial.isEmpty()) {
						partial.forEach(userNameMap::putIfAbsent);
					}
				}
				if (!resourceIds.isEmpty()) {
					Map<String, Map<String, String>> partial = resolver.resolveResourceNames(resourceIds);
					if (partial != null && !partial.isEmpty()) {
						partial.forEach((type, idMap) -> {
							if (idMap == null || idMap.isEmpty()) return;
							resourceNameMap
									.computeIfAbsent(type, k -> new HashMap<>())
									.putAll(idMap);
						});
					}
				}
			}
			catch (Exception ex) {
				log.warn("op-log snapshot resolver failed: {} - {}",
						resolver.getClass().getSimpleName(), ex.getMessage());
			}
		}

		for (OpEvent e : events) {
			if (e == null) continue;
			if (isBlank(e.getUserName()) && e.getUserId() != null) {
				String name = userNameMap.get(e.getUserId());
				if (name != null) e.setUserName(name);
			}
			if (isBlank(e.getResourceName()) && e.getResourceType() != null && e.getResourceId() != null) {
				Map<String, String> idMap = resourceNameMap.get(e.getResourceType());
				if (idMap != null) {
					String name = idMap.get(e.getResourceId());
					if (name != null) e.setResourceName(truncate(name, RESOURCE_NAME_MAX_LEN));
				}
			}
		}
	}

	/* ============================================================
	 *  实体映射
	 * ============================================================ */

	private static WoOpLog toEntity(OpEvent e) {
		WoOpLog row = new WoOpLog();
		// 同名同类型字段：userId / virtualUserId / userName / domainId / comId / module / action
		// / resourceType / resourceId / resourceName / requestPath / httpMethod / paramsSummary
		// / resultCode / errorMessage / ip / userAgent / occurAt（Timestamp）
		COPIER.copy(e, row, null);

		Timestamp now = DateUtils.convSQLDate(new Date());
		row.setId(IDGen.nextId());
		// long → Integer 类型不一致，BeanCopier 自动跳过，这里手工补
		row.setDurationMs((int) e.getDurationMs());
		if (row.getOccurAt() == null) {
			row.setOccurAt(now);
		}
		// createdAt 必须显式回填：spring-data-jdbc 会把 null 显式 INSERT 到 NOT NULL 列；同时统一 UTC 时间。
		row.setCreatedAt(now);
		// 兜底再截断一次，防御切面 SpEL 直接返回超长字符串
		if (row.getResourceName() != null && row.getResourceName().length() > RESOURCE_NAME_MAX_LEN) {
			row.setResourceName(truncate(row.getResourceName(), RESOURCE_NAME_MAX_LEN));
		}
		return row;
	}

	/* ============================================================
	 *  小工具
	 * ============================================================ */

	private static boolean isBlank(String s) {
		return s == null || s.isEmpty();
	}

	private static String truncate(String s, int max) {
		if (s == null) return null;
		if (s.length() <= max) return s;
		return s.substring(0, max - 3) + "...";
	}
}
