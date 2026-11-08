/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.dao;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoDomainOption;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 域级系统选项 DAO。
 * 
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface DomainOptionDao extends BaseDao<WoDomainOption, Long> {

	@Query("SELECT * FROM wo_domain_option WHERE domain_id = :domainId AND option_key = :optionKey AND delete_flag = 'normal' LIMIT 1")
	WoDomainOption findByDomainIdAndOptionKey(@Param("domainId") Long domainId, @Param("optionKey") String optionKey);

	@Query("SELECT * FROM wo_domain_option WHERE domain_id = :domainId AND delete_flag = 'normal'")
	java.util.List<WoDomainOption> findAllByDomainId(@Param("domainId") Long domainId);
}
