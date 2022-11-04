/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.service;

import java.util.concurrent.TimeUnit;

import com.wldos.auth.model.State;
import com.wldos.base.Base;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.UUIDUtils;

import org.springframework.stereotype.Service;

/**
 * 状态码服务，状态码是用来表达异步交互在时间维度上状态的随机码，有效期默认2分钟，防止csrf攻击。
 *
 * @author 树悉猿
 * @date 2022/10/17
 * @version 1.0
 */
@Service
public class StateCodeService extends Base {

	/**
	 * 状态码校验
	 *
	 * @param stateCode 状态码+uuid，约定：6位状态码开头
	 * @return 是否通过
	 */
	public boolean checkState(String stateCode) {
		String state = stateCode.substring(0, 6);
		String uuid = stateCode.substring(6);
		return this.checkState(state, uuid);
	}

	/**
	 * 状态码校验
	 *
	 * @param state 状态码
	 * @param uuid uuid
	 * @return 是否通过
	 */
	public boolean checkState(String state, String uuid) {
		// 获取缓存中的验证码
		String stateCode = this.getStateCodeFromCache(uuid);
		if (stateCode == null) {
			return false;
		}
		// 判断验证码
		return stateCode.toLowerCase().trim().equals(ObjectUtils.string(state).toLowerCase());
	}

	private String getStateCodeFromCache(String uuid) {
		return ObjectUtils.string(this.cache.get(String.format(RedisKeyEnum.STATE.toString(), uuid)));
	}

	/**
	 * 生成随机数状态码
	 *
	 * @return 状态码和uuid
	 */
	public String genState() {

		String uid = UUIDUtils.generateShortUuid();

		String text = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		// 验证码有效期120秒
		this.cache.set(String.format(RedisKeyEnum.STATE.toString(), uid), text, 2, TimeUnit.MINUTES);

		return text + uid;
	}
}
