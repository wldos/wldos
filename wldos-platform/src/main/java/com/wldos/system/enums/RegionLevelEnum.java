/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 地区级别枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum RegionLevelEnum {
	
	PROV("1"),
	
	CITY("2"),
	
	COUNTY("3"),
	
	TOWN("4"),
	
	VILLAGE("5"),
	
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
