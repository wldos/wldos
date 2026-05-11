/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.enums;

/**
 * 日历单日类型。
 *
 * <ul>
 *   <li>OFF：法定放假日（含调休产生的连休日）；</li>
 *   <li>WORK：调休补班日（原本的周末，被调为工作日）。</li>
 * </ul>
 *
 * <p>表 wo_calendar_holiday 内**仅记录**与默认周末日历不同的日子；
 * 平日工作 / 周末休息属于默认行为，不入表，由调用方按 weekday 兜底。
 *
 * @author Yuanxi Universe
 * @date 2026/04/30
 * @version 1.0
 */
public enum DayKindEnum {

	OFF("放假"),
	WORK("调休补班");

	private final String label;

	DayKindEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return name();
	}
}
