/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.repo;

import com.wldos.cms.entity.KModelContent;
import com.wldos.cms.entity.KTermType;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 分类类型repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface TermTypeRepo extends PagingAndSortingRepository<KTermType, Long>{
	@Modifying
	@Query("update k_term_type set count = (count + 1) where id=:termTypeId")
	void countPlus(Long termTypeId);

	@Query("select c.* from k_model_content c where c.id=(select t.content_id from k_term_type t where t.id=:termTypeId)")
	KModelContent queryContentTypeByTermType(Long termTypeId);
}
