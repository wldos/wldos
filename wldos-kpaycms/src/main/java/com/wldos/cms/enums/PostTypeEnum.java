

package com.wldos.cms.enums;


public enum PostTypeEnum {

	POST("post"),

	PAGE("page"),

	BOOK("book"),

	CHAPTER("chapter"),

	ATTACHMENT("attachment"),

	NAV_ITEM_MENU("navItemMenu");

	private final String name;

	PostTypeEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
