/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.service;

import java.util.List;

import com.wldos.base.service.BaseService;
import com.wldos.cms.entity.KPubmeta;
import com.wldos.cms.model.KModelMetaKey;
import com.wldos.cms.repo.PubmetaRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发布内容扩展属性service。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PubmetaService extends BaseService<PubmetaRepo, KPubmeta, Long> {

	public List<KPubmeta> queryPubMetaByPubId(Long pid) {
		return this.entityRepo.queryPubMetaByPubId(pid);
	}

	KPubmeta queryByPubIdAndMetaKey(Long postId, String metaKey) {
		return this.entityRepo.queryByPubIdAndMetaKey(postId, metaKey);
	}

	List<KPubmeta> queryAllByPubIdInAndMetaKey(List<Long> postId, String metaKey) {
		return this.entityRepo.queryAllByPubIdInAndMetaKey(postId, metaKey);
	}

	public List<KPubmeta> queryPubMetaByPubIds(List<Long> pids) {
		return this.entityRepo.queryPubMetaByPubIds(pids);
	}

	public void increasePubViews(int count, Long pid) {
		this.entityRepo.increasePubViews(count, pid, KModelMetaKey.PUB_META_KEY_VIEWS);
	}

	boolean isExistsViews(Long pid) {
		return this.entityRepo.isExistsViews(pid, KModelMetaKey.PUB_META_KEY_VIEWS);
	}
}
