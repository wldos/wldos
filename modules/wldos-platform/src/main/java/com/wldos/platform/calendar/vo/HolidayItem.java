/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 节假日条目（管理端 / 客户端共用 VO）。
 *
 * <p>给客户端用 yyyy-MM-dd 字符串而非 Date，避免时区与序列化坑；
 * 管理端表单同样用此 VO，提交时按 day-precision 解析回 Date。
 *
 * @author 元悉宇宙
 * @date 2026/04/30
 * @version 1.0
 */
@ApiModel("节假日条目")
@Getter
@Setter
public class HolidayItem {

	@ApiModelProperty(value = "id（编辑时必填，新增不传）", example = "100001")
	private String id;

	@ApiModelProperty(value = "日期 yyyy-MM-dd", example = "2026-10-01", required = true)
	private String date;

	@ApiModelProperty(value = "年份（新增可不传，由后端按 date 解析）", example = "2026")
	private Integer year;

	@ApiModelProperty(value = "单日类型 OFF=放假 / WORK=调休补班", example = "OFF", required = true)
	private String dayKind;

	@ApiModelProperty(value = "节日名", example = "国庆节")
	private String name;

	@ApiModelProperty(value = "来源 MANUAL / SYNC / IMPORT", example = "MANUAL")
	private String source;

	@ApiModelProperty("备注")
	private String remark;
}
