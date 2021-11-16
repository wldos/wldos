/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.repo;

import com.wldos.cms.entity.KModelContent;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 内容模型repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface ContentRepo extends PagingAndSortingRepository<KModelContent, Long>{

	@Query("select a.* from k_model_content a where a.content_code=:contType")
	KModelContent findByContType(String contType);
}
