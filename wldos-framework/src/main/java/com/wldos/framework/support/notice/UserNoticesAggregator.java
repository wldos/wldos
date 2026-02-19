/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.notice;

import java.util.List;
import java.util.Map;

/**
 * 用户通知聚合器：汇总各 {@link UserNoticesProvider} 的列表并排序返回。
 * 平台通过可选注入此接口提供 GET /user/notices；开源版不实现此接口则返回空列表，
 * 商业版由消息中间件模块（如 wldos-msg-middleware）提供实现。
 *
 * @author 元悉宇宙
 */
public interface UserNoticesAggregator {

	/**
	 * 聚合当前用户在各模块下的通知列表。
	 *
	 * @param userId  当前用户 ID，为 null 时返回空列表
	 * @param totalCap 返回条数上限，≤0 时由实现方自定
	 * @return 聚合后的通知列表，不为 null
	 */
	List<Map<String, Object>> aggregate(Long userId, int totalCap);
}
