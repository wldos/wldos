/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.vo;

import java.io.Serializable;
import java.util.List;

import com.wldos.support.issue.verify.VerifyEnv;

/**
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
public class VerifyInfo implements Serializable {

	private List<String> ipAddress;

	private List<String> macAddress;

	private String cpuSerial;

	private String mainBoardSerial;

	private String domain;

	private String orgName;

	private String prodName;

	private String edition;

	private String issuer = "wldos.com";

	private String version;

	public VerifyInfo() {
	}

	private VerifyInfo(List<String> ipAddress, List<String> macAddress, String cpuSerial, String mainBoardSerial,
			String domain, String orgName, String prodName, String edition, String version) {
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.cpuSerial = cpuSerial;
		this.mainBoardSerial = mainBoardSerial;
		this.domain = domain;
		this.orgName = orgName;
		this.prodName = prodName;
		this.edition = edition;
		this.version = version;
	}

	private VerifyInfo(String domain, String orgName, String prodName, String edition) {
		this.domain = domain;
		this.orgName = orgName;
		this.prodName = prodName;
		this.edition = edition;
	}

	private VerifyInfo(String domain, String orgName, String prodName, String edition, String version) {
		this.domain = domain;
		this.orgName = orgName;
		this.prodName = prodName;
		this.edition = edition;
		this.version = version;
	}

	public static VerifyInfo of(VerifyEnv verifyEnv) {
		return new VerifyInfo(verifyEnv.getDomain(), verifyEnv.getOrgName(), verifyEnv.getProdName(),
				verifyEnv.getEdition(), verifyEnv.getVersion());
	}

	public static VerifyInfo of(String domain, String orgName, String prodName, String edition, String version) {
		return new VerifyInfo(domain, orgName, prodName, edition, version);
	}

	public static VerifyInfo of(List<String> ipAddress, List<String> macAddress, String cpuSerial, String mainBoardSerial,
			String domain, String orgName, String prodName, String edition, String version) {
		return new VerifyInfo(ipAddress, macAddress, cpuSerial, mainBoardSerial, domain, orgName, prodName, edition, version);
	}

	public static VerifyInfo of(List<String> ipAddress, List<String> macAddress, String cpuSerial, String mainBoardSerial, VerifyEnv verifyEnv) {
		return new VerifyInfo(ipAddress, macAddress, cpuSerial, mainBoardSerial, verifyEnv.getDomain(),
				verifyEnv.getOrgName(), verifyEnv.getProdName(), verifyEnv.getEdition(), verifyEnv.getVersion());
	}

	public List<String> getIpAddress() {
		return ipAddress;
	}

	public List<String> getMacAddress() {
		return macAddress;
	}

	public String getCpuSerial() {
		return cpuSerial;
	}

	public String getMainBoardSerial() {
		return mainBoardSerial;
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

	public void setIpAddress(List<String> ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setMacAddress(List<String> macAddress) {
		this.macAddress = macAddress;
	}

	public void setCpuSerial(String cpuSerial) {
		this.cpuSerial = cpuSerial;
	}

	public void setMainBoardSerial(String mainBoardSerial) {
		this.mainBoardSerial = mainBoardSerial;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "VerifyInfo: {" +
				"ipAddress=" + ipAddress +
				", macAddress=" + macAddress +
				", cpuSerial='" + cpuSerial + '\'' +
				", mainBoardSerial='" + mainBoardSerial + '\'' +
				", domain='" + domain + '\'' +
				", orgName='" + orgName + '\'' +
				", prodName='" + prodName + '\'' +
				", edition='" + edition + '\'' +
				", version='" + version + '\'' +
				", issuer='" + issuer + '\'' +
				'}';
	}
}