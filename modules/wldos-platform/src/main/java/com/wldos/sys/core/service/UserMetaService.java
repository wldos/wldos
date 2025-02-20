/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.core.service;

import java.util.List;

import com.wldos.framework.service.RepoService;
import com.wldos.sys.core.entity.WoUsermeta;
import com.wldos.sys.core.repo.UsermetaRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户辅助属性service。
 *
 * @author 元悉宇宙
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserMetaService extends RepoService<UsermetaRepo, WoUsermeta, Long> {
	public WoUsermeta findByUserIdAndMetaKey(Long uid, String ack) {
		return this.entityRepo.findByUserIdAndMetaKey(uid, ack);
	}

	public List<WoUsermeta> findAllByUserId(Long userId) {
		return this.entityRepo.findAllByUserId(userId);
	}
}
