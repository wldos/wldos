/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.schedule;

import java.time.LocalDate;

import io.github.wldos.framework.support.audit.ISystemLogger;
import io.github.wldos.framework.support.audit.SystemEvent;
import com.wldos.platform.calendar.service.HolidaySyncService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 节假日定时同步任务。
 *
 * <p>触发节奏（默认 cron）：
 * <ul>
 *   <li>每天 03:30 检查当年与次年；当年缺数据立即同步，次年仅在 11/1 之后开始尝试同步；</li>
 *   <li>外部源失败时不降级到兜底（避免每天反复写入兜底覆盖管理员的修订），降级仅在
 *       {@link com.wldos.platform.calendar.service.HolidayBootstrap} 首次启动时使用。</li>
 * </ul>
 *
 * <p>开关：`wldos.platform.calendar.sync-enabled=false` 可关闭定时任务。
 *
 * <p>@EnableScheduling 已在 wldos-framework 激活，此处仅 @Scheduled 即可。
 *
 * @author Yuanxi Universe
 * @date 2026/04/30
 * @version 1.0
 */
@Slf4j
@Component
public class HolidaySyncTask {

	@Autowired
	private HolidaySyncService syncService;

	@Autowired(required = false)
	private ISystemLogger systemLogger;

	@Value("${wldos.platform.calendar.sync-enabled:true}")
	private boolean enabled;

	@Scheduled(cron = "${wldos.platform.calendar.sync-cron:0 30 3 * * ?}")
	public void daily() {
		if (!enabled) {
			return;
		}
		LocalDate today = LocalDate.now();
		int thisYear = today.getYear();
		int nextYear = thisYear + 1;

		runOne(thisYear);

		// 次年仅在 11 月 1 日之后开始尝试，外部数据源在此之前通常未公布
		if (today.getMonthValue() < 11) {
			return;
		}
		runOne(nextYear);
	}

	/** 同步单个年度并记录系统事件（成功/失败均落系统日志，便于排查调度漂移）。 */
	private void runOne(int year) {
		try {
			int n = syncService.syncYear(year, false);
			if (n > 0) {
				log.info("[holiday.cron] year={} synced n={}", year, n);
			}
			recordSysEvent(SystemEvent
					.info("scheduler", "HOLIDAY_SYNC_OK", "节假日同步成功 year=" + year + " count=" + n)
					.withMetadata("{\"year\":" + year + ",\"count\":" + n + "}"));
		}
		catch (Exception e) {
			log.warn("[holiday.cron] year={} sync failed: {}", year, e.getMessage());
			recordSysEvent(SystemEvent
					.warn("scheduler", "HOLIDAY_SYNC_FAIL", "节假日同步失败 year=" + year + " : " + e.getMessage())
					.withMetadata("{\"year\":" + year + ",\"error\":\"" + safe(e.getMessage()) + "\"}"));
		}
	}

	private void recordSysEvent(SystemEvent ev) {
		if (systemLogger == null || ev == null) return;
		systemLogger.recordAsync(ev);
	}

	private static String safe(String s) {
		if (s == null) return "";
		return s.replace("\"", "\\\"");
	}
}
