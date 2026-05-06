/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.calendar.dao.HolidayDao;
import com.wldos.platform.calendar.entity.WoCalendarHoliday;
import com.wldos.platform.calendar.enums.DayKindEnum;
import com.wldos.platform.calendar.enums.HolidaySourceEnum;
import com.wldos.platform.calendar.vo.HolidayItem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 节假日服务：管理端 CRUD + 客户端按年返回 Map。
 *
 * <p>职责：
 * <ul>
 *   <li>手工维护：单条 / 批量保存、删除、按年查询；</li>
 *   <li>对外提供按年 Map（key=yyyy-MM-dd, value=OFF/WORK），供任何业务模块或客户端拉取后注入本地兜底；</li>
 *   <li>外部同步入库时保护 MANUAL 条目：MANUAL 不会被 SYNC / IMPORT 覆盖。</li>
 * </ul>
 *
 * <p>表内只记录"与默认周末日历不同"的日子，调用方按 weekday 兜底处理"普通工作日 / 普通周末"。
 *
 * @author 元悉宇宙
 * @date 2026/04/30
 * @version 1.0
 */
@Slf4j
@Service
public class HolidayService extends EntityService<HolidayDao, WoCalendarHoliday, Long> {

	/* ========== 查询 ========== */

	/** 按年返回全量条目（VO 形态，给管理端表格用） */
	@Transactional(readOnly = true)
	public List<HolidayItem> listByYear(int year) {
		List<WoCalendarHoliday> rows = this.entityRepo.findByYear(year);
		List<HolidayItem> list = new ArrayList<>(rows == null ? 0 : rows.size());
		if (rows != null) {
			for (WoCalendarHoliday r : rows) {
				list.add(toItem(r));
			}
		}
		return list;
	}

	/**
	 * 按年返回 Map<yyyy-MM-dd, OFF/WORK>，供客户端 / 边缘服务拉取。
	 * 表内空时返回 emptyMap，调用方走兜底（客户端 holiday 包内置表）。
	 */
	@Transactional(readOnly = true)
	public Map<String, String> mapByYear(int year) {
		List<WoCalendarHoliday> rows = this.entityRepo.findByYear(year);
		if (rows == null || rows.isEmpty()) {
			return new HashMap<>(0);
		}
		Map<String, String> m = new LinkedHashMap<>(rows.size() * 2);
		for (WoCalendarHoliday r : rows) {
			if (r.getHolidayDate() == null || !StringUtils.hasText(r.getDayKind())) {
				continue;
			}
			m.put(r.getHolidayDate().toString(), r.getDayKind());
		}
		return m;
	}

	/** 按日期回查（暴露给 SyncService 与 Bootstrap 使用） */
	@Transactional(readOnly = true)
	public WoCalendarHoliday findByDate(Date date) {
		if (date == null) {
			return null;
		}
		return this.entityRepo.findByDate(date);
	}

	/** 该年是否已有任何条目 */
	@Transactional(readOnly = true)
	public boolean existsYear(int year) {
		return this.entityRepo.countByYear(year) > 0;
	}

	/* ========== 写入 ========== */

	/**
	 * 单条保存（新增或更新）：
	 *   - 入参带 id：按 id 更新；
	 *   - 入参不带 id：按 date 是否已存在决定 insert 还是 update。
	 *
	 * 写入前自动补齐 year，并校验 dayKind 合法。
	 */
	@Transactional
	public WoCalendarHoliday save(HolidayItem item, HolidaySourceEnum defaultSource) {
		validate(item);
		Date date = parseDate(item.getDate());
		WoCalendarHoliday entity;
		if (StringUtils.hasText(item.getId())) {
			entity = this.findById(Long.parseLong(item.getId()));
			if (entity == null) {
				throw new IllegalArgumentException("holiday not found: id=" + item.getId());
			}
		}
		else {
			entity = this.entityRepo.findByDate(date);
			if (entity == null) {
				entity = new WoCalendarHoliday();
			}
		}
		entity.setHolidayDate(date);
		entity.setYear(date.toLocalDate().getYear());
		entity.setDayKind(item.getDayKind());
		entity.setName(item.getName());
		entity.setRemark(item.getRemark());
		String source = StringUtils.hasText(item.getSource()) ? item.getSource() :
				(defaultSource == null ? HolidaySourceEnum.MANUAL.name() : defaultSource.name());
		entity.setSource(source);
		return this.saveOrUpdate(entity);
	}

	/**
	 * 批量保存：用于"导入兜底"、"外部 API 同步"。
	 *
	 * <p>保护规则：现有条目 source = MANUAL 时**不覆盖**（手工维护优先级最高）。
	 * 现有条目为非 MANUAL 时按本次 source 覆盖。
	 *
	 * @return 实际写入条数（含 update）
	 */
	@Transactional
	public int batchSave(List<HolidayItem> items, HolidaySourceEnum source) {
		if (items == null || items.isEmpty()) {
			return 0;
		}
		String src = (source == null ? HolidaySourceEnum.IMPORT : source).name();
		List<WoCalendarHoliday> rows = new ArrayList<>(items.size());
		int skip = 0;
		for (HolidayItem it : items) {
			try {
				validate(it);
				Date date = parseDate(it.getDate());
				WoCalendarHoliday exist = this.entityRepo.findByDate(date);
				if (exist != null && HolidaySourceEnum.MANUAL.name().equals(exist.getSource())) {
					skip++;
					continue;
				}
				WoCalendarHoliday e = exist == null ? new WoCalendarHoliday() : exist;
				e.setHolidayDate(date);
				e.setYear(date.toLocalDate().getYear());
				e.setDayKind(it.getDayKind());
				e.setName(it.getName());
				e.setRemark(it.getRemark());
				e.setSource(src);
				rows.add(e);
			}
			catch (Exception ex) {
				log.warn("[holiday.batchSave] skip invalid item={} reason={}", it == null ? null : it.getDate(), ex.getMessage());
			}
		}
		if (rows.isEmpty()) {
			return 0;
		}
		Iterable<WoCalendarHoliday> saved = this.saveOrUpdateAll(rows);
		int n = 0;
		if (saved != null) {
			for (WoCalendarHoliday ignored : saved) {
				n++;
			}
		}
		log.info("[holiday.batchSave] source={} saved={} skip(manual)={}", src, n, skip);
		return n;
	}

	/** 软删除（基于 BaseEntity.deleteFlag，由框架 deleteByIds 处理） */
	@Transactional
	public boolean removeById(Long id) {
		if (id == null) return false;
		WoCalendarHoliday e = this.findById(id);
		if (e == null) return false;
		this.deleteById(id);
		return true;
	}

	/* ========== utilities ========== */

	private static void validate(HolidayItem item) {
		if (item == null) {
			throw new IllegalArgumentException("holiday item is null");
		}
		if (!StringUtils.hasText(item.getDate())) {
			throw new IllegalArgumentException("date is required");
		}
		if (!StringUtils.hasText(item.getDayKind())) {
			throw new IllegalArgumentException("dayKind is required");
		}
		try {
			DayKindEnum.valueOf(item.getDayKind());
		}
		catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("dayKind invalid: " + item.getDayKind());
		}
	}

	private static Date parseDate(String s) {
		LocalDate ld = LocalDate.parse(s.trim());
		return Date.valueOf(ld);
	}

	private static HolidayItem toItem(WoCalendarHoliday r) {
		HolidayItem v = new HolidayItem();
		v.setId(r.getId() == null ? null : String.valueOf(r.getId()));
		v.setDate(r.getHolidayDate() == null ? null : r.getHolidayDate().toString());
		v.setYear(r.getYear());
		v.setDayKind(r.getDayKind());
		v.setName(r.getName());
		v.setSource(r.getSource());
		v.setRemark(r.getRemark());
		return v;
	}
}
