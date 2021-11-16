

package com.wldos.cms.enums;


public enum PostStatusEnum {

	PUBLISH("publish"),

	INHERIT("inherit"),

	IN_REVIEW("in_review"),

	OFFLINE("offline"),

	INITIAL("initial"),

	DRAFT("draft"),

	AUTO_DRAFT("auto_draft"),

	TRASH("trash");

	private final String name;

	PostStatusEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
