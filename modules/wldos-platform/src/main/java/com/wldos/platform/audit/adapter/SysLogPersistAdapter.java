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
import java.util.Date;
import java.util.List;

import io.github.wldos.framework.common.IDGen;
import io.github.wldos.common.utils.DateUtils;
import io.github.wldos.framework.support.audit.SystemEvent;
import com.wldos.framework.support.audit.port.SystemLogPersistPort;
import com.wldos.platform.audit.entity.WoSysLog;
import com.wldos.platform.audit.service.SysLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Component;

/**
 * 系统日志持久化适配器（端口实现）。
 *
 * <p>{@link SystemEvent} → {@link WoSysLog} 字段映射；同名同类型字段走 {@link BeanCopier}，
 * {@code severity}（enum → String）与源端不存在的字段（id/createdAt）手工补齐；雪花 ID 在适配器侧生成。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Component
public class SysLogPersistAdapter implements SystemLogPersistPort {

	/**
	 * 同名同类型字段批量拷贝。{@code severity}（{@code SystemEventSeverity}→{@code String}）
	 * 类型不一致会被自动跳过，由 {@link #toEntity(SystemEvent)} 手工补齐。
	 */
	private static final BeanCopier COPIER = BeanCopier.create(SystemEvent.class, WoSysLog.class, false);

	@Autowired
	private SysLogService sysLogService;

	@Override
	public void saveOne(SystemEvent event) {
		if (event == null) return;
		this.sysLogService.insertOne(toEntity(event));
	}

	@Override
	public void saveBatch(List<SystemEvent> events) {
		if (events == null || events.isEmpty()) return;
		List<WoSysLog> rows = new ArrayList<>(events.size());
		for (SystemEvent e : events) {
			if (e == null) continue;
			rows.add(toEntity(e));
		}
		if (rows.isEmpty()) return;
		this.sysLogService.insertBatch(rows);
	}

	private static WoSysLog toEntity(SystemEvent e) {
		WoSysLog row = new WoSysLog();
		// 同名同类型字段：sourceModule / eventType / message / metadata / operatorUserId
		// / domainId / comId / occurAt（Timestamp）
		COPIER.copy(e, row, null);

		Timestamp now = DateUtils.convSQLDate(new Date());
		row.setId(IDGen.nextId());
		// enum → String：BeanCopier 自动跳过，这里手工补
		row.setSeverity(e.getSeverity() == null ? null : e.getSeverity().name());
		if (row.getOccurAt() == null) {
			row.setOccurAt(now);
		}
		// createdAt 必须显式回填：spring-data-jdbc 会把 null 显式 INSERT 到 NOT NULL 列；同时统一 UTC 时间。
		row.setCreatedAt(now);
		return row;
	}
}
