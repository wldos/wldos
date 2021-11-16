

package com.wldos.cms.model;


public class Attachment {

	protected String attachPath;

	protected String attachMetadata;

	protected String attachFileAlt;

	private String url;

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
