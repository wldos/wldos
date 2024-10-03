/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.framework.repo.impl;

import com.wldos.framework.repo.RepoExt;
import com.wldos.base.tools.CommonOperation;
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
