/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 发布类型扩展属性。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
@Getter
@Setter
public class PubTypeExt {

	private Long id;

	private Long pubId;

	private Long metaId;

	private String metaKey;

	private String metaName;

	private String metaValue;

	private String metaDesc;

	private String dataType;

	private String enumValue;

	private String pubType;

	public PubTypeExt() {
	}

	public static PubTypeExt of(String metaKey, String metaValue) {
		return new PubTypeExt(metaKey, metaValue);
	}

	private PubTypeExt(String metaKey, String metaValue) {
		this.metaKey = metaKey;
		this.metaValue = metaValue;
	}
}
