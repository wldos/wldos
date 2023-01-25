/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.entity.KPubmeta;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 发布内容扩展数据repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface PubmetaRepo extends PagingAndSortingRepository<KPubmeta, Long> {
	@Query("select m.* from k_pubmeta m where m.pub_id=:pid")
	List<KPubmeta> queryPubMetaByPubId(Long pid);

	KPubmeta queryByPubIdAndMetaKey(Long pubId, String metaKey);

	List<KPubmeta> queryAllByPubIdInAndMetaKey(List<Long> pubId, String metaKey);

	@Query("select m.* from k_pubmeta m where m.pub_id in (:pids) ")
	List<KPubmeta> queryPubMetaByPubIds(List<Long> pids);

	@Modifying
	@Query("update k_pubmeta set meta_value = meta_value + (:count) where pub_id=:pid and meta_key=:viewsKey")
	void increasePubViews(int count, Long pid, String viewsKey);

	@Query("select count(1) from k_pubmeta s where s.pub_id=:pid and s.meta_key=:viewsKey")
	boolean isExistsViews(Long pid, String viewsKey);
}
