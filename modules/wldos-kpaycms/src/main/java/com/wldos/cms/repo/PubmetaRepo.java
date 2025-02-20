/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.framework.repo.BaseRepo;
import com.wldos.support.cms.entity.KPubmeta;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;

/**
 * 发布内容扩展数据repository操作类
 *
 * @author 元悉宇宙
 * @date 2021/4/17
 * @version 1.0
 */
public interface PubmetaRepo extends BaseRepo<KPubmeta, Long> {
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
