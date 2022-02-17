/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.entity.KPostmeta;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 帖子扩展数据repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface PostmetaRepo extends PagingAndSortingRepository<KPostmeta, Long> {
	@Query("select m.* from k_postmeta m where m.post_id=:pid")
	List<KPostmeta> queryPostMetaByPostId(Long pid);

	KPostmeta queryByPostIdAndMetaKey(Long postId, String metaKey);

	List<KPostmeta> queryAllByPostIdInAndMetaKey(List<Long> postId, String metaKey);

	@Query("select m.* from k_postmeta m where m.post_id in (:pids) ")
	List<KPostmeta> queryPostMetaByPostIds(List<Long> pids);

	@Modifying
	@Query("update k_postmeta set meta_value = meta_value + (:count) where post_id=:pid and meta_key=:viewsKey")
	void increasePostViews(int count, Long pid, String viewsKey);

	@Query("select count(1) from k_postmeta s where s.post_id=:pid and s.meta_key=:viewsKey")
	boolean isExistsViews(Long pid, String viewsKey);
}
