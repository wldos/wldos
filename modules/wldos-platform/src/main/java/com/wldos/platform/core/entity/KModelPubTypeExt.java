/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
@Table
@Getter
@Setter
public class KModelPubTypeExt {

	@Id
	private Long id;

	private String metaKey;

	private String metaName;

	private String metaDesc;

	private String dataType;

	private String enumValue;

	private String pubType;
}