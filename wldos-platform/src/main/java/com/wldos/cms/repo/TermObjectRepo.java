/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.entity.KTermObject;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 分类、对象关联关系repository操作类。
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface TermObjectRepo extends PagingAndSortingRepository<KTermObject, Long>{

	@Query("select o.* from k_term_object o where o.term_type_id=:termTypeId")
	List<KTermObject> findAllByTermTypeId(Long termTypeId);

	List<KTermObject> findAllByObjectId(Long objectId);

	List<KTermObject> findAllByObjectIdIn(List<Long> objectIds);
}
