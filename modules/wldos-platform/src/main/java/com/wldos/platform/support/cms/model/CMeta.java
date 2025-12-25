/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.model;

import java.util.List;

import com.wldos.platform.support.cms.dto.PubTypeExt;

/**
 * Pub元模型定义。
 *
 * @author 元悉宇宙
 * @date 2021/6/19
 * @version 1.0
 */
public class CMeta {

	/** 封面特色图path */
	protected String cover;

	/** 查看数 */
	protected String views;

	protected String watermarkText;

	protected String watermarkEnabled;

	protected List<PubTypeExt> pubTypeExt;

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public List<PubTypeExt> getPubTypeExt() {
		return pubTypeExt;
	}

	public void setPubTypeExt(List<PubTypeExt> pubTypeExt) {
		this.pubTypeExt = pubTypeExt;
	}

	public String getWatermarkText() {
		return watermarkText;
	}

	public void setWatermarkText(String watermarkText) {
		this.watermarkText = watermarkText;
	}

	public String getWatermarkEnabled() {
		return watermarkEnabled;
	}

	public void setWatermarkEnabled(String watermarkEnabled) {
		this.watermarkEnabled = watermarkEnabled;
	}
}
