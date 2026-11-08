/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.clickcaptcha;

import java.util.Map;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.auth.vo.ClickCaptchaVerifyParams;
import io.github.wldos.common.exception.BaseException;

import org.springframework.stereotype.Service;

/**
 * 未加载商业点选模块时的契约占位：找回密码不走点选，仅邮箱准入与邮箱验证码。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class DisabledClickCaptchaProvider extends NonEntityService implements ClickCaptchaProvider {

	@Override
	public Map<String, Object> createChallenge(String poolKey) {
		throw new BaseException("点选安全验证未启用");
	}

	@Override
	public String verify(ClickCaptchaVerifyParams params) {
		return null;
	}
}
