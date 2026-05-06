/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.dao;

import java.sql.Date;
import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.calendar.entity.WoCalendarHoliday;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 节假日表 Repository。
 *
 * @author 元悉宇宙
 * @date 2026/04/30
 * @version 1.0
 */
public interface HolidayDao extends BaseDao<WoCalendarHoliday, Long> {

	/** 按日期回查（软删除过滤） */
	@Query("select h.* from wo_calendar_holiday h " +
			"where h.delete_flag='normal' and h.holiday_date=:holidayDate limit 1")
	WoCalendarHoliday findByDate(@Param("holidayDate") Date holidayDate);

	/** 按年返回全部条目（按日期升序），用于客户端拉取与管理端按年展示 */
	@Query("select h.* from wo_calendar_holiday h " +
			"where h.delete_flag='normal' and h.year=:year order by h.holiday_date asc")
	List<WoCalendarHoliday> findByYear(@Param("year") int year);

	/** 该年是否已有任何条目（用于 seed 与外部同步时跳过已存在年份） */
	@Query("select count(1) from wo_calendar_holiday h " +
			"where h.delete_flag='normal' and h.year=:year")
	long countByYear(@Param("year") int year);
}
