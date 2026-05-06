/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.wldos.common.Constants;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.framework.support.audit.IOpLogContextResolver;
import io.github.wldos.framework.support.audit.ResourceTypeDict;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.audit.dao.OpLogDao;
import com.wldos.platform.audit.entity.WoOpLog;
import com.wldos.platform.audit.vo.OpLogItem;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志 Service。
 *
 * <p>职责与 {@link LoginLogService} 同构：写入（异步消费侧）+ 管理端列表查询。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Slf4j
@Service
public class OpLogService extends EntityService<OpLogDao, WoOpLog, Long> {

	/**
	 * 实体 → VO 同名同类型字段批量拷贝。
	 *
	 * <p>{@link WoOpLog#getId()} 为 {@code Long} 而 {@link OpLogItem#getId()} 为 {@code String}，
	 * 类型不一致会被 {@link BeanCopier} 静默跳过，由 {@link #toItem(WoOpLog)} 手工补；
	 * {@code resourceName} 仅存在于 VO，源端无对应 getter，本就不会被拷贝，
	 * 由 {@link #enrichDisplayNames(List)} 反查回填。
	 */
	private static final BeanCopier ENTITY_TO_VO = BeanCopier.create(WoOpLog.class, OpLogItem.class, false);

	/**
	 * 收集所有 {@link IOpLogContextResolver} bean。
	 *
	 * <p>用 {@link ObjectProvider}<{@link List}<...>> 而非直接 {@code List} 注入：
	 * 即使没有任何模块实现该接口（例如裁剪部署只用 audit 内核而不开任何业务模块），
	 * 注入也不会失败，{@code orderedStream()} 返回空。
	 */
	private final ObjectProvider<List<IOpLogContextResolver>> resolversProvider;

	/**
	 * 资源类型 → 中文 label 总字典缓存。
	 *
	 * <p>启动时由 {@link #initResourceTypeLabels()} 从所有 {@link IOpLogContextResolver#resourceTypeLabels()}
	 * 聚合一次；之后查询页 {@link #toItem(WoOpLog)} 直接 {@code O(1)} 命中,不再每次查询都扫 resolver。
	 *
	 * <p>多模块字典 key 冲突按"先注册的优先"——Spring 收集顺序由模块加载顺序决定,
	 * 与单模块自治的语义一致(冲突本来就是命名不规范,字典层不做仲裁)。
	 */
	private Map<String, String> resourceTypeLabelCache = Collections.emptyMap();

	@Autowired
	public OpLogService(ObjectProvider<List<IOpLogContextResolver>> resolversProvider) {
		this.resolversProvider = resolversProvider;
	}

	/**
	 * 启动时聚合字典缓存,失败静默 fallback 到空表(查询页退化为展示原 type 英文,不阻塞业务)。
	 *
	 * <p>各 resolver 返回 {@link List}<{@link ResourceTypeDict}>(枚举驱动,可读),
	 * 这里转成 {@link Map} 是为了查询页 {@code O(1)} 命中——cache 是私有索引,
	 * 不属于"对外接口的入参/返回值/变量",符合 wldos"避免 Map"约束。
	 */
	@PostConstruct
	public void initResourceTypeLabels() {
		List<IOpLogContextResolver> resolvers = this.resolversProvider.getIfAvailable();
		if (resolvers == null || resolvers.isEmpty()) {
			return;
		}
		Map<String, String> agg = new HashMap<>();
		for (IOpLogContextResolver r : resolvers) {
			if (r == null) continue;
			try {
				List<ResourceTypeDict> partial = r.resourceTypeLabels();
				if (partial == null || partial.isEmpty()) continue;
				for (ResourceTypeDict d : partial) {
					if (d == null || d.getCode() == null || d.getLabel() == null) continue;
					// 多 resolver code 冲突时，先注册的优先（与文档约定一致）
					agg.putIfAbsent(d.getCode(), d.getLabel());
				}
			}
			catch (Exception ex) {
				log.warn("resourceTypeLabels init failed: {} - {}", r.getClass().getSimpleName(), ex.getMessage());
			}
		}
		this.resourceTypeLabelCache = Collections.unmodifiableMap(agg);
		log.info("[op.log] resource type label cache initialized, size={}", agg.size());
	}

	@Transactional
	public WoOpLog insertOne(WoOpLog log) {
		if (log == null) {
			return null;
		}
		return this.entityRepo.save(log);
	}

	@Transactional
	public int insertBatch(List<WoOpLog> logs) {
		if (logs == null || logs.isEmpty()) {
			return 0;
		}
		Iterable<WoOpLog> saved = this.entityRepo.saveAll(logs);
		int n = 0;
		if (saved != null) {
			for (WoOpLog ignored : saved) {
				n++;
			}
		}
		return n;
	}

	@Transactional(readOnly = true)
	public PageData<OpLogItem> queryList(PageQuery pageQuery) {
		if (pageQuery == null) {
			return new PageData<>(0, 1, 10, new ArrayList<>(0));
		}
		OpLogItem voProto = new OpLogItem();
		WoOpLog entityProto = new WoOpLog();
		PageData<OpLogItem> page = this.execQueryForPage(voProto, entityProto, pageQuery);
		List<OpLogItem> rows = page == null ? null : page.getRows();
		fillResourceTypeLabel(rows);
		enrichDisplayNames(rows);
		return page;
	}

	@Transactional(readOnly = true)
	public OpLogItem detail(Long id) {
		if (id == null) {
			return null;
		}
		WoOpLog e = this.findById(id);
		if (e == null) {
			return null;
		}
		OpLogItem item = toItem(e);
		List<OpLogItem> rows = Collections.singletonList(item);
		fillResourceTypeLabel(rows);
		enrichDisplayNames(rows);
		return item;
	}

	/**
	 * 按 {@link #resourceTypeLabelCache} 给每行回填中文 label;
	 * 字典里没登记的 type 留 {@code null},前端按"无 label 显示原 type"渲染。
	 */
	private void fillResourceTypeLabel(List<OpLogItem> rows) {
		if (rows == null || rows.isEmpty() || resourceTypeLabelCache.isEmpty()) {
			return;
		}
		for (OpLogItem r : rows) {
			if (r == null || isBlank(r.getResourceType())) continue;
			String label = resourceTypeLabelCache.get(r.getResourceType());
			if (label != null) {
				r.setResourceTypeLabel(label);
			}
		}
	}

	/* ============================================================
	 *  展示侧名称回填：批量解析操作者与资源名，避免 N+1
	 * ============================================================ */

	/**
	 * 兜底回填 {@code userName} 与 {@code resourceName}（仅针对老数据）。
	 *
	 * <p>新数据由 {@link com.wldos.platform.audit.adapter.OpLogPersistAdapter} 在异步消费侧
	 * 入库前已批量解析并落库，本方法对它们是 no-op；
	 * 仅对历史行（升级前没有 resource_name 列、user_name 为空等情况）触发一次 SPI 反查。
	 *
	 * <p>合规警示：这里反查得到的是<b>当前业务命名</b>，不再是事件发生时的快照——
	 * 老数据本来就丢失了快照信息，能展示已经是 best-effort，前端 UI 会标灰提示用户。
	 */
	private void enrichDisplayNames(List<OpLogItem> rows) {
		if (rows == null || rows.isEmpty()) {
			return;
		}
		List<IOpLogContextResolver> resolvers = this.resolversProvider.getIfAvailable();
		if (resolvers == null || resolvers.isEmpty()) {
			return;
		}

		Set<Long> userIds = new HashSet<>();
		Map<String, Set<String>> resourceIds = new HashMap<>();
		for (OpLogItem r : rows) {
			if (r == null) continue;
			Long uid = r.getUserId();
			if (uid != null && !Constants.GUEST_ID.equals(uid) && isBlank(r.getUserName())) {
				userIds.add(uid);
			}
			if (isBlank(r.getResourceName())) {
				String type = r.getResourceType();
				String rid = r.getResourceId();
				if (type != null && !type.isEmpty() && rid != null && !rid.isEmpty()) {
					resourceIds.computeIfAbsent(type, k -> new HashSet<>()).add(rid);
				}
			}
		}
		if (userIds.isEmpty() && resourceIds.isEmpty()) {
			return;
		}

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
				log.warn("op-log context resolver failed: {} - {}", resolver.getClass().getSimpleName(), ex.getMessage());
			}
		}

		for (OpLogItem r : rows) {
			if (r == null) continue;
			if (isBlank(r.getUserName()) && r.getUserId() != null) {
				String name = userNameMap.get(r.getUserId());
				if (name != null) {
					r.setUserName(name);
				}
			}
			if (isBlank(r.getResourceName()) && r.getResourceType() != null && r.getResourceId() != null) {
				Map<String, String> idMap = resourceNameMap.get(r.getResourceType());
				if (idMap != null) {
					String name = idMap.get(r.getResourceId());
					if (name != null) {
						r.setResourceName(name);
					}
				}
			}
		}
	}

	private static boolean isBlank(String s) {
		return s == null || s.isEmpty();
	}

	private static OpLogItem toItem(WoOpLog e) {
		OpLogItem v = new OpLogItem();
		// 同名同类型字段：userId / virtualUserId / userName / domainId / comId / module / action
		// / resourceType / resourceId / resourceName / requestPath / httpMethod / paramsSummary
		// / resultCode / errorMessage / durationMs / ip / userAgent / occurAt / createdAt
		// resourceName 已在写入侧由 OpLogPersistAdapter 落库，BeanCopier 直接拷过来；
		// 老数据若仍为空，由 enrichDisplayNames 兜底反查
		ENTITY_TO_VO.copy(e, v, null);
		// id：Long → String 类型不一致，BeanCopier 静默跳过，手工补
		v.setId(e.getId() == null ? null : String.valueOf(e.getId()));
		return v;
	}
}
