/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.support.plugins.vo;

/**
 * 插件vo。
 *
 * @author 元悉宇宙
 * @date 2023/4/23
 * @version 1.0
 */
public class PluginVO {
	private Long id;

	private String pluginName;

	private String pluginUrl;

	private String bootClass;

	private String scanPath;

	private String description;

	private String installInfo;

	private String versionHistory;

	private String previewPic;

	private String config;

	private String status;

	private Long starCount;

	private Long likeCount;

	private Long downCount;

	private Long displayOrder;

	private String isValid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getPluginUrl() {
		return pluginUrl;
	}

	public void setPluginUrl(String pluginUrl) {
		this.pluginUrl = pluginUrl;
	}

	public String getBootClass() {
		return bootClass;
	}

	public void setBootClass(String bootClass) {
		this.bootClass = bootClass;
	}

	public String getScanPath() {
		return scanPath;
	}

	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInstallInfo() {
		return installInfo;
	}

	public void setInstallInfo(String installInfo) {
		this.installInfo = installInfo;
	}

	public String getVersionHistory() {
		return versionHistory;
	}

	public void setVersionHistory(String versionHistory) {
		this.versionHistory = versionHistory;
	}

	public String getPreviewPic() {
		return previewPic;
	}

	public void setPreviewPic(String previewPic) {
		this.previewPic = previewPic;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getStarCount() {
		return starCount;
	}

	public void setStarCount(Long starCount) {
		this.starCount = starCount;
	}

	public Long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}

	public Long getDownCount() {
		return downCount;
	}

	public void setDownCount(Long downCount) {
		this.downCount = downCount;
	}

	public Long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getIsValid() {
		return isValid;
	}

	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}
}