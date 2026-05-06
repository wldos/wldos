/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.service;

import java.util.Collections;
import java.util.List;

import com.wldos.platform.calendar.vo.HolidayItem;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link HolidayExternalSource} 的默认实现：不发起任何外部调用，始终返回空列表。
 *
 * <p>设计意图：
 * <ul>
 *   <li>站点未对接外部数据源时，{@link HolidaySyncService} 调用此实现，能正常走完流程而不报错；</li>
 *   <li>真实站点应自行实现 HolidayExternalSource 并注入 Bean，覆盖本默认实现；</li>
 *   <li>遵循 wldos 契约模式：契约 + 默认实现 + 条件激活，业务侧只注入接口。</li>
 * </ul>
 *
 * <p>注：本类**不**直接标 {@code @Component}；由 {@link HolidayCalendarConfiguration} 通过
 * {@code @Bean + @ConditionalOnMissingBean} 装配，避免 {@code @Component} 上加
 * {@code @ConditionalOnMissingBean} 的"自匹配"陷阱。
 *
 * @author 元悉宇宙
 * @date 2026/04/30
 * @version 1.0
 */
@Slf4j
public class DefaultHolidayExternalSource implements HolidayExternalSource {

	@Override
	public List<HolidayItem> fetch(int year) {
		log.info("[holiday.sync] no external source configured, return empty for year={}", year);
		return Collections.emptyList();
	}

	@Override
	public String name() {
		return "default-noop";
	}
}
