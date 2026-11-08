/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.audit.support;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import io.github.wldos.common.Constants;
import io.github.wldos.framework.common.CommonOperation;
import io.github.wldos.framework.support.audit.SystemEvent;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 系统日志入队前补全 {@code comId}/{@code domainId}/{@code operatorUserId}，与操作日志 AOP 同源（网关注入头 / CommonOperation）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class SystemLogContextEnricher {

	@Resource
	@Lazy
	private CommonOperation commonOperation;

	public void enrichIfAbsent(SystemEvent event) {
		if (event.getComId() != null && event.getDomainId() != null && event.getOperatorUserId() != null) {
			return;
		}
		TenantStamp stamp = resolveFromCurrentRequest();
		if (event.getOperatorUserId() == null && stamp.operatorUserId != null) {
			event.withOperator(stamp.operatorUserId);
		}
		if (event.getDomainId() == null && stamp.domainId != null) {
			event.setDomainId(stamp.domainId);
		}
		if (event.getComId() == null && stamp.comId != null) {
			event.setComId(stamp.comId);
		}
		if (event.getOperatorUserId() == null) {
			Long fromMeta = parseUserIdFromMetadata(event.getMetadata());
			if (fromMeta != null) {
				event.withOperator(fromMeta);
			}
		}
	}

	/** 请求线程快照，供网关聚合桶在 flush 线程外保留租户/站点。 */
	public TenantStamp captureFromCurrentRequest() {
		return resolveFromCurrentRequest();
	}

	private TenantStamp resolveFromCurrentRequest() {
		TenantStamp stamp = new TenantStamp();
		try {
			stamp.operatorUserId = this.commonOperation.getUserId();
			stamp.domainId = this.commonOperation.getDomainId();
			Long workCom = this.commonOperation.getWorkComId();
			stamp.comId = workCom != null ? workCom : this.commonOperation.getTenantId();
		}
		catch (Exception ignored) {
			// 无请求上下文时走 header
		}
		HttpServletRequest request = currentRequest();
		if (request == null) {
			return stamp;
		}
		if (stamp.operatorUserId == null) {
			stamp.operatorUserId = parseLongString(request.getHeader(Constants.CONTEXT_KEY_USER_ID));
		}
		if (stamp.domainId == null) {
			stamp.domainId = parseLongString(request.getHeader(Constants.CONTEXT_KEY_USER_DOMAIN));
		}
		if (stamp.comId == null) {
			Long workCom = parseLongString(request.getHeader(Constants.CONTEXT_KEY_USER_WORK_COM));
			stamp.comId = workCom != null ? workCom : parseLongString(request.getHeader(Constants.CONTEXT_KEY_USER_TENANT));
		}
		return stamp;
	}

	private static Long parseUserIdFromMetadata(String metadata) {
		String raw = SystemLogMetadataEnricher.extractOperatorUserIdFromMetadata(metadata);
		return parseLongString(raw);
	}

	public static Long parseLongString(String raw) {
		if (raw == null || raw.isEmpty()) {
			return null;
		}
		try {
			return Long.parseLong(raw.trim());
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	private static HttpServletRequest currentRequest() {
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		if (attrs instanceof ServletRequestAttributes) {
			return ((ServletRequestAttributes) attrs).getRequest();
		}
		return null;
	}

	public static final class TenantStamp {
		private Long operatorUserId;
		private Long domainId;
		private Long comId;

		public Long getOperatorUserId() {
			return operatorUserId;
		}

		public Long getDomainId() {
			return domainId;
		}

		public Long getComId() {
			return comId;
		}
	}
}
