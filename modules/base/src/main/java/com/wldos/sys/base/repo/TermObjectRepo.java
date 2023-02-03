/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.sys.base.entity.KTermObject;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 分类、对象关联关系repository操作类。
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface TermObjectRepo extends PagingAndSortingRepository<KTermObject, Long> {
	/**
	 * 查询某个分类下关联的作品集或作品列表
	 *
	 * @param termTypeId 分类类型id
	 * @return 内容关联列表（分类或标签等）
	 */
	@Query("select o.* from k_term_object o where o.term_type_id=:termTypeId")
	List<KTermObject> findAllByTermTypeId(@Param("termTypeId") Long termTypeId);

	/**
	 * 根据对象id查询归属的分类关系列表，这混合了所有分类法关系，如果你想按分类法类型查询，请移步<code>TermRepo#findAllByObjectAndClassType</code>
	 *
	 * @param objectId 对象id
	 * @return 分类关系列表（分类、标签等）
	 */
	List<KTermObject> findAllByObjectId(@Param("objectId") Long objectId);

	/**
	 * 根据对象id批量查询归属的分类关系
	 *
	 * @param objectIds 批量对象id
	 * @return 分类关系列表（分类、标签等）
	 */
	List<KTermObject> findAllByObjectIdIn(@Param("objectIds") List<Long> objectIds);

	/**
	 * 删除归属的某些分类（分类、标签等）
	 *
	 * @param termTypeId 分类项类型id
	 * @param pId 对象id
	 */
	@Modifying
	@Query("delete from k_term_object where term_type_id in (:termTypeId) and object_id=:pId")
	void deleteAllByTermTypeIdAndObjectId(@Param("termTypeId") List<Long> termTypeId, @Param("pId") Long pId);
}