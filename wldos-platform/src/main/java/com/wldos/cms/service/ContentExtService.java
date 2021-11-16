/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.service;

import java.util.List;

import com.wldos.support.service.BaseService;
import com.wldos.cms.entity.KModelContentExt;
import com.wldos.cms.repo.ContentExtRepo;
import com.wldos.cms.vo.ContentExt;

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
