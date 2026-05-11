/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.calendar.entity.WoCalendarHoliday;
import com.wldos.platform.calendar.enums.DayKindEnum;
import com.wldos.platform.calendar.enums.HolidaySourceEnum;
import com.wldos.platform.calendar.service.HolidayBuiltin;
import com.wldos.platform.calendar.service.HolidayService;
import com.wldos.platform.calendar.service.HolidaySyncService;
import com.wldos.platform.calendar.vo.HolidayItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.wldos.common.res.Result;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 节假日管理端 controller。
 *
 * <p>路径规划（位于"系统设置"菜单下）：
 * <pre>
 *   GET    /admin/sys/calendar/holiday/list?year=2026                按年列表
 *   POST   /admin/sys/calendar/holiday/save                          单条保存（新增 / 更新）
 *   POST   /admin/sys/calendar/holiday/batch-save                    批量保存
 *   DELETE /admin/sys/calendar/holiday/delete  body={id}             删除（直接复用 EntityController.remove，物理删；日历条目可重建，无需软删）
 *   POST   /admin/sys/calendar/holiday/sync?year=2026                手工触发外部同步
 *   POST   /admin/sys/calendar/holiday/import-builtin?year=2026      导入内置兜底
 *   GET    /admin/sys/calendar/holiday/builtin-years                 内置兜底支持的年份列表
 *   GET    /admin/sys/calendar/holiday/enums                         day-kind / source 等枚举
 * </pre>
 *
 * @author Yuanxi Universe
 * @date 2026/04/30
 * @version 1.0
 */
@Api(tags = "节假日日历")
@RestController
@RequestMapping("/admin/sys/calendar/holiday")
public class HolidayAdminController extends EntityController<HolidayService, WoCalendarHoliday> {

	@Autowired
	private HolidaySyncService syncService;

	@ApiOperation(value = "按年查询节假日", notes = "返回该年全部条目（升序），仅记录与默认周末日历不同的日子")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "year", value = "公元年", dataTypeClass = Integer.class, paramType = "query", example = "2026")
	})
	@GetMapping("/list")
	public List<HolidayItem> listByYear(@RequestParam(value = "year", required = false) Integer year) {
		int y = (year == null) ? LocalDate.now().getYear() : year;
		return this.service.listByYear(y);
	}

	@ApiOperation(value = "保存节假日（单条）", notes = "新增或更新；id 存在按 id 更新，否则按 date 判断")
	@PostMapping("/save")
	@OpLog(action = "保存节假日（单条）", resourceType = "calendar_holiday", resourceId = "#item.id")
	public Result<HolidayItem> save(@RequestBody HolidayItem item) {
		WoCalendarHoliday entity = this.service.save(item, HolidaySourceEnum.MANUAL);
		HolidayItem v = new HolidayItem();
		v.setId(entity.getId() == null ? null : String.valueOf(entity.getId()));
		v.setDate(entity.getHolidayDate() == null ? null : entity.getHolidayDate().toString());
		v.setYear(entity.getYear());
		v.setDayKind(entity.getDayKind());
		v.setName(entity.getName());
		v.setSource(entity.getSource());
		v.setRemark(entity.getRemark());
		return Result.ok(v);
	}

	@ApiOperation(value = "批量保存节假日", notes = "受 MANUAL 优先级保护：现有 MANUAL 条目不会被覆盖")
	@PostMapping("/batch-save")
	@OpLog(action = "批量保存节假日", resourceType = "calendar_holiday")
	public Result<Integer> batchSave(@RequestBody List<HolidayItem> items) {
		int n = this.service.batchSave(items, HolidaySourceEnum.MANUAL);
		return Result.ok(n);
	}

	// 删除直接复用父类 EntityController.remove(@RequestBody E)，路径 DELETE /delete；
	// 父类是物理删（entityRepo.delete(entity)），日历条目可重建，物理删合理。
	// 前端 service.js 用 { method: 'DELETE', data: { id } }（与 ceos-ads / ceos-place 等其他 admin 模块一致）。

	@ApiOperation(value = "手工触发外部同步", notes = "调用 HolidayExternalSource 拉取并入库，MANUAL 不被覆盖；外部源未实现时返回 0")
	@PostMapping("/sync")
	@OpLog(action = "手工触发节假日同步", resourceType = "calendar_holiday", resourceId = "#year")
	public Result<Integer> sync(
			@ApiParam("公元年") @RequestParam(value = "year", required = false) Integer year,
			@ApiParam("外部源失败时是否降级兜底（默认否）") @RequestParam(value = "fallback", required = false) Boolean fallback) {
		int y = (year == null) ? LocalDate.now().getYear() : year;
		boolean fb = (fallback != null && fallback);
		int n = this.syncService.syncYear(y, fb);
		return Result.ok(n);
	}

	@ApiOperation(value = "导入内置兜底", notes = "把 HolidayBuiltin 的兜底数据写入指定年份；MANUAL 不被覆盖")
	@PostMapping("/import-builtin")
	@OpLog(action = "导入内置节假日兜底", resourceType = "calendar_holiday", resourceId = "#year")
	public Result<Integer> importBuiltin(@ApiParam("公元年") @RequestParam(value = "year", required = false) Integer year) {
		int y = (year == null) ? LocalDate.now().getYear() : year;
		return Result.ok(this.syncService.importBuiltin(y));
	}

	@ApiOperation(value = "内置兜底支持的年份", notes = "用于前端下拉提示")
	@GetMapping("/builtin-years")
	public List<Integer> builtinYears() {
		return HolidayBuiltin.SUPPORTED_YEARS;
	}

	@ApiOperation(value = "枚举字典", notes = "返回 dayKind / source 枚举，结构 {dayKind:[{value,label}], source:[{value,label}]}")
	@GetMapping("/enums")
	public Map<String, List<EnumOption>> enums() {
		Map<String, List<EnumOption>> m = new LinkedHashMap<>(2);
		List<EnumOption> dk = new ArrayList<>();
		for (DayKindEnum k : DayKindEnum.values()) {
			dk.add(new EnumOption(k.getValue(), k.getLabel()));
		}
		List<EnumOption> sc = new ArrayList<>();
		for (HolidaySourceEnum s : HolidaySourceEnum.values()) {
			sc.add(new EnumOption(s.getValue(), s.getLabel()));
		}
		m.put("dayKind", dk);
		m.put("source", sc);
		return m;
	}

	/** 枚举 VO（与 wldos SelectOption 兼容；本模块尽量保持轻依赖） */
	public static class EnumOption {
		public String value;
		public String label;

		public EnumOption(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String getValue() { return value; }
		public String getLabel() { return label; }
	}
}
