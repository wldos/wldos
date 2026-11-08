/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.auth.service;

/**
 * 账号密码登录时的 Host/站点准入：禁止跨租户、跨站兜底登录。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface LoginHostAdmissionPolicy {

	/**
	 * 校验用户是否允许在当前请求域上完成密码登录；不允许则抛业务异常。
	 *
	 * @param userId 已验密通过的用户 id
	 * @param requestDomainId 当前 Host 解析的 wo_domain.id
	 * @param contextComId 请求上下文租户 com（可为 null）
	 */
	void assertPasswordLoginAllowed(Long userId, Long requestDomainId, Long contextComId);

	/**
	 * 带体验沙箱上下文时的准入；默认与 {@link #assertPasswordLoginAllowed(Long, Long, Long)} 相同。
	 *
	 * @param trialSandboxId 体验沙箱 id（门户 Host 登录时由前端传入，可为 null）
	 */
	default void assertPasswordLoginAllowed(
			Long userId,
			Long requestDomainId,
			Long contextComId,
			Long trialSandboxId) {
		assertPasswordLoginAllowed(userId, requestDomainId, contextComId);
	}

	/**
	 * 按登录名查找用户时使用的站点 id（决定租户查找范围）；默认等于当前请求 Host 解析的 domainId。
	 *
	 * @param trialSandboxId 体验沙箱 id（可为 null）
	 * @return 用于解析租户范围的 wo_domain.id
	 */
	default Long resolveLoginLookupDomainId(Long requestDomainId, Long trialSandboxId) {
		return requestDomainId;
	}

	/**
	 * 写入 JWT 的 domainId；默认等于当前请求 Host 解析的 domainId。
	 *
	 * @param trialSandboxId 体验沙箱 id（可为 null）
	 * @return token 绑定的 wo_domain.id
	 */
	default Long resolvePasswordLoginTokenDomainId(
			Long userId,
			Long requestDomainId,
			Long contextComId,
			Long trialSandboxId) {
		return requestDomainId;
	}
}
