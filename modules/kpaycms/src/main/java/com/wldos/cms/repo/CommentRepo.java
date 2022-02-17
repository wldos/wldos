/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.entity.KComments;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 评论repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface CommentRepo extends PagingAndSortingRepository<KComments, Long> {

	@Query("select c.* from k_comments c where c.delete_flag=:delFlag and c.approved=:comStatus and c.post_id=:postId")
	List<KComments> queryCommentsByPostId(Long postId, String delFlag, String comStatus);
}
