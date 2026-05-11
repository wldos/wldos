/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.entity;

import java.sql.Date;

import com.wldos.framework.mvc.entity.BaseEntity;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 法定节假日 / 调休补班单日条目，对应表 wo_calendar_holiday。
 *
 * <p>设计要点：
 * <ul>
 *   <li>单条单日，便于按 ID 增删改；通过年份字段冗余加速年度筛选；</li>
 *   <li>仅记录"与默认周末日历不同"的日子；其余日期不入表，由调用方按 weekday 兜底；</li>
 *   <li>source 区分手工 / 同步 / 导入，外部同步不覆盖 MANUAL 条目；</li>
 *   <li>审计字段继承 BaseEntity（id / createBy / updateBy / createTime / updateTime / deleteFlag / versions ...）。</li>
 * </ul>
 *
 * @author Yuanxi Universe
 * @date 2026/04/30
 * @version 1.0
 */
@Table
@Getter
@Setter
public class WoCalendarHoliday extends BaseEntity {

	/** 节假日 / 调休补班的具体日期（业务唯一键，软删除维度内唯一） */
	private Date holidayDate;

	/** 年份（冗余 holiday_date 的年份，便于年度查询与聚合） */
	private Integer year;

	/** 单日类型：OFF=放假 / WORK=调休补班，对应 enums.DayKindEnum */
	private String dayKind;

	/** 节日名（"春节"/"国庆"/"调休补班"等，可空；多语展示由前端 i18n 处理） */
	private String name;

	/** 数据来源：MANUAL / SYNC / IMPORT，对应 enums.HolidaySourceEnum */
	private String source;

	/** 备注 */
	private String remark;
}
