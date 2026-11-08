/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.policy.hook;

import com.wldos.cms.policy.WorkbenchPolicyHooks;
import io.github.wldos.framework.support.hook.WSHook;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 注册创作工作台限额贡献 Hook（社区默认 Handler）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
@ConditionalOnBean(ConfigWorkbenchLimitHookHandler.class)
public class WorkbenchPolicyHookRegistrar {

	private static final int PRIORITY_CONFIG_LIMIT = 900;

	private final WSHook wsHook;

	private final ConfigWorkbenchLimitHookHandler configWorkbenchLimitHookHandler;

	public WorkbenchPolicyHookRegistrar(WSHook wsHook, ConfigWorkbenchLimitHookHandler configWorkbenchLimitHookHandler) {
		this.wsHook = wsHook;
		this.configWorkbenchLimitHookHandler = configWorkbenchLimitHookHandler;
	}

	@PostConstruct
	public void register() {
		this.wsHook.addHandler(WorkbenchPolicyHooks.HOOK_CONTRIBUTE_LIMIT, this.configWorkbenchLimitHookHandler,
				PRIORITY_CONFIG_LIMIT, 1);
	}
}
