/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.entity.KTermType;
import com.wldos.cms.entity.KTerms;
import com.wldos.cms.vo.Term;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 分类项repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface TermRepo extends PagingAndSortingRepository<KTerms, Long>{

	@Query("select a.*, o.id term_type_id, o.class_type, o.content_id, o.description, o.parent_id, o.count from k_terms a join k_term_type o on a.id=o.term_id where o.class_type=:classType and o.content_id=:contId")
	List<Term> findByContType(String classType, Long contId);

	@Query("select a.*, o.id term_type_id, o.class_type, o.content_id, o.description, o.parent_id, o.count from k_terms a join k_term_type o on a.id=o.term_id where o.class_type=:classType")
	List<Term> findAllByClassType(String classType);

	@Query("select a.*, o.id term_type_id, o.class_type, o.content_id, o.description, o.parent_id, o.count from k_terms a join k_term_type o on a.id=o.term_id where o.id in (:ids)")
	List<Term> findAllByClassType(List<Long> ids);

	@Query("select a.*, o.id term_type_id, o.class_type, o.content_id, o.description, o.parent_id, o.count from k_terms a join k_term_type o on a.id=o.term_id  where o.class_type=:classType and EXISTS(select 1 from k_term_object b where o.id=b.term_type_id and b.object_id=:objectId)")
	List<Term> findAllByObjectAndClassType(Long objectId, String classType);

	@Query("select p.* from k_term_type p join k_terms t on p.term_id=t.id where t.slug=:slugCategory")
	KTermType queryTermTypeBySlug(String slugCategory);

	@Query("select t.* from k_term_type p join k_terms t on p.term_id=t.id where p.id in (:termTypeIds)")
	List<KTerms> queryKTermsByTermTypeIds(List<Long> termTypeIds);

	@Query("select t.*, p.id term_type_id, p.class_type, p.content_id, p.description, p.parent_id, p.count from k_term_type p join k_terms t on p.term_id=t.id where p.id=:termTypeId")
	Term queryTermByTermTypeId(Long termTypeId);

	@Query("select t.*, p.id term_type_id, p.class_type, p.content_id, p.description, p.parent_id, p.count from k_term_type p join k_terms t on p.term_id=t.id where t.slug=:slugTerm")
	Term queryTermBySlugTerm(String slugTerm);

	@Query("select t.*, p.id term_type_id, p.class_type, p.content_id, p.description, p.parent_id, p.count from k_term_type p join k_terms t on p.term_id=t.id where p.id in(:termTypeIds)")
	List<Term> queryAllTermsByTermTypeIds(List<Long> termTypeIds);
}
