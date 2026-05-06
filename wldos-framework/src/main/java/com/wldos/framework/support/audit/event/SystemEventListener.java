/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.framework.support.audit.event;

import io.github.wldos.framework.support.audit.ISystemLogger;
import io.github.wldos.framework.support.audit.SystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 桥接 Spring 事件总线 → {@link ISystemLogger} 契约。
 *
 * <p>支持业务侧两种入口（与 {@link SystemEvent} 文档一致）：
 * <ul>
 *   <li>方式 A（直调）：{@code systemLogger.recordAsync(SystemEvent.info(...))} —— 主入口；</li>
 *   <li>方式 B（事件总线）：{@code applicationEventPublisher.publishEvent(SystemEvent.info(...))} ——
 *       本监听器接收，转调到 {@link ISystemLogger}。</li>
 * </ul>
 *
 * <p>方式 B 的意义：业务模块完全无需注入日志组件 + 多 listener 可并行消费同一事件
 * （例如：默认 listener 写库；商业增强 listener 同时推送告警邮件）。
 *
 * <p>本监听器对 {@code SystemEvent} POJO 直接监听（Spring 4.2+ 支持发布任意对象，
 * 无需继承 {@code ApplicationEvent}）。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Slf4j
@Component
public class SystemEventListener {

	@Autowired(required = false)
	private ISystemLogger systemLogger;

	@EventListener
	public void onSystemEvent(SystemEvent event) {
		if (systemLogger == null) {
			// 没有契约实现（极端裁剪部署）：降级 logback。
			log.warn("[sys.log.listener.fallback] {} / {} : {}",
					event == null ? null : event.getSourceModule(),
					event == null ? null : event.getEventType(),
					event == null ? null : event.getMessage());
			return;
		}
		systemLogger.recordAsync(event);
	}
}
