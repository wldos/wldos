/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * CMS 检索索引全量重建结果摘要。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("CMS 检索重建摘要")
public class CmsSearchRebuildSummary {

	@ApiModelProperty("参与重建的正式作品行数")
	private int canonicalCount;

	@ApiModelProperty("cms-public 全文文档数")
	private int publicTextCount;

	@ApiModelProperty("cms-internal 全文文档数")
	private int internalTextCount;

	@ApiModelProperty("尝试向量 upsert 次数")
	private int vectorUpsertAttempts;

	public int getCanonicalCount() {
		return canonicalCount;
	}

	public void setCanonicalCount(int canonicalCount) {
		this.canonicalCount = canonicalCount;
	}

	public int getPublicTextCount() {
		return publicTextCount;
	}

	public void setPublicTextCount(int publicTextCount) {
		this.publicTextCount = publicTextCount;
	}

	public int getInternalTextCount() {
		return internalTextCount;
	}

	public void setInternalTextCount(int internalTextCount) {
		this.internalTextCount = internalTextCount;
	}

	public int getVectorUpsertAttempts() {
		return vectorUpsertAttempts;
	}

	public void setVectorUpsertAttempts(int vectorUpsertAttempts) {
		this.vectorUpsertAttempts = vectorUpsertAttempts;
	}
}
