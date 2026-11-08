/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wldos.cms.service.DomainCloneService;
import com.wldos.cms.vo.DomainCloneBody;
import com.wldos.cms.vo.DomainCloneResult;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.service.DomainService;

import io.github.wldos.framework.support.audit.annotation.OpLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 子站复制管理端 API。
 *
 * @author Yuanxi Universe
 * @date 2025/6/7
 * @version 1.0
 * @since 3.0
 */
@Api(tags = "子站复制")
@RestController
@RequestMapping("admin/sys/domain")
public class DomainCloneAdminController extends NonEntityController<DomainCloneService> {

	@Autowired
	private DomainService domainService;

	@ApiOperation(value = "复制子站", notes = "从源域复制菜单、域资源、CMS page 到目标域")
	@PostMapping("clone-site")
	@OpLog(action = "子站复制", resourceType = "domain", resourceId = "#body['targetDomainId']")
	public DomainCloneResult cloneSite(@RequestBody DomainCloneBody body) {
		DomainCloneResult result = service.cloneSite(
				body.getSourceDomainId(),
				body.getTargetDomainId(),
				body.getOptions());
		WoDomain target = domainService.findById(body.getTargetDomainId());
		if (target != null) {
			domainService.refreshDomain(target);
		}
		return result;
	}
}
