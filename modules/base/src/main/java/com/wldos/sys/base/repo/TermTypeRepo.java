/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.sys.base.entity.KModelContent;
import com.wldos.sys.base.entity.KTermType;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 分类类型repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface TermTypeRepo extends PagingAndSortingRepository<KTermType, Long> {
	@Modifying
	@Query("update k_term_type set count = (count + 1) where id=:termTypeId")
	void countPlus(@Param("termTypeId") Long termTypeId);

	@Modifying
	@Query("update k_term_type set count = (count + 1) where id in ( :termTypeIds )")
	void countPlus(@Param("termTypeIds") List<Long> termTypeIds);

	@Modifying
	@Query("update k_term_type set count = ABS(count - 1) where id in ( :termTypeIds )")
	void countSubtract(@Param("termTypeIds") List<Long> termTypeIds);

	@Query("select c.* from k_model_content c where c.id=(select t.content_id from k_term_type t where t.id=:termTypeId)")
	KModelContent queryContentTypeByTermType(@Param("termTypeId") Long termTypeId);
}
