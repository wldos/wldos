/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.base.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getTermTypeId() {
		return termTypeId;
	}

	public void setTermTypeId(Long termTypeId) {
		this.termTypeId = termTypeId;
	}

	public Long getTermOrder() {
		return termOrder;
	}

	public void setTermOrder(Long termOrder) {
		this.termOrder = termOrder;
	}
}
