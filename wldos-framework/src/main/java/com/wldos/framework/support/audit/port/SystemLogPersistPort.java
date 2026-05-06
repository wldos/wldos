/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.framework.support.audit.port;

import java.util.List;

import io.github.wldos.framework.support.audit.SystemEvent;

/**
 * 系统日志持久化端口（内部 SPI）。
 *
 * <p>架构定位与 {@link LoginLogPersistPort}/{@link OpLogPersistPort} 一致：
 * framework 不依赖具体表/Dao，platform 适配器实现本接口绑定到 {@code wo_sys_log} 表。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
public interface SystemLogPersistPort {

	void saveOne(SystemEvent event);

	void saveBatch(List<SystemEvent> events);
}
