/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.vo;

import java.io.Serializable;

/**
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
public class IssueVerifyParam implements Serializable {

	private String subject;

	private String publicAlias;

	private String storePass;

	private String licensePath;

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