/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.dao.impl;

import com.wldos.framework.mvc.dao.RepoExt;
import com.wldos.framework.common.CommonOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * jdbc操作扩展实现。
 *
 * @author 元悉宇宙
 * @date 2023/10/23
 * @version 1.0
 */
@Slf4j
public class RepoExtImpl implements RepoExt {
	@Autowired
	@Lazy
	private CommonOperation commonOperation;

	@Override
	public CommonOperation getCommonOperation() {
		return this.commonOperation;
	}

	@Override
	public <E> Long insertSelective(E entity, boolean isAutoFill) {
		return this.commonOperation.dynamicInsertByEntity(entity, isAutoFill);
	}
}
