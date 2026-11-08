/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.audit.support;

import io.github.wldos.framework.support.audit.ISystemLogger;
import io.github.wldos.framework.support.audit.SystemEvent;

/**
 * 平台侧系统日志装饰：入队前补 metadata 与租户/站点/操作者列。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public class PlatformSystemLogger implements ISystemLogger {

	private final ISystemLogger delegate;
	private final SystemLogContextEnricher contextEnricher;

	public PlatformSystemLogger(ISystemLogger delegate, SystemLogContextEnricher contextEnricher) {
		this.delegate = delegate;
		this.contextEnricher = contextEnricher;
	}

	@Override
	public void recordAsync(SystemEvent event) {
		SystemLogIpEnricher.enrichIfAbsent(event);
		this.contextEnricher.enrichIfAbsent(event);
		this.delegate.recordAsync(event);
	}
}
