/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.entity.KStars;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 点赞关注repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface StarRepo extends PagingAndSortingRepository<KStars, Long> {

	@Query("select c.* from k_stars c where c.object_id=:objId")
	List<KStars> queryStarsByObjectId(Long objId);

	@Query("select c.* from k_stars c where c.object_id=:objId and c.user_id=:userId")
	KStars queryStarByObjectIdAndUserId(Long objId, Long userId);

	@Modifying
	@Query("update k_stars set stars=ABS(stars-1) where object_id=:objId and userId=:userId")
	void updateStar(Long objId, Long userId);

	@Modifying
	@Query("update k_stars set likes=ABS(likes-1) where object_id=:objId and userId=:userId")
	void updateLike(Long objId, Long userId);

	@Modifying
	@Query("update k_stars set stars=ABS(stars-1) where id=:id")
	void updateStar(Long id);

	@Modifying
	@Query("update k_stars set likes=ABS(likes-1) where id=:id")
	void updateLike(Long id);
}
