/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.clickcaptcha;

import java.util.Map;

import com.wldos.platform.auth.vo.ClickCaptchaVerifyParams;

/**
 * 找回密码发信前点选人机验证（商业背景图模块实现；未加载时由占位 Bean 关闭能力）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface ClickCaptchaProvider {

	/**
	 * @param poolKey {@link ClickCaptchaPoolKey}，由 locale / Accept-Language 解析
	 * @return 题目载荷（含 challengeUuid、mode 等，由前端按 mode 渲染）
	 */
	Map<String, Object> createChallenge(String poolKey);

	/**
	 * @return 一次性 graphPass，失败返回 null
	 */
	String verify(ClickCaptchaVerifyParams params);

	/**
	 * 找回密码是否在发信前必须经过二次验证（商业背景图点选模块为 true，社区默认 false）。
	 */
	default boolean forgetPasswordRequiresSecondaryVerify() {
		return false;
	}
}
