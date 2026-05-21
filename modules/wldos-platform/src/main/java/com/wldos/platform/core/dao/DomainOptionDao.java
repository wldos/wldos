/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.dao;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoDomainOption;

import org.springframework.data.repository.query.Param;

/**
 * 域级系统选项 DAO。
 */
public interface DomainOptionDao extends BaseDao<WoDomainOption, Long> {

	WoDomainOption findByDomainIdAndOptionKey(@Param("domainId") Long domainId, @Param("optionKey") String optionKey);
}
