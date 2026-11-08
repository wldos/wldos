/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.policy.hook;

import com.wldos.cms.policy.WorkbenchPolicyContext;
import com.wldos.cms.policy.WorkbenchPolicyHooks;
import io.github.wldos.framework.support.plugins.Handler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

/**
 * 社区/自部署实例默认创作上限（配置项，非订单 SKU）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
@ConditionalOnMissingClass("com.wldos.store.entitlements.hook.MembershipWorkbenchLimitHookHandler")
public class ConfigWorkbenchLimitHookHandler implements Handler {

	@Value("${wldos.cms.workbench.max-works:50}")
	private int maxWorks;

	@Value("${wldos.cms.workbench.max-chapters:500}")
	private int maxChapters;

	@Override
	public Object applyHandler(Object... args) {
		if (args == null || args.length == 0 || !(args[0] instanceof WorkbenchPolicyContext)) {
			return args != null && args.length > 0 ? args[0] : null;
		}
		WorkbenchPolicyContext ctx = (WorkbenchPolicyContext) args[0];
		String action = ctx.getAction();
		if (WorkbenchPolicyHooks.ACTION_CREATE_WORK.equals(action)) {
			ctx.mergeLimit(this.maxWorks);
		} else if (WorkbenchPolicyHooks.ACTION_CREATE_CHAPTER.equals(action)) {
			ctx.mergeLimit(this.maxChapters);
		}
		return ctx;
	}
}
