/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.model;

/**
 * 状态码。
 *
 * @author 元悉宇宙
 * @date 2022/10/17
 * @version 1.0
 */
public class State {
	/** 唯一随机码 */
	private String uuid;

	/** 字面随机数 */
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
