/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.service;

import java.util.List;

import com.wldos.platform.calendar.vo.HolidayItem;

/**
 * 外部节假日数据源 SPI。
 *
 * <p>默认实现 {@link DefaultHolidayExternalSource} 仅返回内置兜底（不发起网络调用），
 * 站点可通过提供同接口的实现（如对接"国务院假办"网站爬取 / 第三方天气 API / 多 locale 节日服务等）覆盖默认实现，
 * 由 Spring `@ConditionalOnMissingBean` 决定激活策略，符合 wldos 契约模式。
 *
 * <p>实现侧约束：
 * <ul>
 *   <li>只读返回该年份的全量条目；不要写库（写库由 {@link HolidaySyncService} 统一处理，避免重复 audit）；</li>
 *   <li>条目 source 应填 SYNC（由调用方再校正一次）；</li>
 *   <li>失败时抛 {@link Exception}，由调用方捕获并降级。</li>
 * </ul>
 *
 * @author 元悉宇宙
 * @date 2026/04/30
 * @version 1.0
 */
public interface HolidayExternalSource {

	/**
	 * 拉取指定年份的全量节假日条目。
	 *
	 * @param year 公元年（如 2027）
	 * @return 条目列表（可空）；不应返回 null，建议返回 emptyList
	 */
	List<HolidayItem> fetch(int year) throws Exception;

	/**
	 * 数据源标识（用于事件 / 日志 / 多源对账）。
	 */
	String name();
}
