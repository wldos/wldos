/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.enums;

/**
 * 节假日数据来源。
 *
 * <ul>
 *   <li>MANUAL：管理员手工维护（最高优先级，外部同步不会覆盖 MANUAL 条目）；</li>
 *   <li>SYNC：定时任务从外部 API 同步而来；</li>
 *   <li>IMPORT：批量导入（含首次启动 seed 兜底数据）。</li>
 * </ul>
 *
 * @author 元悉宇宙
 * @date 2026/04/30
 * @version 1.0
 */
public enum HolidaySourceEnum {

	MANUAL("手工维护"),
	SYNC("外部同步"),
	IMPORT("批量导入");

	private final String label;

	HolidaySourceEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return name();
	}
}
