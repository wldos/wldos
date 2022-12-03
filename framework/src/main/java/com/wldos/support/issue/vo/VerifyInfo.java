/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
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
 * license绑定和验证的硬件信息。请不要修改VerifyInfo的结构，否则license安装时反序列化失败，导致license安装失败，系统无法启动!!!
 *
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
public class VerifyInfo implements Serializable {

	/**
	 * 允许的IP地址, 支持集群
	 */
	private List<String> ipAddress;

	/**
	 * 允许的MAC地址, 支持集群
	 */
	private List<String> macAddress;

	/**
	 * 允许的CPU序列号
	 */
	private String cpuSerial;

	/**
	 * 允许的主板序列号
	 */
	private String mainBoardSerial;

	/** 被授权的主域名 */
	private String domain;
	/** 组织单位名 */
	private String orgName;
	/** 产品名称 */
	private String prodName;
	/** 产品版本 */
	private String edition;
	/** 发行者 */
	private String issuer = "wldos.com";
	/** 内部版本号 */
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

	/**
	 * 附加软件信息（外网部署用）
	 *
	 * @param verifyEnv 验证环境
	 * @return 验证信息
	 */
	public static VerifyInfo of(VerifyEnv verifyEnv) {
		return new VerifyInfo(verifyEnv.getDomain(), verifyEnv.getOrgName(), verifyEnv.getProdName(),
				verifyEnv.getEdition(), verifyEnv.getVersion());
	}

	/**
	 * 附加软件信息（外网部署用）
	 *
	 * @param domain 被授权域名
	 * @param orgName 被授权组织名称
	 * @param prodName 授权产品名称
	 * @param edition 软件版本，见#IssueVersionEnum
	 * @param version 内部版本号
	 * @return 验证信息
	 */
	public static VerifyInfo of(String domain, String orgName, String prodName, String edition, String version) {
		return new VerifyInfo(domain, orgName, prodName, edition, version);
	}

	/**
	 * 硬件信息和软件信息（内网部署用）
	 *
	 * @param ipAddress ip地址
	 * @param macAddress mac地址
	 * @param cpuSerial cpu串号
	 * @param mainBoardSerial 主板序列号
	 * @param domain 被授权域名
	 * @param orgName 被授权组织名称
	 * @param prodName 授权产品名称
	 * @param edition 授权产品版本
	 * @param version 内部版本号
	 * @return 验证信息
	 */
	public static VerifyInfo of(List<String> ipAddress, List<String> macAddress, String cpuSerial, String mainBoardSerial,
			String domain, String orgName, String prodName, String edition, String version) {
		return new VerifyInfo(ipAddress, macAddress, cpuSerial, mainBoardSerial, domain, orgName, prodName, edition, version);
	}

	/**
	 * 硬件信息和软件信息（内网部署用）
	 *
	 * @param ipAddress ip地址
	 * @param macAddress mac地址
	 * @param cpuSerial cpu串号
	 * @param mainBoardSerial 主板序列号
	 * @param verifyEnv 服务器验证环境信息，用于标明被授权方商业信息
	 * @return 验证信息
	 */
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
