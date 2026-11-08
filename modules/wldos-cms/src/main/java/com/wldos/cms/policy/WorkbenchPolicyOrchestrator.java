/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.policy;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.framework.support.hook.FrameworkHookExtNames;
import io.github.wldos.framework.support.hook.WSHook;

import org.springframework.stereotype.Component;

/**
 * 创作工作台策略：Hook 聚合限额后断言，并触发 agent / 业务 before Hook。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class WorkbenchPolicyOrchestrator {

	private final WSHook wsHook;

	public WorkbenchPolicyOrchestrator(WSHook wsHook) {
		this.wsHook = wsHook;
	}

	public void assertAllowed(WorkbenchPolicyContext context) {
		if (context == null || context.getAction() == null) {
			return;
		}
		runContributeLimitHook(context);
		if (context.getDenyReason() != null) {
			throw new BaseException(context.getDenyReason(), ResultCode.FORBIDDEN.getCode());
		}
		if (context.getEffectiveMax() != null && context.getCurrentUsage() + 1 > context.getEffectiveMax()) {
			throw new BaseException("已达当前会员权益上限，请升级套餐", ResultCode.FORBIDDEN.getCode());
		}
		if (this.wsHook != null) {
			this.wsHook.doInvoke(FrameworkHookExtNames.WLDOS_POLICY_ENFORCE, context);
			String beforeExt = beforeHookForAction(context.getAction());
			if (beforeExt != null) {
				this.wsHook.doInvoke(beforeExt, context);
			}
		}
	}

	public WorkbenchActionLimit resolveLimit(WorkbenchPolicyContext context) {
		WorkbenchActionLimit snapshot = new WorkbenchActionLimit();
		if (context == null || context.getAction() == null) {
			return snapshot;
		}
		snapshot.setAction(context.getAction());
		snapshot.setUsed(context.getCurrentUsage());
		runContributeLimitHook(context);
		if (context.getDenyReason() != null) {
			snapshot.setMax(0);
			return snapshot;
		}
		snapshot.setMax(context.getEffectiveMax());
		return snapshot;
	}

	private void runContributeLimitHook(WorkbenchPolicyContext context) {
		if (this.wsHook == null) {
			return;
		}
		this.wsHook.applyHandler(WorkbenchPolicyHooks.HOOK_CONTRIBUTE_LIMIT, context);
	}

	private static String beforeHookForAction(String action) {
		if (WorkbenchPolicyHooks.ACTION_CREATE_WORK.equals(action)) {
			return WorkbenchPolicyHooks.HOOK_BEFORE_CREATE_WORK;
		}
		if (WorkbenchPolicyHooks.ACTION_CREATE_CHAPTER.equals(action)) {
			return WorkbenchPolicyHooks.HOOK_BEFORE_CREATE_CHAPTER;
		}
		return null;
	}
}
