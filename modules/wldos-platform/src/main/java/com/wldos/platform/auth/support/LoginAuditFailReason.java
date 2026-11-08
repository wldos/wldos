/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.auth.support;

/**
 * 登录失败审计原因（仅写入 {@code wo_login_log.fail_reason}，不返回前端）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public enum LoginAuditFailReason {

	/** 登录名在库中不存在。 */
	ACCOUNT_NOT_FOUND("账号不存在"),

	/** 账号存在但密码校验未通过。 */
	BAD_PASSWORD("密码错误"),

	/** RSA 解密失败，多为公钥不一致或客户端未按约定加密。 */
	PASSWORD_DECRYPT_FAILED("密码解密失败（平台密钥或客户端加密异常）");

	private final String logText;

	LoginAuditFailReason(String logText) {
		this.logText = logText;
	}

	public String getLogText() {
		return logText;
	}
}
