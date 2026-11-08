/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.mp.context;

import io.github.wldos.framework.common.CommonOperation;
import io.github.wldos.platform.support.mp.context.WldosWriteScope;
import io.github.wldos.platform.support.rbac.WriteScopeTarget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * {@link WldosWriteScope} 默认实现。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class WldosWriteScopeImpl implements WldosWriteScope {

	@Autowired
	@Lazy
	private CommonOperation commonOperation;

	@Override
	public void apply(Object entity, WriteScopeTarget target) {
		commonOperation.applyWriteScope(entity, target);
	}
}
