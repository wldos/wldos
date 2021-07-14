/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 资源相关枚举值。
 *
 * @Title ResourceEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum ResourceEnum {
	/**	菜单类 */
	menu("menu"),
	/** 操作类 */
	button("button"),
	/** 接口服务 */
	webServ("webServ"),
	/** 数据服务 */
	dataServ("dataServ"),
	/** 静态资源 */
	_static("_static"),
	/** 管理菜单 */
	adminMenu("admin_menu"),
	other("other");

	private String resName;

	ResourceEnum(String resName) {
		this.resName = resName;
	}

	@Override
	public String toString() {
		return this.resName;
	}
}
