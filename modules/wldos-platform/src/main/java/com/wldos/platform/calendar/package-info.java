/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
/**
 * wldos 平台基础能力：节假日日历。
 *
 * <p>定位：与 wo_region 同级的平台基础设施，跨业务复用。
 * 表前缀 wo_*；包路径 com.wldos.platform.calendar.{entity,dao,service,controller,vo,enums,schedule}。
 *
 * <p>对外能力：
 * <ul>
 *   <li>管理端：/admin/sys/calendar/holiday/* 列表 / 单条 CRUD / 批量保存 / 同步外部 / 导入兜底；</li>
 *   <li>程序内调用：注入 {@link com.wldos.platform.calendar.service.HolidayService}，{@code mapByYear} 返回 Map&lt;yyyy-MM-dd, OFF/WORK&gt;；</li>
 *   <li>外部数据源：{@link com.wldos.platform.calendar.service.HolidayExternalSource} SPI，
 *       默认 noop 实现，站点可注入同接口 Bean 覆盖（{@code @ConditionalOnMissingBean} 激活）。</li>
 * </ul>
 *
 * <p>i18n 取舍：name 字段语义层透传（默认 seed 中文节日名服务于中国 locale 的开箱即用）；
 * 多语展示由前端自行 i18n。其他 locale 站点关闭 bootstrap 自启 seed，注入自家 HolidayExternalSource 即可。
 */
package com.wldos.platform.calendar;
