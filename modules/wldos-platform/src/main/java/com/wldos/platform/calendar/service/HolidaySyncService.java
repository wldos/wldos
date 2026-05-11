/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.service;

import java.util.List;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.calendar.enums.HolidaySourceEnum;
import com.wldos.platform.calendar.vo.HolidayItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 节假日同步服务。
 *
 * <p>职责：
 * <ul>
 *   <li>从外部数据源（{@link HolidayExternalSource}，可被站点覆盖）拉取指定年份的全量条目；</li>
 *   <li>按"MANUAL 不覆盖"规则写入 wo_calendar_holiday；</li>
 *   <li>失败时降级为兜底常量（{@link HolidayBuiltin}）；</li>
 *   <li>仅做编排，落库委托 {@link HolidayService#batchSave}，避免重复 audit 处理。</li>
 * </ul>
 *
 * @author Yuanxi Universe
 * @date 2026/04/30
 * @version 1.0
 */
@Slf4j
@Service
public class HolidaySyncService extends NonEntityService {

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private HolidayExternalSource externalSource;

	/**
	 * 同步指定年份。
	 *
	 * @param year 公元年
	 * @param fallbackToBuiltin 外部源失败 / 返回空时是否降级到内置兜底
	 * @return 实际写入条数
	 */
	@Transactional
	public int syncYear(int year, boolean fallbackToBuiltin) {
		List<HolidayItem> items;
		try {
			items = externalSource.fetch(year);
		}
		catch (Exception e) {
			log.warn("[holiday.sync] external source {} fetch year={} failed: {}", externalSource.name(), year, e.getMessage());
			items = null;
		}

		if ((items == null || items.isEmpty()) && fallbackToBuiltin) {
			items = HolidayBuiltin.builtin(year);
			log.info("[holiday.sync] fallback to builtin for year={}, n={}", year, items == null ? 0 : items.size());
			if (items == null || items.isEmpty()) {
				return 0;
			}
			return holidayService.batchSave(items, HolidaySourceEnum.IMPORT);
		}

		if (items == null || items.isEmpty()) {
			return 0;
		}
		return holidayService.batchSave(items, HolidaySourceEnum.SYNC);
	}

	/**
	 * 强制导入指定年份的内置兜底（管理端"导入兜底"按钮）。
	 * 已存在的 MANUAL 条目仍受保护不被覆盖。
	 *
	 * @return 实际写入条数
	 */
	@Transactional
	public int importBuiltin(int year) {
		List<HolidayItem> items = HolidayBuiltin.builtin(year);
		if (items == null || items.isEmpty()) {
			log.info("[holiday.sync] no builtin for year={}", year);
			return 0;
		}
		return holidayService.batchSave(items, HolidaySourceEnum.IMPORT);
	}
}
