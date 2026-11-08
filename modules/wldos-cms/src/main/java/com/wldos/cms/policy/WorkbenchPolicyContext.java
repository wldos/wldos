/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.policy;

/**
 * 工作台策略判定上下文（Hook 入参 args[0]；Handler 通过 mergeLimit / deny 参与聚合）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public class WorkbenchPolicyContext {

	private String action;

	private Long subjectUserId;

	private Long domainId;

	private Long comId;

	private String pubType;

	private Long parentResourceId;

	private long currentUsage;

	private Integer effectiveMax;

	private String denyReason;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getSubjectUserId() {
		return subjectUserId;
	}

	public void setSubjectUserId(Long subjectUserId) {
		this.subjectUserId = subjectUserId;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Long getComId() {
		return comId;
	}

	public void setComId(Long comId) {
		this.comId = comId;
	}

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}

	public Long getParentResourceId() {
		return parentResourceId;
	}

	public void setParentResourceId(Long parentResourceId) {
		this.parentResourceId = parentResourceId;
	}

	public long getCurrentUsage() {
		return currentUsage;
	}

	public void setCurrentUsage(long currentUsage) {
		this.currentUsage = currentUsage;
	}

	public String getDenyReason() {
		return denyReason;
	}

	public Integer getEffectiveMax() {
		return effectiveMax;
	}

	public void mergeLimit(int maxLimit) {
		if (this.denyReason != null) {
			return;
		}
		if (this.effectiveMax == null) {
			this.effectiveMax = maxLimit;
		} else {
			this.effectiveMax = Math.min(this.effectiveMax, maxLimit);
		}
	}

	public void deny(String reason) {
		this.denyReason = reason != null ? reason : "操作未授权";
		this.effectiveMax = null;
	}
}
