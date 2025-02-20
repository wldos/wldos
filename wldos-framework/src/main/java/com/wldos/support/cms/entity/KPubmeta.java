/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.support.cms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
public class KPubmeta {
	@Id
	private Long id;

	private Long pubId;

	private String metaKey;

	private String metaValue;

	public KPubmeta() {
	}

	public static KPubmeta of(Long id, Long pubId, String metaKey, String metaValue) {
		return new KPubmeta(id, pubId, metaKey, metaValue);
	}

	private KPubmeta(Long id, Long pubId, String metaKey, String metaValue) {
		this.id = id;
		this.pubId = pubId;
		this.metaKey = metaKey;
		this.metaValue = metaValue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPubId() {
		return pubId;
	}

	public void setPubId(Long pubId) {
		this.pubId = pubId;
	}

	public String getMetaKey() {
		return metaKey;
	}

	public void setMetaKey(String metaKey) {
		this.metaKey = metaKey;
	}

	public String getMetaValue() {
		return metaValue;
	}

	public void setMetaValue(String metaValue) {
		this.metaValue = metaValue;
	}
}
