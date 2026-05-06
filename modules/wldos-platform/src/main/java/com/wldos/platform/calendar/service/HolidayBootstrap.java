/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 节假日数据首次启动 seed。
 *
 * <p>启动后检查"当年 + 次年"是否已有任何条目；空年自动调用 {@link HolidaySyncService#importBuiltin}
 * 写入兜底（默认中国法定节假日，便于 zh-CN locale 开箱即用）。该过程对管理员透明。
 *
 * <p>开关：`wldos.platform.calendar.bootstrap-enabled=false` 可关闭（多实例部署、非 zh 站点等场景）。
 *
 * @author 元悉宇宙
 * @date 2026/04/30
 * @version 1.0
 */
@Slf4j
@Component
public class HolidayBootstrap implements ApplicationRunner {

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private HolidaySyncService syncService;

	@Value("${wldos.platform.calendar.bootstrap-enabled:true}")
	private boolean enabled;

	@Override
	public void run(org.springframework.boot.ApplicationArguments args) {
		if (!enabled) {
			log.info("[holiday.bootstrap] disabled by config");
			return;
		}
		int thisYear = LocalDate.now().getYear();
		seedIfEmpty(thisYear);
		seedIfEmpty(thisYear + 1);
	}

	private void seedIfEmpty(int year) {
		try {
			if (holidayService.existsYear(year)) {
				return;
			}
			int n = syncService.importBuiltin(year);
			if (n > 0) {
				log.info("[holiday.bootstrap] year={} seeded n={}", year, n);
			}
		}
		catch (Exception e) {
			log.warn("[holiday.bootstrap] year={} seed failed: {}", year, e.getMessage());
		}
	}
}
