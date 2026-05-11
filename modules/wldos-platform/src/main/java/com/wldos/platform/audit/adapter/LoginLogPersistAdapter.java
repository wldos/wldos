/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.adapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.wldos.common.utils.DateUtils;
import io.github.wldos.framework.support.audit.LoginEvent;
import io.github.wldos.framework.common.IDGen;
import com.wldos.framework.support.audit.port.LoginLogPersistPort;
import com.wldos.platform.audit.entity.WoLoginLog;
import com.wldos.platform.audit.service.LoginLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Component;

/**
 * 登录日志持久化适配器（端口实现）。
 *
 * <p>把 framework 层的 {@link LoginLogPersistPort} 适配到 platform 的
 * {@link LoginLogService}（绑定 {@code wo_login_log} 表）：
 * <ul>
 *   <li>{@link LoginEvent} → {@link WoLoginLog} 字段映射；</li>
 *   <li>同名同类型字段走 {@link BeanCopier}（CGLIB 字节码生成，性能优于反射 BeanUtils 数十倍）；</li>
 *   <li>类型不一致字段（enum→String）与源端不存在的字段（id/createdAt）手工赋值；</li>
 *   <li>雪花 ID 在适配器侧生成（{@link IDGen#nextId()}），保证 entity 入库前 id 已就位；</li>
 *   <li>{@code occurAt} 取业务侧填的事件时间；落空时降级为消费时间；</li>
 *   <li>{@code createdAt} 显式回填 UTC 时间，避免 spring-data-jdbc 把 null 写到 NOT NULL 列。</li>
 * </ul>
 *
 * @author Yuanxi Universe
 * @date 2026/05/04
 * @version 1.0
 */
@Component
public class LoginLogPersistAdapter implements LoginLogPersistPort {

	/**
	 * 同名同类型字段批量拷贝。
	 *
	 * <p>{@code useConverter=false}：性能更优；类型不匹配的字段（如 enum→String）会被自动跳过，
	 * 由 {@link #toEntity(LoginEvent)} 手工补齐。
	 */
	private static final BeanCopier COPIER = BeanCopier.create(LoginEvent.class, WoLoginLog.class, false);

	@Autowired
	private LoginLogService loginLogService;

	@Override
	public void saveOne(LoginEvent event) {
		if (event == null) {
			return;
		}
		this.loginLogService.insertOne(toEntity(event));
	}

	@Override
	public void saveBatch(List<LoginEvent> events) {
		if (events == null || events.isEmpty()) {
			return;
		}
		List<WoLoginLog> rows = new ArrayList<>(events.size());
		for (LoginEvent e : events) {
			if (e == null) continue;
			rows.add(toEntity(e));
		}
		if (rows.isEmpty()) return;
		this.loginLogService.insertBatch(rows);
	}

	private static WoLoginLog toEntity(LoginEvent e) {
		WoLoginLog row = new WoLoginLog();
		// 同名同类型字段：userId / loginAccount / oauthProvider / failReason / ip / userAgent
		// / domainId / comId / occurAt（Timestamp）
		COPIER.copy(e, row, null);

		Timestamp now = DateUtils.convSQLDate(new Date());
		row.setId(IDGen.nextId());
		// enum → String：BeanCopier 类型不匹配自动跳过，这里手工补
		row.setEventType(e.getEventType() == null ? null : e.getEventType().name());
		row.setResult(e.getResult() == null ? null : e.getResult().name());
		// occurAt 兜底：业务侧未填则用消费时间（极少见）
		if (row.getOccurAt() == null) {
			row.setOccurAt(now);
		}
		// createdAt 必须显式回填：spring-data-jdbc 默认会把所有字段（含 null）一并 INSERT，
		// 留空会向 NOT NULL 列写 NULL（MySQL 严格模式会抛 "Column 'created_at' cannot be null"）；
		// 同时 WLDOS 时间字段强约束 UTC，统一走 DateUtils.convSQLDate，不依赖 DB CURRENT_TIMESTAMP。
		row.setCreatedAt(now);
		return row;
	}
}
