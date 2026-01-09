/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.service;

import java.util.List;

import com.wldos.cms.dao.PubmetaDao;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.support.cms.PubmetaOpener;
import com.wldos.platform.support.cms.entity.KPubmeta;
import com.wldos.platform.support.cms.model.KModelMetaKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发布内容扩展属性service。
 *
 * @author 元悉宇宙
 * @date 2021/6/17
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PubmetaService extends EntityService<PubmetaDao, KPubmeta, Long> implements PubmetaOpener {

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
