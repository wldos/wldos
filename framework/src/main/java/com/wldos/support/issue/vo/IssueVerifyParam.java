/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.vo;

import java.io.Serializable;

/**
 * license验证参数。
 *
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
public class IssueVerifyParam implements Serializable {
	/**
	 * 证书subject
	 */
	private String subject;

	/**
	 * 公钥别称
	 */
	private String publicAlias;

	/**
	 * 访问公钥库的密码
	 */
	private String storePass;

	/**
	 * 证书生成路径
	 */
	private String licensePath;

	/**
	 * 密钥库存储路径
	 */
	private String publicKeysStorePath;

	public IssueVerifyParam() {
	}

	public IssueVerifyParam(String subject, String publicAlias, String storePass, String licensePath, String publicKeysStorePath) {
		this.subject = subject;
		this.publicAlias = publicAlias;
		this.storePass = storePass;
		this.licensePath = licensePath;
		this.publicKeysStorePath = publicKeysStorePath;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPublicAlias() {
		return publicAlias;
	}

	public void setPublicAlias(String publicAlias) {
		this.publicAlias = publicAlias;
	}

	public String getStorePass() {
		return storePass;
	}

	public void setStorePass(String storePass) {
		this.storePass = storePass;
	}

	public String getLicensePath() {
		return licensePath;
	}

	public void setLicensePath(String licensePath) {
		this.licensePath = licensePath;
	}

	public String getPublicKeysStorePath() {
		return publicKeysStorePath;
	}

	public void setPublicKeysStorePath(String publicKeysStorePath) {
		this.publicKeysStorePath = publicKeysStorePath;
	}
}

