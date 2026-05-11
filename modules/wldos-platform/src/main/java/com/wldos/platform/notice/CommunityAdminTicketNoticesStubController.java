/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.notice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.wldos.common.res.Result;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 不包含工单商业模块时，管理端布局仍会轮询
 * {@code GET /admin/ticket/notices*}；此处提供空实现，避免 404 落入静态资源处理器。
 * <p>当 classpath 中存在商业工单控制器 {@code com.wldos.cms.customer.controller.TicketAdminController}
 * 时本类不注册；仅在该类不可见（未引入 {@code wldos-cms-customer} 等）时启用空实现。
 */
@RestController
@RequestMapping("/admin/ticket")
@ConditionalOnMissingClass("com.wldos.cms.customer.controller.TicketAdminController")
public class CommunityAdminTicketNoticesStubController {

	@GetMapping("notices/count")
	public Result<Map<String, Integer>> noticesCount() {
		Map<String, Integer> data = new HashMap<>(2);
		data.put("totalCount", 0);
		data.put("unreadCount", 0);
		return Result.ok(data);
	}

	@GetMapping("notices")
	public Result<List<Map<String, Object>>> notices(@RequestParam(value = "maxCount", defaultValue = "20") int maxCount) {
		return Result.ok(Collections.emptyList());
	}
}
