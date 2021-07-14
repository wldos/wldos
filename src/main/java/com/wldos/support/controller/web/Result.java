/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.controller.web;

import java.io.Serializable;

/**
 * 基础响应模板。
 *
 * @Title Result
 * @Package com.wldos.support.controller.web
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/29
 * @Version 1.0
 */
public class Result implements Serializable {

	private int status = 200;

	private String message;

	public Result() {}

	public Result(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
