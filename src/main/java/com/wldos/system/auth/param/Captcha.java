/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.auth.param;

import java.io.Serializable;

/**
 * 验证码验证参数。
 *
 * @Title Captcha
 * @Package com.wldos.system.auth.param
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/4
 * @Version 1.0
 */
public class Captcha implements Serializable {
	private String captcha;
	private String uuid;
	private String status;

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
