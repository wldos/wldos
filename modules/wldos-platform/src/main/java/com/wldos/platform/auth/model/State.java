/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 状态码。
 *
 * @author 元悉宇宙
 * @date 2022/10/17
 * @version 1.0
 */
@ApiModel(description = "状态码")
@Getter
@Setter
public class State {
	@ApiModelProperty(value = "唯一随机码", example = "550e8400-e29b-41d4-a716-446655440000")
	private String uuid;

	@ApiModelProperty(value = "字面随机数", example = "abc123")
	private String text;

	public State() {
	}

	public static State of(String uid, String text) {
		return new State(uid, text);
	}

	private State(String uid, String text) {
		this.uuid = uid;
		this.text = text;
	}
}
