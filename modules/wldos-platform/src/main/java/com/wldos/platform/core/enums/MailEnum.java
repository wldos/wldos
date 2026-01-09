/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

/**
 * 邮件状态枚举。
 *
 * @author 元悉宇宙
 * @date 2022/8/27
 * @version 1.0
 */
public enum MailEnum {

	/** 成功。*/
	SUCCESS("成功", "1"),
	/** 失败 */
	FAILURE("失败", "0"),
	/** 发送中 */
	SENDING("发送中", "2");

	private final String label;

	private final String value;

	MailEnum(String label, String value) {
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
		return "{label: '" + this.label + "', value: '" + this.value + "'}";
	}
}
