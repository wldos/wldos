/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.model;

/**
 * 内容附件：图片、文件、音频、视频等。
 *
 * @author 树悉猿
 * @date 2021/6/19
 * @version 1.0
 */
public class Attachment {
	/** 在图片服务器上的物理存储路径 */
	protected String attachPath;

	/** 缩略图渲染样式、图片元信息等用户设置 */
	protected String attachMetadata;

	/** 附件alt */
	protected String attachFileAlt;

	private String url; // 用户访问附件的web url

	public Attachment() {}

	public static Attachment of(String attachPath, String attachMetadata, String attachFileAlt) {
		return new Attachment(attachPath, attachMetadata, attachFileAlt);
	}

	private Attachment(String attachPath, String attachMetadata, String attachFileAlt) {
		this.attachPath = attachPath;
		this.attachMetadata = attachMetadata;
		this.attachFileAlt = attachFileAlt;
	}

	public String getAttachPath() {
		return attachPath;
	}

	public void setAttachPath(String attachPath) {
		this.attachPath = attachPath;
	}

	public String getAttachMetadata() {
		return attachMetadata;
	}

	public void setAttachMetadata(String attachMetadata) {
		this.attachMetadata = attachMetadata;
	}

	public String getAttachFileAlt() {
		return attachFileAlt;
	}

	public void setAttachFileAlt(String attachFileAlt) {
		this.attachFileAlt = attachFileAlt;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
