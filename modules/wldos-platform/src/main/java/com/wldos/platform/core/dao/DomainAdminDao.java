/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoDomainAdmin;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DomainAdminDao extends BaseDao<WoDomainAdmin, Long> {

	@Query("SELECT * FROM wo_domain_admin WHERE domain_id = :domainId AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoDomainAdmin> findByDomainId(@Param("domainId") Long domainId);

	@Query("SELECT * FROM wo_domain_admin WHERE domain_id = :domainId AND user_id = :userId AND delete_flag = 'normal' LIMIT 1")
	WoDomainAdmin findByDomainAndUser(@Param("domainId") Long domainId, @Param("userId") Long userId);

	@Query("SELECT * FROM wo_domain_admin WHERE user_id = :userId AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoDomainAdmin> findByUserId(@Param("userId") Long userId);

	@Query("SELECT * FROM wo_domain_admin WHERE user_id IN (:userIds) AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoDomainAdmin> findByUserIds(@Param("userIds") Collection<Long> userIds);

	@Query("SELECT * FROM wo_domain_admin WHERE delete_flag = 'normal' AND is_valid = '1' ORDER BY create_time DESC")
	List<WoDomainAdmin> findAllValid();

	@Query("SELECT count(1) FROM wo_domain_admin WHERE domain_id = :domainId AND delete_flag = 'normal' AND is_valid = '1'")
	long countByDomainId(@Param("domainId") Long domainId);
}
