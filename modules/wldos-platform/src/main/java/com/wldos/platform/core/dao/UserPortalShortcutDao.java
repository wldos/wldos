/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.dao;

import java.util.List;
import java.util.Optional;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoUserPortalShortcut;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 门户「我的常用」DAO。
 */
public interface UserPortalShortcutDao extends BaseDao<WoUserPortalShortcut, Long> {

	@Query("select * from wo_user_portal_shortcut where user_id = :userId and com_id = :comId and domain_id = :domainId "
			+ "and delete_flag = 'normal' order by coalesce(pinned, '0') desc, "
			+ "case when coalesce(pinned, '0') = '1' then display_order else -coalesce(hit_count, 0) end asc, "
			+ "coalesce(last_hit_at, create_time) desc, id desc")
	List<WoUserPortalShortcut> listByScope(@Param("userId") Long userId, @Param("comId") Long comId,
			@Param("domainId") Long domainId);

	@Query("select * from wo_user_portal_shortcut where user_id = :userId and com_id = :comId and domain_id = :domainId "
			+ "and path = :path and delete_flag = 'normal'")
	Optional<WoUserPortalShortcut> findByPath(@Param("userId") Long userId, @Param("comId") Long comId,
			@Param("domainId") Long domainId, @Param("path") String path);

	@Query("select id from wo_user_portal_shortcut where user_id = :userId and com_id = :comId and domain_id = :domainId "
			+ "and delete_flag = 'normal' and coalesce(pinned, '0') <> '1' "
			+ "order by coalesce(hit_count, 0) asc, coalesce(last_hit_at, create_time) asc limit 1")
	Long findEvictUnpinnedId(@Param("userId") Long userId, @Param("comId") Long comId,
			@Param("domainId") Long domainId);

	@Query("select coalesce(max(display_order), 0) from wo_user_portal_shortcut where user_id = :userId "
			+ "and com_id = :comId and domain_id = :domainId and delete_flag = 'normal'")
	int maxDisplayOrder(@Param("userId") Long userId, @Param("comId") Long comId, @Param("domainId") Long domainId);

	@Query("select min(display_order) from wo_user_portal_shortcut where user_id = :userId "
			+ "and com_id = :comId and domain_id = :domainId and delete_flag = 'normal' and coalesce(pinned, '0') = '1'")
	Integer minPinnedDisplayOrder(@Param("userId") Long userId, @Param("comId") Long comId,
			@Param("domainId") Long domainId);

	@Query("select count(1) from wo_user_portal_shortcut where user_id = :userId and com_id = :comId "
			+ "and domain_id = :domainId and delete_flag = 'normal'")
	long countActive(@Param("userId") Long userId, @Param("comId") Long comId, @Param("domainId") Long domainId);
}
