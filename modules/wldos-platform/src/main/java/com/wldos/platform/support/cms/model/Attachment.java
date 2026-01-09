/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 内容附件：图片、文件、音频、视频等。
 *
 * @author 元悉宇宙
 * @date 2021/6/19
 * @version 1.0
 */
@ApiModel(description = "内容附件：图片、文件、音频、视频等")
@Getter
@Setter
public class Attachment {
	@ApiModelProperty(value = "在图片服务器上的物理存储路径", example = "/2024/12/image.jpg")
	protected String attachPath;

	@ApiModelProperty(value = "缩略图渲染样式、图片元信息等用户设置", example = "{\"width\":800,\"height\":600}")
	protected String attachMetadata;

	@ApiModelProperty(value = "附件alt文本", example = "示例图片")
	protected String attachFileAlt;

	@ApiModelProperty(value = "用户访问附件的Web URL", example = "https://example.com/images/image.jpg")
	private String url;

	public Attachment() {
	}

	public static Attachment of(String attachPath, String attachMetadata, String attachFileAlt) {
		return new Attachment(attachPath, attachMetadata, attachFileAlt);
	}

	private Attachment(String attachPath, String attachMetadata, String attachFileAlt) {
		this.attachPath = attachPath;
		this.attachMetadata = attachMetadata;
		this.attachFileAlt = attachFileAlt;
	}
}
