/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common;

/**
 * 全局常量类。
 *
 * @author 树悉猿
 * @date 2021-03-23
 * @version V1.0
 */
public final class Constants {
	/** 生成树根虚节点 */
	public static final long TOP_VIR_ID = 11L;

	/** 顶级组织ID，即平台组织ID */
	public static final long TOP_ORG_ID = 0L;

	/** 顶级体系ID，即平台体系结构ID */
	public static final long TOP_ARCH_ID = 0L;

	/** 顶级公司ID，即平台主体ID */
	public static final long TOP_COM_ID = 0L;

	/** 顶级公司编码，即平台主体编码 */
	public static final String TOP_COM_CODE = "platform";

	/** 顶级公司名称，即平台主体名称 */
	public static final String TOP_COM_NAME = "平台";

	/** 游客ID约定为0 */
	public static final Long GUEST_ID = 0L;

	/** 菜单根节点ID即资源根节点 */
	public static final long MENU_ROOT_ID = 0L;

	public static final long TOP_ROLE_ID = 0L;

	/** 顶级行政区划ID，即中国 */
	public static final long TOP_REGION_ID = 0L;

	/** 顶级分类ID，即无 */
	public static final long TOP_TERM_ID = 0L;

	/** 顶级评论ID */
	public static final long TOP_COMMENT_ID = 0L;

	/** 顶级发布ID */
	public static final long TOP_PUB_ID = 0L;

	/** 系统用户ID,用于初始化系统基础数据或者创建新用户时充当操作人 */
	public static final long SYSTEM_USER_ID = 0L;

	/** 枚举值：平台专用应用类型 */
	public static final String ENUM_TYPE_APP_PLAT = "platform";

	/** 枚举值：平台专用系统用户组类型 */
	public static final String ENUM_TYPE_ORG_PLAT = "platform";

	/** 枚举值：平台专用系统体系结构类型 */
	public static final String ENUM_TYPE_ARCH_PLAT = "platform";

	/** 用户token异常 */
	public static final Integer EX_USER_INVALID_CODE = 401;

	public static final Integer EX_USER_PASS_INVALID_CODE = 400;

	public static final int TOKEN_FORBIDDEN_CODE = 403;

	public static final Integer EX_OTHER_CODE = 500;

	/** 上下文参数 */
	public static final String CONTEXT_KEY_USER = "curUser";

	public static final String CONTEXT_KEY_USER_ID = "curUserId";

	public static final String CONTEXT_KEY_USER_NAME = "curUserName";

	/** 当前用户归属的租户（主企业）id，游客归属平台自身 */
	public static final String CONTEXT_KEY_USER_TENANT = "tenantId";

	/** 当前请求的域id */
	public static final String CONTEXT_KEY_USER_DOMAIN = "domainId";

	public static final String CONTEXT_KEY_TOKEN_EXPIRE_TIME = "tokenExp";

	/** 租户字段 */
	public static final String COMMON_KEY_TENANT_COLUMN = "comId";

	/** 域字段 */
	public static final String COMMON_KEY_DOMAIN_COLUMN = "domainId";

	/** 应用类型 */
	public static final String COMMON_KEY_APP_TYPE = "appType";

	/** 平台根域名key */
	public static final String COMMON_KEY_PLATFORM_DOMAIN = "platDomain";

	/** 平台超级管理员用户组 */
	public static final String AdminOrgCode = "admin";

	/** 平台租户管理员用户组 */
	public static final String TAdminOrgCode = "tadmin";

	/** 可信者用户组 */
	public static final String CanTrustOrgCode = "can_trust";

	/** 租户(公司)实体bean名称 */
	public static final String CLASS_NAME_COMPANY = "WoCompany";

	/** 枚举类型-角色-游客 */
	public static final String ENUM_TYPE_ROLE_GUEST = "guest";

	/** OAuth2.0社会化登录配置项前缀 */
	public static final String OAUTH_SETTINGS_PREFIX = "oauth_login_";

	public static final String DIRECTORY_TEMP_NAME = "temp";
}