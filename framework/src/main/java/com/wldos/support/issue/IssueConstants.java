/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue;

/**
 * license相关常量。
 *
 * @author 树悉猿
 * @date 2022/1/26
 * @version 1.0
 */
public class IssueConstants {
	/** 默认版本 */
	public static final String DEFAULT_VERSION = "Free";
	/** wldos license主题必须是这个，否则作无效 */
	public static final String SUBJECT_WLDOS = "wldos-user";
	/** WLDOS版本参数key */
	public static final String Key_PROPERTY_WLDOS_EDITION = "wldos.edition";
	/** 被授权组织参数key */
	public static final String Key_PROPERTY_LICENSE_ORG = "license.orgName";
	/** 被授权产品参数key */
	public static final String Key_PROPERTY_LICENSE_PROD = "license.prodName";
}
