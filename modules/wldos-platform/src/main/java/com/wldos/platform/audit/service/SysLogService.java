/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.platform.support.rbac.RbacScopeSqlFragments;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.audit.dao.SysLogDao;
import com.wldos.platform.audit.entity.WoSysLog;
import com.wldos.platform.audit.support.SysLogMetadataQuery;
import com.wldos.platform.audit.support.SystemLogIpEnricher;
import com.wldos.platform.audit.support.SystemLogMetadataEnricher;
import com.wldos.platform.audit.vo.SysLogItem;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统日志 Service（写入 + 管理端列表查询，与 LoginLogService/OpLogService 同构）。
 *
 * @author Yuanxi Universe
 * @date 2025/05/04
 * @version 1.0
 */
@Slf4j
@Service
public class SysLogService extends EntityService<SysLogDao, WoSysLog, Long> {

	private static final BeanCopier ENTITY_TO_VO = BeanCopier.create(WoSysLog.class, SysLogItem.class, false);

	@Transactional
	public WoSysLog insertOne(WoSysLog log) {
		if (log == null) return null;
		return this.entityRepo.save(log);
	}

	@Transactional
	public int insertBatch(List<WoSysLog> logs) {
		if (logs == null || logs.isEmpty()) return 0;
		Iterable<WoSysLog> saved = this.entityRepo.saveAll(logs);
		int n = 0;
		if (saved != null) {
			for (WoSysLog ignored : saved) {
				n++;
			}
		}
		return n;
	}

	@Transactional(readOnly = true)
	public PageData<SysLogItem> queryList(PageQuery pageQuery) {
		if (pageQuery == null) {
			return new PageData<>(0, 1, 10, new ArrayList<>(0));
		}
		Map<String, Object> condition = pageQuery.getCondition();
		if (condition == null) {
			condition = new HashMap<>(4);
			pageQuery.setCondition(condition);
		}
		String requestUri = SysLogMetadataQuery.takeConditionString(condition, "requestUri");
		String requestMethod = SysLogMetadataQuery.takeConditionString(condition, "requestMethod");
		String ip = SysLogMetadataQuery.takeConditionString(condition, "ip");
		String host = SysLogMetadataQuery.takeConditionString(condition, "requestHost");
		if (host == null) {
			host = SysLogMetadataQuery.takeConditionString(condition, "host");
		}
		String userAgent = SysLogMetadataQuery.takeConditionString(condition, "userAgent");
		String actorKind = SysLogMetadataQuery.takeConditionString(condition, "actorKind");

		PageData<SysLogItem> page;
		if (SysLogMetadataQuery.hasAnyFilter(requestUri, requestMethod, ip, host, userAgent, actorKind)) {
			page = queryListWithMetadata(pageQuery, requestUri, requestMethod, ip, host, userAgent, actorKind);
		}
		else {
			SysLogItem voProto = new SysLogItem();
			WoSysLog entityProto = new WoSysLog();
			page = this.execQueryForPage(voProto, entityProto, pageQuery);
		}
		if (page.getRows() != null) {
			for (SysLogItem item : page.getRows()) {
				fillMetadataFields(item);
			}
		}
		return page;
	}

	private PageData<SysLogItem> queryListWithMetadata(PageQuery pageQuery, String requestUri, String requestMethod,
			String ip, String host, String userAgent, String actorKind) {
		StringBuilder sql = new StringBuilder("select a.* from wo_sys_log a where 1=1 ");
		Map<String, Object> params = new HashMap<>(8);
		SysLogMetadataQuery.appendMetadataFilters(sql, params, requestUri, requestMethod, ip, host, userAgent,
				actorKind);
		Map<String, Object> condition = pageQuery.getCondition();
		if (condition != null) {
			if (condition.remove(RbacScopeSqlFragments.SCOPE_ROW_PLATFORM_LAYER) != null) {
				RbacScopeSqlFragments.appendRowPlatformLayerSql(sql, "a");
			}
			Object sysLogTenantUnion = condition.remove(RbacScopeSqlFragments.SCOPE_SYSLOG_TENANT_UNION);
			if (sysLogTenantUnion != null) {
				RbacScopeSqlFragments.appendSysLogTenantUnionNamed(sql, params, "a", sysLogTenantUnion);
			}
		}
		this.commonOperate.appendConditionNamed(sql, params, WoSysLog.class, "a", pageQuery.getCondition());
		this.commonOperate.appendFilterNamed(sql, params, WoSysLog.class, "a", pageQuery.getFilter());
		this.commonOperate.appendOrderByNamed(sql, pageQuery.getSorter(), "a", "a.occur_at desc");
		return this.commonOperate.execPageQueryNamed(sql.toString(), params, pageQuery, SysLogItem.class);
	}

	@Transactional(readOnly = true)
	public SysLogItem detail(Long id) {
		if (id == null) return null;
		WoSysLog e = this.findById(id);
		if (e == null) return null;
		return toItem(e);
	}

	private static SysLogItem toItem(WoSysLog e) {
		SysLogItem v = new SysLogItem();
		ENTITY_TO_VO.copy(e, v, null);
		v.setId(e.getId() == null ? null : String.valueOf(e.getId()));
		fillMetadataFields(v);
		return v;
	}

	private static void fillMetadataFields(SysLogItem item) {
		if (item == null) {
			return;
		}
		String metadata = item.getMetadata();
		item.setIp(SystemLogIpEnricher.extractIpFromMetadata(metadata));
		String requestPath = SystemLogMetadataEnricher.extractUriFromMetadata(metadata);
		item.setRequestUri(requestPath);
		item.setRequestUrl(requestPath);
		item.setRequestMethod(SystemLogMetadataEnricher.extractMethodFromMetadata(metadata));
		item.setRequestHost(SystemLogMetadataEnricher.extractHostFromMetadata(metadata));
		item.setUserAgent(SystemLogMetadataEnricher.extractUserAgentFromMetadata(metadata));
		item.setReferer(SystemLogMetadataEnricher.extractRefererFromMetadata(metadata));
		item.setActorKind(SystemLogMetadataEnricher.extractActorKindFromMetadata(metadata));
		item.setAggregateCount(SystemLogMetadataEnricher.extractAggregateCountFromMetadata(metadata));
	}
}
