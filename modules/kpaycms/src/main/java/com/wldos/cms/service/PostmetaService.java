/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.service;

import java.util.List;

import com.wldos.base.service.BaseService;
import com.wldos.cms.entity.KPostmeta;
import com.wldos.cms.model.KModelMetaKey;
import com.wldos.cms.repo.PostmetaRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 帖子扩展属性service。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PostmetaService extends BaseService<PostmetaRepo, KPostmeta, Long> {

	public List<KPostmeta> queryPostMetaByPostId(Long pid) {
		return this.entityRepo.queryPostMetaByPostId(pid);
	}

	List<KPostmeta> queryAllByPostIdInAndMetaKey(List<Long> postId, String metaKey) {
		return this.entityRepo.queryAllByPostIdInAndMetaKey(postId, metaKey);
	}

	public List<KPostmeta> queryPostMetaByPostIds(List<Long> pids) {
		return this.entityRepo.queryPostMetaByPostIds(pids);
	}

	public void increasePostViews(int count, Long pid) {
		this.entityRepo.increasePostViews(count, pid, KModelMetaKey.PUB_META_KEY_VIEWS);
	}

	boolean isExistsViews(Long pid) {
		return this.entityRepo.isExistsViews(pid, KModelMetaKey.PUB_META_KEY_VIEWS);
	}
}
