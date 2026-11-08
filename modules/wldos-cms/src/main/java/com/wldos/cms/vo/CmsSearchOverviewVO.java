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
 * CMS 检索与平台 search 配置只读概览。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("CMS 检索配置概览")
public class CmsSearchOverviewVO {

	@ApiModelProperty("wldos.index.provider")
	private String fulltextProvider;

	@ApiModelProperty("wldos.search.vector.enabled")
	private boolean vectorEnabled;

	@ApiModelProperty("wldos.vector.provider")
	private String vectorProvider;

	@ApiModelProperty("CMS 全文 namespace：cms-public / cms-internal")
	private String cmsTextNamespaces;

	@ApiModelProperty("混合检索 RRF 是否启用")
	private boolean hybridRrfEnabled;

	public String getFulltextProvider() {
		return fulltextProvider;
	}

	public void setFulltextProvider(String fulltextProvider) {
		this.fulltextProvider = fulltextProvider;
	}

	public boolean isVectorEnabled() {
		return vectorEnabled;
	}

	public void setVectorEnabled(boolean vectorEnabled) {
		this.vectorEnabled = vectorEnabled;
	}

	public String getVectorProvider() {
		return vectorProvider;
	}

	public void setVectorProvider(String vectorProvider) {
		this.vectorProvider = vectorProvider;
	}

	public String getCmsTextNamespaces() {
		return cmsTextNamespaces;
	}

	public void setCmsTextNamespaces(String cmsTextNamespaces) {
		this.cmsTextNamespaces = cmsTextNamespaces;
	}

	public boolean isHybridRrfEnabled() {
		return hybridRrfEnabled;
	}

	public void setHybridRrfEnabled(boolean hybridRrfEnabled) {
		this.hybridRrfEnabled = hybridRrfEnabled;
	}
}
