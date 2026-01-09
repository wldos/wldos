/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.service;

import java.util.List;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.cms.vo.Doc;
import com.wldos.cms.vo.DocItem;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.platform.core.service.AuthService;
import com.wldos.platform.support.cms.dto.ContModelDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文档库service。
 *
 * @author 元悉宇宙
 * @date 2023/5/14
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DocService extends NonEntityService {

	private final PubService pubService;

	public DocService(PubService pubService, AuthService authService) {
		this.pubService = pubService;
	}


	public PageableResult<DocItem> queryDocList(PageQuery pageQuery) {

		return this.pubService.queryDocList(pageQuery);
	}

	public Doc queryDoc(Long bookId, Long domainId) {
		ContModelDto pub = this.pubService.queryContModel(bookId, domainId);
		if (pub == null)
			return null;

		List<DocItem> chapters = this.pubService.queryDocItemByParentId(bookId, DeleteFlagEnum.NORMAL.toString());
		return Doc.of(pub.getId(), pub.getPubTitle(), chapters);
	}
}
