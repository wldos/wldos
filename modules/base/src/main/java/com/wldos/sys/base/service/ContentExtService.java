/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.service;

import java.util.List;

import com.wldos.base.service.BaseService;
import com.wldos.sys.base.dto.ContentExt;
import com.wldos.sys.base.repo.ContentExtRepo;
import com.wldos.sys.base.entity.KModelContentExt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 内容扩展service。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ContentExtService extends BaseService<ContentExtRepo, KModelContentExt, Long> {

	public List<ContentExt> queryExtPropsByContentId(Long contentId) {
		return this.entityRepo.queryExtPropsByContentId(contentId);
	}
}
