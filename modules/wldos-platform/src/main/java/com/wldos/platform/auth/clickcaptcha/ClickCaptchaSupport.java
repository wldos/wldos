/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.clickcaptcha;

import java.util.concurrent.TimeUnit;

import io.github.wldos.framework.support.cache.ICache;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.common.utils.UUIDUtils;

/**
 * 点选验证 Redis 键与发信门闸凭证。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class ClickCaptchaSupport {

	public static final String MODE_TILE = "tile";

	public static final String MODE_IMAGE = "image";

	private static final String CLICK_CHALLENGE = "wldos:captcha:click:ch:%s";

	private static final String EMAIL_SEND_GATE = "wldos:captcha:email-gate:%s";

	private static final int CLICK_CHALLENGE_TTL_MINUTES = 5;

	private static final int EMAIL_SEND_GATE_TTL_MINUTES = 2;

	private ClickCaptchaSupport() {
	}

	public static String clickChallengeKey(String challengeUuid) {
		return String.format(CLICK_CHALLENGE, challengeUuid);
	}

	public static String emailSendGateKey(String graphPass) {
		return String.format(EMAIL_SEND_GATE, graphPass);
	}

	public static void storeChallenge(ICache cache, String challengeUuid, String payload) {
		cache.set(
				clickChallengeKey(challengeUuid),
				payload,
				CLICK_CHALLENGE_TTL_MINUTES,
				TimeUnit.MINUTES);
	}

	public static String getChallengePayload(ICache cache, String challengeUuid) {
		return ObjectUtils.string(cache.get(clickChallengeKey(challengeUuid)));
	}

	public static void removeChallenge(ICache cache, String challengeUuid) {
		cache.remove(clickChallengeKey(challengeUuid));
	}

	public static String issueGraphPass(ICache cache) {
		String graphPass = UUIDUtils.generateShortUuid();
		cache.set(emailSendGateKey(graphPass), "1", EMAIL_SEND_GATE_TTL_MINUTES, TimeUnit.MINUTES);
		return graphPass;
	}

	public static boolean consumeGraphPass(ICache cache, String graphPass) {
		if (ObjectUtils.isBlank(graphPass)) {
			return false;
		}
		String key = emailSendGateKey(graphPass);
		if (!"1".equals(ObjectUtils.string(cache.get(key)))) {
			return false;
		}
		cache.remove(key);
		return true;
	}
}
