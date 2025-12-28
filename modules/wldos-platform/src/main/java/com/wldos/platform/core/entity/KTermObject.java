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

@Table
@Getter
@Setter
public class KTermObject {
	@Id
	private Long id;

	private Long objectId;

	private Long termTypeId;

	private Long termOrder;

	public KTermObject() {
	}

	public static KTermObject of(long id, Long termTypeId, Long objectId, Long termOrder) {
		return new KTermObject(id, termTypeId, objectId, termOrder);
	}

	private KTermObject(long id, Long termTypeId, Long objectId, Long termOrder) {
		this.id = id;
		this.termTypeId = termTypeId;
		this.objectId = objectId;
		this.termOrder = termOrder;
	}
}
