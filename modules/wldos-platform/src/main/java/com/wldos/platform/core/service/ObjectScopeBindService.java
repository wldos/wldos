/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.ObjectScopeBindDao;
import com.wldos.platform.core.dao.ScopeDimensionDao;
import com.wldos.platform.core.entity.WoObjectScopeBind;
import com.wldos.platform.core.entity.WoScopeDimension;

/**
 * 对象范围维绑定维护（按表渐进登记）。
 *
 * @author Yuanxi Universe
 * @version 1.0.0
 * @since 3.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ObjectScopeBindService extends EntityService<ObjectScopeBindDao, WoObjectScopeBind, Long> {

	@Autowired
	private ScopeDimensionDao scopeDimensionDao;

	public void validateBind(WoObjectScopeBind entity) {
		if (entity == null) {
			throw new BaseException("绑定不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!StringUtils.hasText(entity.getEntityKey())) {
			throw new BaseException("实体不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!StringUtils.hasText(entity.getDimensionCode())) {
			throw new BaseException("范围维不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!StringUtils.hasText(entity.getColumnName())) {
			throw new BaseException("物理列不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		entity.setEntityKey(entity.getEntityKey().trim());
		entity.setDimensionCode(entity.getDimensionCode().trim());
		entity.setColumnName(entity.getColumnName().trim());
		if (entity.getJoinTemplate() != null) {
			entity.setJoinTemplate(entity.getJoinTemplate().trim());
		}
		WoScopeDimension dim = this.scopeDimensionDao.findByDimensionCode(entity.getDimensionCode());
		if (dim == null || !"1".equals(dim.getEnabled())) {
			throw new BaseException("该范围维未在字典中启用：" + entity.getDimensionCode(),
					ResultCode.VALIDATION_ERROR.getCode());
		}
	}
}
