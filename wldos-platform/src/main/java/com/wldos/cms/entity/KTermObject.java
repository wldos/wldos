/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
public class KTermObject {
	@Id
	private Long id;

	private Long objectId;

	private Long termTypeId;

	private Long termOrder;

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
