/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 地区级别枚举。
 *
 * @Title RegionLevelEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum RegionLevelEnum {

	/** 省级。*/
	prov("1"),
	/** 市级 */
	city("2"),
	/** 区县 */
	county("3"),
	/** 乡镇、街道 */
	town("4"),
	/** 村、社区、居委会 */
	village("5"),
	/** 网格 */
	grid("6");

	private String regionName;

	RegionLevelEnum(String regionName) {
		this.regionName = regionName;
	}

	@Override
	public String toString() {
		return this.regionName;
	}
}
