/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.verify;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wldos.support.issue.IssueConstants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
@Component
public class VerifyEnv {
	@Value("${wldos.platform.domain:wldos.com}")
	private String domain;
	@Value("${license.orgName:wldos.com}")
	private String orgName;
	@Value("${license.prodName:WLDOS平台}")
	private String prodName;
	@Value("${wldos.edition:"+ IssueConstants.DEFAULT_VERSION+"}")
	private String edition;
	@Value("${wldos.version:v1.0.0}")
	private String version;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date expiryTime;

	public String getDomain() {
		return domain;
	}

	public String getOrgName() {
		return orgName;
	}

	public String getProdName() {
		return prodName;
	}

	public String getEdition() {
		return edition;
	}

	public String getVersion() {
		return version;
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}
}