/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.policy;

/**
 * 单个 action 的已用量与有效上限快照。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public class WorkbenchActionLimit {

	private String action;

	private long used;

	private Integer max;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getUsed() {
		return used;
	}

	public void setUsed(long used) {
		this.used = used;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public boolean isUnlimited() {
		return max == null;
	}
}
