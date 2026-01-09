/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 作品详情主图。
 *
 * @author 元悉宇宙
 * @date 2021/7/7
 * @version 1.0
 */
@ApiModel(description = "作品详情主图")
@JsonIgnoreProperties({ "noMainPicPath" })
public class MainPicture {
	@ApiModelProperty(value = "主图键值", example = "mainPic1")
	private String key;

	@ApiModelProperty(value = "主图URL", example = "https://example.com/images/main.jpg")
	private String url;

	/** 无主图占位图路径 */
	public static final String noMainPicPath = "/noPic.jpg";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
