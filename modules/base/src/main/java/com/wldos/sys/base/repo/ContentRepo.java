/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import com.wldos.sys.base.entity.KModelContent;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 内容模型repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface ContentRepo extends PagingAndSortingRepository<KModelContent, Long> {

	@Query("select a.* from k_model_content a where a.content_code=:contType")
	KModelContent findByContType(@Param("contType") String contType);
}
