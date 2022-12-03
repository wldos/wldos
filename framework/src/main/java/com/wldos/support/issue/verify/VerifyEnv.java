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
 * license环境变量（用于读入系统配置的授权信息，系统加载时与license真实信息比对，然后作为授权信息展示的依据，
 * 不直接取license是为了不频繁调用敏感模块以增加软件的不可破解性）。
 *
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
@Component
public class VerifyEnv {
	/* 以下信息应该封装在发行版jar中和license中，保证实际产品与部署的license一致 */
	/** 许可主域名 */
	@Value("${wldos.platform.domain:wldos.com}")
	private String domain;
	/** 组织单位名 */
	@Value("${license.orgName:wldos.com}")
	private String orgName;
	/** 产品名称 */
	@Value("${license.prodName:WLDOS平台}")
	private String prodName;
	/** 产品版本 */
	@Value("${wldos.edition:"+ IssueConstants.DEFAULT_VERSION+"}")
	private String edition;
	/** 产品版本号 */
	@Value("${wldos.version:v1.0.0}")
	private String version;

	/** 到期时间 */
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