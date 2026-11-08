/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.policy;

/**
 * 创作工作台策略 action 与 WSHook extName。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class WorkbenchPolicyHooks {

	public static final String ACTION_CREATE_WORK = "cms.workbench.createWork";

	public static final String ACTION_CREATE_CHAPTER = "cms.workbench.createChapter";

	public static final String HOOK_CONTRIBUTE_LIMIT = "cms.workbench.contributeLimit";

	public static final String HOOK_BEFORE_CREATE_WORK = "cms.beforeCreateWork";

	public static final String HOOK_BEFORE_CREATE_CHAPTER = "cms.beforeCreateChapter";

	private WorkbenchPolicyHooks() {
	}
}
