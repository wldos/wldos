/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util.constant;

/**
 * 通用常量类
 */
public final class PubConstants {
	/** 顶级组织ID，即平台组织ID */
	public static final long TOP_ORG_ID = 0l;

	/** 顶级体系ID，即平台体系结构ID */
	public static final long TOP_ARCH_ID = 0l;

	/** 顶级公司ID，即平台主体ID */
	public static final long TOP_COM_ID = 0l;

	/** 游客ID约定为0 */
	public static final Long GUEST_ID = 0l;

	/** 菜单根节点ID即资源根节点 */
	public static final long MENU_ROOT_ID = 0l;

	public static final long TOP_ROLE_ID = 0l;

	/** 顶级行政区划ID，即中国 */
	public static final long TOP_REGION_ID = 0l;

	/** 系统用户ID,用于初始化系统基础数据或者创建新用户时充当操作人 */
	public static final long SYSTEM_USER_ID = 0l;

	/** 用户token异常 */
	public static final Integer EX_USER_INVALID_CODE = 401;

	public static final Integer EX_USER_PASS_INVALID_CODE = 400;

	public static final int TOKEN_FORBIDDEN_CODE = 403;


	public static final Integer EX_OTHER_CODE = 500;

	public static final String CONTEXT_KEY_USER = "curUser";

	public static final String CONTEXT_KEY_USER_ID = "curUserId";

	public static final String CONTEXT_KEY_USER_NAME = "curUserName";

	public static final String CONTEXT_KEY_USER_TENANT = "tenantId";
	/** 租户字段 */
	public static final String COMMON_KEY_TENANT_COLUMN = "comId";

	/** 平台超级管理员角色组 */
	public static final String AdminOrgCode = "admin";
}
