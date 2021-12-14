/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.cms.dto.LevelNode;
import com.wldos.cms.dto.TermObject;
import com.wldos.cms.enums.TermTypeEnum;
import com.wldos.cms.repo.TermObjectRepo;
import com.wldos.cms.repo.TermRepo;
import com.wldos.cms.vo.TermTree;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.cms.entity.KModelContent;
import com.wldos.cms.entity.KTermObject;
import com.wldos.cms.entity.KTermType;
import com.wldos.cms.entity.KTerms;
import com.wldos.cms.repo.ContentRepo;
import com.wldos.cms.repo.TermTypeRepo;
import com.wldos.cms.vo.Term;
import com.wldos.support.util.NameConvert;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.core.service.DomainService;
import com.wldos.system.enums.RedisKeyEnum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分类操作service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@Service
@Transactional(rollbackFor = Exception.class)
public class TermService extends BaseService<TermRepo, KTerms, Long> {

	private final BeanCopier termCopier = BeanCopier.create(Term.class, KTerms.class, false);
	private final BeanCopier termTypeCopier = BeanCopier.create(Term.class, KTermType.class, false);

	@Value("${wldos.cms.defaultTermTypeId}")
	private Long defaultTermTypeId;

	private final TermTypeRepo termTypeRepo;

	private final ContentRepo contentRepo;

	private final TermObjectRepo termObjectRepo;

	private final DomainService domainService;

	private final TermObjectService termObjectService;

	public TermService(TermTypeRepo termTypeRepo, ContentRepo contentRepo, TermObjectRepo termObjectRepo, DomainService domainService, TermObjectService termObjectService) {
		this.termTypeRepo = termTypeRepo;
		this.contentRepo = contentRepo;
		this.termObjectRepo = termObjectRepo;
		this.domainService = domainService;
		this.termObjectService = termObjectService;
	}
	
	public PageableResult<TermTree> queryTermForTree(KTerms term, PageQuery pageQuery) {
		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder(50).append("select a.*, o.id term_type_id, o.class_type, o.content_id, o.description,")
				.append(" o.parent_id, o.count from k_terms a join k_term_type o on ")
				.append("a.id=o.term_id where 1=1 ");
		if (!condition.isEmpty()) {
			StringBuilder finalSql1 = new StringBuilder();
			condition.forEach((key, value) -> {
				if (ObjectUtil.isBlank(value))
					return;
				try {
					Field field = ReflectionUtils.findRequiredField(term.getClass(), key);
					if (field.getType().equals(String.class)) {
						finalSql1.append(" and instr(a.").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
					}
					else if (field.getType().equals(Timestamp.class)) {
						finalSql1.append(" and date_format(a.").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = date_format(?, '%Y-%m-%d') ");
					}
					else {
						finalSql1.append(" and a.").append(NameConvert.humpToUnderLine(key)).append("= ? ");
					}
				}
				catch (IllegalArgumentException e) {
					Field field = ReflectionUtils.findRequiredField(KTermType.class, key);
					if (field.getType().equals(String.class)) {
						finalSql1.append(" and instr(o.").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
					}
					else if (field.getType().equals(Timestamp.class)) {
						finalSql1.append(" and date_format(o.").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = date_format(?, '%Y-%m-%d') ");
					}
					else {
						finalSql1.append(" and o.").append(NameConvert.humpToUnderLine(key)).append("= ? ");
					}
				}
				params.add(value);
			});
			sql.append(finalSql1);
		}

		if (filter != null && !filter.isEmpty()) {
			StringBuffer finalSql = new StringBuffer();
			filter.forEach((key, value) -> {
				finalSql.append(" and o.").append(NameConvert.humpToUnderLine(key)).append(" in (");
				value.forEach(item -> {
					finalSql.append("?,");
					params.add(item);
				});
			});

			sql.append(finalSql).delete(sql.length() - 1, sql.length());

			sql.append(")");
		}

		List<Map<String, Object>> all = this.commonJdbc.getNamedParamJdbcTemplate().getJdbcOperations()
				.queryForList("select count(1) as total from ( " + sql + " ) b", params.toArray());
		int total = all.isEmpty() ? 0 : Integer.parseInt(ObjectUtil.string(all.get(0).get("total")));

		if (!sort.isEmpty()) {
			StringBuffer temp = new StringBuffer(" order by ");
			sort.stream().iterator().forEachRemaining(s ->
				temp.append(NameConvert.humpToUnderLine(s.getProperty())).append(" ").append(s.isAscending() ? "" : " desc ")
			);
			sql.append(temp);
		}

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<TermTree> list = this.commonJdbc.execQueryForPage(TermTree.class, sql.toString(), currentPage, pageSize, params.toArray());

		list = TreeUtil.build(list, Constants.TOP_TERM_ID);

		return new PageableResult<>(total, currentPage, pageSize, list);
	}
	
	public List<Term> findAllCategory() {
		String key = RedisKeyEnum.WLDOS_TERM.toString();
		String value = ObjectUtil.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				List<Term> terms = this.entityRepo.findAllByClassType(TermTypeEnum.CATEGORY.toString());

				if (ObjectUtil.isBlank(terms))
					return new ArrayList<>();
				value = om.writeValueAsString(terms);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return terms;
			}

			return om.readValue(value, new TypeReference<List<Term>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取所有业务分类异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}
	
	public List<Term> findAllCategory(String domain) {
		List<Long> typeIds = this.domainService.queryTermTypeIdsByDomain(domain);
		List<Long> childIds = this.queryChildIds(typeIds);

		return this.entityRepo.findAllByClassType(childIds);
	}

	
	public List<Long> queryChildIds(List<Long> termTypeIds) {
		List<LevelNode> nodes = new ArrayList<>();
		for (Long id : termTypeIds) {
			List<LevelNode> temp = this.queryTermTreeByParentId(id);
			nodes.addAll(temp);
		}


		return nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
	}
	
	public List<Long> queryChildIds(Long termTypeId) {
		List<LevelNode> nodes = this.queryTermTreeByParentId(termTypeId);

		return nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
	}
	
	public List<Term> findAllTag() {
		String key = RedisKeyEnum.WLDOS_TAG.toString();
		String value = ObjectUtil.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				List<Term> terms = this.entityRepo.findAllByClassType(TermTypeEnum.TAG.toString());

				if (ObjectUtil.isBlank(terms))
					return new ArrayList<>();
				value = om.writeValueAsString(terms);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return terms;
			}

			return om.readValue(value, new TypeReference<List<Term>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取所有标签异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}
	
	public List<TermObject> findTermByObjectId(List<Long> objIds, String termType) {
		List<KTermObject> termObjects = this.termObjectRepo.findAllByObjectIdIn(objIds);

		List<Term> terms = TermTypeEnum.CATEGORY.toString().equals(termType) ? this.findAllCategory() : this.findAllTag();

		Map<Long, Term> termMap = terms.parallelStream().collect(Collectors.toMap(Term::getTermTypeId, t -> t, (k1, k2) -> k1));

		Map<Long, List<KTermObject>> collect = termObjects.parallelStream().collect(Collectors.groupingBy(KTermObject::getObjectId));
		return collect.entrySet().parallelStream().map(c -> {
			List<Term> termList = c.getValue().parallelStream().map(o -> termMap.get(o.getTermTypeId())).collect(Collectors.toList());
			return new TermObject(c.getKey(), termList);
		}).collect(Collectors.toList());
	}
	
	public List<Term> findCategoryByContCode(String contType) {
		KModelContent modelContent = this.contentRepo.findByContType(contType);
		return this.entityRepo.findByContType(TermTypeEnum.CATEGORY.toString(), modelContent.getId());
	}
	
	public List<Term> findTagByContCode(String contCode) {
		KModelContent modelContent = this.contentRepo.findByContType(contCode);
		return this.entityRepo.findByContType(TermTypeEnum.TAG.toString(), modelContent.getId());
	}
	
	public void addTerm(Term term) {

		KTerms kTerm = new KTerms();
		this.termCopier.copy(term, kTerm, null);
		kTerm.setId(this.nextId());
		this.insertSelective(kTerm);

		KTermType termType = new KTermType();
		this.termTypeCopier.copy(term, termType, null);
		termType.setId(kTerm.getId());
		termType.setTermId(kTerm.getId());
		this.insertOtherEntitySelective(termType);
	}
	
	public void updateTerm(Term term) {
		KTerms kTerms = new KTerms();
		this.termCopier.copy(term, kTerms, null);
		this.update(kTerms);

		KTermType termType = new KTermType();
		this.termTypeCopier.copy(term, termType, null);
		termType.setId(term.getTermTypeId());
		this.updateOtherEntity(termType);
	}
	
	public void deleteTerm(Term term) {

		List<KTermObject> termObjects = this.termObjectRepo.findAllByTermTypeId(term.getTermTypeId());
		List<Long> ids = termObjects.parallelStream().map(KTermObject::getId).collect(Collectors.toList());
		this.deleteByIds(termObjects.get(0), ids, false);

		this.deleteByIds(new KTerms(), term.getId(), false);

		this.deleteByIds(new KTermType(), term.getTermTypeId(), false);
	}
	
	public void deleteTerms(List<Map<String, Object>> terms) {
		for (Map<String, Object> term: terms) {
			Term t = new Term();
			t.setId(Long.parseLong(term.get("id").toString()));
			t.setTermTypeId(Long.parseLong(term.get("termTypeId").toString()));
			this.deleteTerm(t);
		}
	}
	
	public void relTermObject(KTermObject termObject) {

		this.insertOtherEntitySelective(termObject);

		this.termTypeRepo.countPlus(termObject.getTermTypeId());
	}
	
	public KModelContent queryContentTypeByTermType(Long termTypeId) {
		return this.termTypeRepo.queryContentTypeByTermType(termTypeId);
	}
	
	public List<Long> findAllByObjectId(Long pid) {
		List<KTermObject> termObjects = this.termObjectRepo.findAllByObjectId(pid);
		if (ObjectUtil.isBlank(termObjects)) {

			this.saveTermObject(this.defaultTermTypeId, pid);
			List<Long> termTypeIds = new ArrayList<>();
			termTypeIds.add(this.defaultTermTypeId);
			return termTypeIds;
		}
		return termObjects.parallelStream().map(KTermObject::getTermTypeId).collect(Collectors.toList());
	}
	
	public List<Term> findAllByObjectAndClassType(Long objectId, String classType) {
		List<Term> terms = this.entityRepo.findAllByObjectAndClassType(objectId, classType);
		if (TermTypeEnum.CATEGORY.toString().equals(classType) && ObjectUtil.isBlank(terms)) {

			this.saveTermObject(this.defaultTermTypeId, objectId);
			terms = new ArrayList<>();
			Term term = this.entityRepo.queryTermByTermTypeId(this.defaultTermTypeId);
			terms.add(term);
		}

		return terms;
	}
	
	public void saveTermObject(Long termTypeId, Long pId) {

		KTermObject termObject = new KTermObject();
		termObject.setId(this.nextId());
		termObject.setTermTypeId(termTypeId);
		termObject.setObjectId(pId);
		termObject.setTermOrder(0L);

		this.relTermObject(termObject);
	}

	public void saveTermObject(List<Long> termTypeIds, Long pId) {

		List<KTermObject> termObjects = termTypeIds.parallelStream().map(tId -> {
			KTermObject termObject = new KTermObject();
			termObject.setId(this.nextId());
			termObject.setTermTypeId(tId);
			termObject.setObjectId(pId);
			termObject.setTermOrder(0L);

			return termObject;
		}).collect(Collectors.toList());

		this.termObjectService.insertSelectiveAll(termObjects);

		this.termTypeRepo.countPlus(termTypeIds);
	}

	public KTermType queryTermTypeBySlug(String slugCategory) {
		return this.entityRepo.queryTermTypeBySlug(slugCategory);
	}
	
	public Term queryTermBySlugTerm(String slugTerm) {
		return this.entityRepo.queryTermBySlugTerm(slugTerm);
	}
	
	public List<KTerms> queryKTermsByTermTypeIds(List<Long> termTypeIds) {

		return this.entityRepo.queryKTermsByTermTypeIds(termTypeIds);
	}
	
	public List<Term> queryAllByTermTypeIds(List<Long> termTypeIds) {
		if (ObjectUtil.isBlank(termTypeIds))
			return new ArrayList<>();
		return this.entityRepo.queryAllTermsByTermTypeIds(termTypeIds);
	}
	
	public List<LevelNode> queryTermTreeByParentId(Object pId) {
		String table = "k_term_type";
		String key = RedisKeyEnum.WLDOS_TERM.toString() + table + pId + "child";
		String value = ObjectUtil.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				List<LevelNode> levelNodes = this.queryTreeByParentId(table, pId);

				if (ObjectUtil.isBlank(levelNodes))
					return new ArrayList<>();

				value = om.writeValueAsString(levelNodes);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return levelNodes;
			}

			return om.readValue(value, new TypeReference<List<LevelNode>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取子分类异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}
	
	public List<LevelNode> queryTermTreeByChildId(Object cId) {
		String table = "k_term_type";
		String key = RedisKeyEnum.WLDOS_TERM.toString() + table + cId + "parent";
		String value = ObjectUtil.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				List<LevelNode> levelNodes = this.queryTreeByChildId(table, cId);

				if (ObjectUtil.isBlank(levelNodes))
					return new ArrayList<>();

				value = om.writeValueAsString(levelNodes);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return levelNodes;
			}

			return om.readValue(value, new TypeReference<List<LevelNode>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取父分类异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}
	
	public void refreshTerm() {
		this.cache.removeByPrefix(RedisKeyEnum.WLDOS_TERM.toString());
		this.cache.remove(RedisKeyEnum.WLDOS_TAG.toString());
	}

	public List<Object> findAllTermTypeIdsByDomain(String domain) {
		List<Long> termTypeIds = this.domainService.queryTermTypeIdsByDomain(domain);

		return this.queryOwnIds(termTypeIds);
	}

	private List<Object> queryOwnIds(List<Long> termTypeIds) {
		List<LevelNode> nodes = new ArrayList<>();
		for (Long id : termTypeIds) {
			List<LevelNode> temp = this.queryTermTreeByParentId(id);
			nodes.addAll(temp);
		}

		return nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
	}
}