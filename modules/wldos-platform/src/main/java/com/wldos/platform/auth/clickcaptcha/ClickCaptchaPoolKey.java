/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.clickcaptcha;

/**
 * 点选验证码字池标识（与前端 locale 解析结果一致）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class ClickCaptchaPoolKey {

	public static final String ZH_HANS = "zh-Hans";

	public static final String ZH_HANT = "zh-Hant";

	public static final String EN = "en";

	private ClickCaptchaPoolKey() {
	}
}
