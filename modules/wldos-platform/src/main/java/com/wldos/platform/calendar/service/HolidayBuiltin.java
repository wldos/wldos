/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.wldos.platform.calendar.enums.DayKindEnum;
import com.wldos.platform.calendar.enums.HolidaySourceEnum;
import com.wldos.platform.calendar.vo.HolidayItem;

/**
 * 节假日内置兜底数据。
 *
 * <p>用途：
 * <ul>
 *   <li>{@link HolidayBootstrap} 首次启动检测到表中无该年数据时，自动 IMPORT 进库；</li>
 *   <li>管理端"导入兜底"按钮可调用 {@link #builtin(int)} 拿到列表后调 batchSave；</li>
 *   <li>本兜底默认覆盖中国法定节假日（wldos 主要用户群），其他 locale 站点可关闭 bootstrap
 *       并注入自家 {@link HolidayExternalSource}，多 locale 由 SPI 扩展承担。</li>
 * </ul>
 *
 * <p>每年 11~12 月外部数据稳定后，应通过管理端"同步外部 API"或"手工编辑"维护新一年数据；
 * 兜底常量仅在首发 / 离线落地时保险用，不替代日常维护。
 *
 * @author 元悉宇宙
 * @date 2026/04/30
 * @version 1.0
 */
public final class HolidayBuiltin {

	private HolidayBuiltin() {
	}

	/**
	 * 当前内置支持的兜底年份列表。
	 *
	 * <p>新加年份时往该列表追加，并在 {@link #builtin(int)} 增加 case 即可。
	 */
	public static final List<Integer> SUPPORTED_YEARS = Collections.unmodifiableList(
			Arrays.asList(2026)
	);

	/**
	 * 取指定年份的兜底列表；不支持的年份返回空。
	 */
	public static List<HolidayItem> builtin(int year) {
		switch (year) {
			case 2026:
				return builtin2026();
			default:
				return Collections.emptyList();
		}
	}

	/**
	 * 2026 年中国法定节假日（OFF）+ 调休补班（WORK）。示例数据，正式以国务院假办公告为准。
	 */
	private static List<HolidayItem> builtin2026() {
		List<HolidayItem> list = new ArrayList<>(36);
		add(list, "2026-01-01", DayKindEnum.OFF, "元旦");
		add(list, "2026-02-14", DayKindEnum.WORK, "春节调休补班");
		add(list, "2026-02-16", DayKindEnum.OFF, "春节");
		add(list, "2026-02-17", DayKindEnum.OFF, "春节");
		add(list, "2026-02-18", DayKindEnum.OFF, "春节");
		add(list, "2026-02-19", DayKindEnum.OFF, "春节");
		add(list, "2026-02-20", DayKindEnum.OFF, "春节");
		add(list, "2026-02-21", DayKindEnum.OFF, "春节");
		add(list, "2026-02-22", DayKindEnum.OFF, "春节");
		add(list, "2026-02-28", DayKindEnum.WORK, "春节调休补班");
		add(list, "2026-04-04", DayKindEnum.OFF, "清明节");
		add(list, "2026-04-05", DayKindEnum.OFF, "清明节");
		add(list, "2026-04-06", DayKindEnum.OFF, "清明节");
		add(list, "2026-04-07", DayKindEnum.OFF, "清明节");
		add(list, "2026-04-26", DayKindEnum.WORK, "劳动节调休补班");
		add(list, "2026-05-01", DayKindEnum.OFF, "劳动节");
		add(list, "2026-05-02", DayKindEnum.OFF, "劳动节");
		add(list, "2026-05-03", DayKindEnum.OFF, "劳动节");
		add(list, "2026-05-04", DayKindEnum.OFF, "劳动节");
		add(list, "2026-05-05", DayKindEnum.OFF, "劳动节");
		add(list, "2026-05-09", DayKindEnum.WORK, "劳动节调休补班");
		add(list, "2026-06-19", DayKindEnum.OFF, "端午节");
		add(list, "2026-06-20", DayKindEnum.OFF, "端午节");
		add(list, "2026-06-21", DayKindEnum.OFF, "端午节");
		add(list, "2026-09-25", DayKindEnum.OFF, "中秋节");
		add(list, "2026-09-26", DayKindEnum.OFF, "中秋节");
		add(list, "2026-09-27", DayKindEnum.WORK, "国庆调休补班");
		add(list, "2026-10-01", DayKindEnum.OFF, "国庆节");
		add(list, "2026-10-02", DayKindEnum.OFF, "国庆节");
		add(list, "2026-10-03", DayKindEnum.OFF, "国庆节");
		add(list, "2026-10-04", DayKindEnum.OFF, "国庆节");
		add(list, "2026-10-05", DayKindEnum.OFF, "国庆节");
		add(list, "2026-10-06", DayKindEnum.OFF, "国庆节");
		add(list, "2026-10-07", DayKindEnum.OFF, "国庆节");
		add(list, "2026-10-08", DayKindEnum.OFF, "国庆节");
		add(list, "2026-10-10", DayKindEnum.WORK, "国庆调休补班");
		return list;
	}

	private static void add(List<HolidayItem> list, String date, DayKindEnum kind, String name) {
		HolidayItem v = new HolidayItem();
		v.setDate(date);
		v.setDayKind(kind.name());
		v.setName(name);
		v.setSource(HolidaySourceEnum.IMPORT.name());
		list.add(v);
	}
}
