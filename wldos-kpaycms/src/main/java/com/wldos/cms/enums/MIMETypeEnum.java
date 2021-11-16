

package com.wldos.cms.enums;


public enum MIMETypeEnum {

	IMAGE("图片", "image"),

	TEXT("文本", "text"),

	VIDEO("视频", "video"),

	AUDIO("音频", "audio"),

	APPLICATION("应用程序", "application");

	private final String label;
	private final String value;

	MIMETypeEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{label: '"+this.label+"', value: '"+this.value+"'}";
	}
}
