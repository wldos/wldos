/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 行政区划搜索 DAO 投影行。
 */
@Getter
@Setter
public class RegionSearchRow {

	private Long id;

	private String name;

	private String level;

	private Long parentId;
}
