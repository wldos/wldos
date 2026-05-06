/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.framework.support.audit.port;

import java.util.List;

import io.github.wldos.framework.support.audit.LoginEvent;

/**
 * 登录日志持久化端口（内部 SPI）。
 *
 * <p>架构定位：
 * <ul>
 *   <li>{@code wldos-framework} 是基础层，<b>不能直接依赖 {@code wldos-platform}</b> 的具体表/Dao；</li>
 *   <li>本接口由 {@code wldos-platform} 的适配器实现（绑定到 {@code wo_login_log} 表），
 *       {@link com.wldos.framework.support.audit.impl.DefaultLoginLogger} 通过此端口落盘；</li>
 *   <li>不同部署（如裁剪版、独立审计服务）可提供不同实现：写 ES、ClickHouse、Kafka 等。</li>
 * </ul>
 *
 * <p>容错原则：实现方必须捕获异常并对外抛出 {@link RuntimeException} 或自吞 + 内部 logback；
 * 默认实现侧会再做一层异常保护，不让落盘失败传播到业务请求线程。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
public interface LoginLogPersistPort {

	/** 单条落盘。 */
	void saveOne(LoginEvent event);

	/** 批量落盘（异步队列消费侧调用）。 */
	void saveBatch(List<LoginEvent> events);
}
