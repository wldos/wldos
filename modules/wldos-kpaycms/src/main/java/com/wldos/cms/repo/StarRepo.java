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
import com.wldos.cms.entity.KStars;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;

/**
 * 点赞关注repository操作类
 *
 * @author 元悉宇宙
 * @date 2021/4/17
 * @version 1.0
 */
public interface StarRepo extends BaseRepo<KStars, Long> {

	@Query("select c.* from k_stars c where c.object_id=:objId")
	List<KStars> queryStarsByObjectId(Long objId);

	@Query("select c.* from k_stars c where c.object_id=:objId and c.user_id=:userId")
	KStars queryStarByObjectIdAndUserId(Long objId, Long userId);

	@Modifying
	@Query("update k_stars set stars=ABS(stars-1) where object_id=:objId and user_id=:userId")
	void updateStar(Long objId, Long userId);

	@Modifying
	@Query("update k_stars set likes=ABS(likes-1) where object_id=:objId and user_id=:userId")
	void updateLike(Long objId, Long userId);

	@Modifying
	@Query("update k_stars set stars=ABS(stars-1) where id=:id")
	void updateStar(Long id);

	@Modifying
	@Query("update k_stars set likes=ABS(likes-1) where id=:id")
	void updateLike(Long id);
}
