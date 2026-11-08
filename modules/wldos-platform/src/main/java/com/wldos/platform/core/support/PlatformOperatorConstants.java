/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.support;

/**
 * 平台官方运营主体（internal tenant）识别常量。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class PlatformOperatorConstants {

	public static final String COM_CODE = "platform-operator";

	/** 绿库初始化预留主键，便于跨环境割接与手工 SQL 对齐。 */
	public static final long RESERVED_COM_ID = 1L;

	public static final String DEFAULT_COM_NAME = "平台官方运营主体";

	private PlatformOperatorConstants() {
	}
}
