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
import com.wldos.cms.entity.KStars;
import com.wldos.cms.repo.StarRepo;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.sys.base.service.AuthService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞关注service。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StarService extends BaseService<StarRepo, KStars, Long> {

	private final PubService pubService;

	private final AuthService authService;

	public StarService(PubService pubService, AuthService authService) {
		this.pubService = pubService;
		this.authService = authService;
	}

	public List<KStars> queryStarsByObjectId(Long contentId) {
		return this.entityRepo.queryStarsByObjectId(contentId);
	}

	/**
	 * 给内容点赞
	 *
	 * @param objId 内容id
	 * @param userId 用户id
	 */
	public int starObject(Long objId, Long userId) {
		KStars stars = this.entityRepo.queryStarByObjectIdAndUserId(objId, userId);
		if (stars == null) {
			// 生成点赞记录
			stars = KStars.of(this.nextId(), objId, userId, ValidStatusEnum.VALID.toString(), null);
			this.insertSelective(stars);
			return 1;
		}
		else {
			// 更新点赞记录
			this.entityRepo.updateStar(stars.getId());
		}

		// 更新发布内容点赞数据
		int add = !this.authService.isGuest(userId) && ValidStatusEnum.VALID.toString().equals(stars.getStars()) ? -1 : 1;
		this.pubService.updateStarCount(objId, add);

		return add;
	}

	/**
	 * 给内容点赞
	 *
	 * @param objId 内容id
	 * @param userId 用户id
	 */
	public int likeObject(Long objId, Long userId) {
		KStars stars = this.entityRepo.queryStarByObjectIdAndUserId(objId, userId);
		if (stars == null) {
			// 生成关注记录
			stars = KStars.of(this.nextId(), objId, userId, null, ValidStatusEnum.VALID.toString());
			this.insertSelective(stars);
			return 1;
		}
		else {
			// 更新关注记录
			this.entityRepo.updateLike(stars.getId());
		}

		// 更新发布内容关注数据
		int add = !this.authService.isGuest(userId) && ValidStatusEnum.VALID.toString().equals(stars.getLikes()) ? -1 : 1;
		this.pubService.updateLikeCount(objId, add);

		return add;
	}
}
