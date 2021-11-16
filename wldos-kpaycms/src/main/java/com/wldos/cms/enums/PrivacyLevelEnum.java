

package com.wldos.cms.enums;


public enum PrivacyLevelEnum {

	PUB("公开查看", "public"),

	TOKEN("查看码可见", "token"),

	REWARD("打赏可见", "reward");

	private final String label;
	private final String value;

	PrivacyLevelEnum(String label, String value) {
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
