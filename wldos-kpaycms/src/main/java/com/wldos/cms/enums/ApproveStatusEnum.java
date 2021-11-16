

package com.wldos.cms.enums;


public enum ApproveStatusEnum {
	
	APPROVING("待审批", "0"),
	
	APPROVED("审批通过", "1"),
	
	SPAM("垃圾信息", "spam"),
	
	TRASH("回收站", "trash");

	private final String label;
	private final String value;

	ApproveStatusEnum(String label, String value) {
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
