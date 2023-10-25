/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.framework.repo.BaseRepo;
import com.wldos.support.term.dto.Term;
import com.wldos.sys.base.entity.KTermType;
import com.wldos.sys.base.entity.KTerms;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 分类项repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface TermRepo extends BaseRepo<KTerms, Long> {

	/**
	 * 查询某类下的分类项，默认按展示顺序排序
	 *
	 * @param classType 分类类型：分类目录、标签等
	 * @return 分类项列表
	 */
	@Query("select a.id, a.name, a.slug, a.info_flag, a.display_order, a.is_valid, o.id term_type_id, o.class_type, o.description, o.parent_id, o.count from k_terms a join k_term_type o on a.id=o.term_id where o.class_type=:classType and a.is_valid='1' and a.delete_flag='normal' order by a.display_order")
	List<Term> findAllByClassType(@Param("classType") String classType);

	/**
	 * 根据对象id、分类类型查询对象归属的某一类型的分类项列表
	 *
	 * @param objectId 对象id
	 * @param classType 分类类型：分类目录、标签等
	 * @return 分类项列表
	 */
	@Query("select a.id, a.name, a.slug, a.info_flag, a.display_order, a.is_valid, o.id term_type_id, o.class_type, o.description, o.parent_id, o.count from k_terms a join k_term_type o on a.id=o.term_id  where o.class_type=:classType and EXISTS(select 1 from k_term_object b where o.id=b.term_type_id and b.object_id=:objectId and a.is_valid='1' and a.delete_flag='normal')")
	List<Term> findAllByObjectAndClassType(@Param("objectId") Long objectId, @Param("classType") String classType);

	/**
	 * 根据分类别名查询分类类型
	 *
	 * @param slugCategory 分类项别名
	 * @return 分类类型实体
	 */
	@Query("select p.* from k_term_type p join k_terms t on p.term_id=t.id where t.slug=:slugCategory")
	KTermType queryTermTypeBySlug(@Param("slugCategory") String slugCategory);

	/**
	 * 根据分类类型id查询分类类型
	 *
	 * @param termTypeId 分类类型id
	 * @return 分类类型实体
	 */
	@Query("select p.* from k_term_type p where p.id=:termTypeId")
	KTermType queryTermTypeById(@Param("termTypeId") Long termTypeId);

	/**
	 * 批量根据分类项名称、分类类型查询该名称的分类项列表
	 *
	 * @param name 标签字面值
	 * @param classType 分类类型：分类目录、标签等
	 * @return 分类项列表
	 */
	@Query("select a.id, a.name, a.slug, a.info_flag, a.display_order, a.is_valid, o.id term_type_id, o.class_type, o.description, o.parent_id, o.count from k_terms a join k_term_type o on a.id=o.term_id where a.is_valid='1' and a.delete_flag='normal' and o.class_type=:classType and a.name in (:name)")
	List<Term> findAllByNameAndClassType(@Param("name") List<String> name, @Param("classType") String classType);

	/**
	 * 通过分类类型id查询分类项
	 *
	 * @param termTypeId 分类id
	 * @return 分类项信息
	 */
	@Query("select a.id, a.name, a.slug, a.info_flag, a.display_order, a.is_valid, p.id term_type_id, p.class_type, p.description, p.parent_id, p.count from k_term_type p join k_terms a on p.term_id=a.id where p.id=:termTypeId")
	Term queryTermByTermTypeId(@Param("termTypeId") Long termTypeId);

	/**
	 * 通过分类项别名查询分类项
	 *
	 * @param slugTerm 分类项别名
	 * @return 分类项信息
	 */
	@Query("select a.id, a.name, a.slug, a.info_flag, a.display_order, a.is_valid, p.id term_type_id, p.class_type, p.description, p.parent_id, p.count from k_term_type p join k_terms a on p.term_id=a.id where a.slug=:slugTerm")
	Term queryTermBySlugTerm(@Param("slugTerm") String slugTerm);

	/**
	 * 是否存在同名或同别名的分类项，仅用于新增
	 *
	 * @param name 分类项名称
	 * @param slug 分类项别名
	 * @return 是否存在
	 */
	@Query("select count(1) from k_terms t where t.name=:name or t.slug=:slug")
	boolean existsTermBySlugOrName(@Param("name") String name, @Param("slug") String slug);

	/**
	 * 是否存在同名或同别名的其他分类项，用于更新时判断是否重名
	 *
	 * @param name 分类项名称
	 * @param id 分类项id
	 * @return 是否存在重名
	 */
	@Query("select count(1) from k_terms t where (t.name=:name or t.slug=:slug) and t.id!=:id")
	boolean existsDifTermByNameOrSlugAndId(@Param("name") String name, @Param("slug") String slug, @Param("id") Long id);

	/**
	 * 用于新增分类项时判断是否与其他分类项别名重复，不区分分类法，仅用于新增
	 *
	 * @param slug 分类或标签的别名
	 * @param name 分类/标签名
	 */
	@Query("select count(1) from k_terms t where t.slug=:slug and t.name!=:name")
	boolean existsDifTermBySlugAndName(@Param("slug") String slug, @Param("name") String name);

	/**
	 * 根据分类项别名查询同名分类项是否已存在，若存在说明原样保存，仅用于更新时降低性能损耗
	 *
	 * @param slug 分类或标签的别名
	 */
	@Query("select count(1) from k_terms t where t.slug=:slug and t.name=:name")
	boolean existsSameTermBySlugAndName(@Param("slug") String slug, @Param("name") String name);

	/**
	 * 根据分类项类型id判断其下是否存在子分类
	 *
	 * @param termTypeId 分类项类型id
	 * @return 是否
	 */
	@Query("select count(1) from k_terms t join k_term_type k on t.id=k.term_id where k.parent_id=:id")
	boolean existsTermByTermTypeId(@Param("id") Long termTypeId);

	/**
	 * 通过分类类型id查询分类信息
	 *
	 * @param termTypeIds 分类类型id
	 * @return 分类信息
	 */
	@Query("select a.id, a.name, a.slug, a.info_flag, a.display_order, a.is_valid, p.id term_type_id, p.class_type, p.description, p.parent_id, p.count from k_term_type p join k_terms a on p.term_id=a.id where p.id in(:termTypeIds)")
	List<Term> queryAllTermsByTermTypeIds(@Param("termTypeIds") List<Long> termTypeIds);

	@Query("select max(r.display_order) max_order from k_terms r join k_term_type t on r.id=t.term_id where r.delete_flag='normal' and r.is_valid='1' and t.parent_id=:pId")
	Long queryMaxOrder(@Param("pId") Long parentId);

	/**
	 * 信息发布状态取反
	 *
	 * @param termIds 待取反的分类项ids
	 */
	@Modifying
	@Query("update k_terms set info_flag=ABS(info_flag-1) where id in (:tIds)")
	void infoFlagByIds(@Param("tIds") List<Long> termIds);
}