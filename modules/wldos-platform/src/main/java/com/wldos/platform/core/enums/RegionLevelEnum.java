/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

/**
 * 地区级别枚举。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public enum RegionLevelEnum {

	/** 省级。*/
	PROV("1"),
	/** 市级 */
	CITY("2"),
	/** 区县 */
	COUNTY("3"),
	/** 乡镇、街道 */
	TOWN("4"),
	/** 村、社区、居委会 */
	VILLAGE("5"),
	/** 网格 */
	GRID("6");

	private final String regionName;

	RegionLevelEnum(String regionName) {
		this.regionName = regionName;
	}

	@Override
	public String toString() {
		return this.regionName;
	}
}