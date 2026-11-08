/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 多 action 创作权益快照。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public class WorkbenchLimitsSnapshot {

	private final List<WorkbenchActionLimit> limits = new ArrayList<>();

	public List<WorkbenchActionLimit> getLimits() {
		return Collections.unmodifiableList(this.limits);
	}

	public void add(WorkbenchActionLimit limit) {
		if (limit != null) {
			this.limits.add(limit);
		}
	}
}
