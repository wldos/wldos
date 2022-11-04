/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wldos.support.issue.IssueConstants;
import com.wldos.support.issue.verify.VerifyEnv;
import com.wldos.support.issue.vo.VerifyInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * license信息。
 *
 * @author 树悉猿
 * @date 2022/10/06
 * @version 1.0
 */
public class Lic {
	/** 许可主域名 */
	private String domain;
	/** 组织单位名 */
	private String orgName;
	/** 产品名称 */
	private String prodName;
	/** 产品版本 */
	private String edition;
	/** 产品版本号 */
	private String version;
	/** 到期时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date expiryTime;

	public Lic() {}

	public static Lic of(VerifyEnv verifyEnv) {
		return new Lic(verifyEnv.getDomain(), verifyEnv.getOrgName(), verifyEnv.getProdName(),
				verifyEnv.getEdition(), verifyEnv.getVersion(), verifyEnv.getExpiryTime());
	}

	public Lic(String domain, String orgName, String prodName, String edition, String version, Date expiryTime) {
		this.domain = domain;
		this.orgName = orgName;
		this.prodName = prodName;
		this.edition = edition;
		this.version = version;
		this.expiryTime = expiryTime;
	}

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

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}
}