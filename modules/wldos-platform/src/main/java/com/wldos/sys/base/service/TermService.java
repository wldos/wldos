/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.base.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.base.RepoService;
import com.wldos.base.entity.EntityAssists;
import com.wldos.common.Constants;
import com.wldos.common.dto.LevelNode;
import com.wldos.common.enums.BoolEnum;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ChineseUtils;
import com.wldos.common.utils.NameConvert;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.TreeUtils;
import com.wldos.common.vo.SelectOption;
import com.wldos.common.vo.TreeNode;
import com.wldos.common.vo.TreeSelectOption;
import com.wldos.support.term.TermOpener;
import com.wldos.support.term.dto.Term;
import com.wldos.support.term.enums.TermTypeEnum;
import com.wldos.sys.base.dto.TermObject;
import com.wldos.sys.base.entity.KTermObject;
import com.wldos.sys.base.entity.KTermType;
import com.wldos.sys.base.entity.KTerms;
import com.wldos.sys.base.repo.TermObjectRepo;
import com.wldos.sys.base.repo.TermRepo;
import com.wldos.sys.base.repo.TermTypeRepo;
import com.wldos.sys.base.vo.Category;
import com.wldos.sys.base.vo.TermTree;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

/**
 * 分类操作service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class TermService extends RepoService<TermRepo, KTerms, Long> implements TermOpener {

	private final BeanCopier termCopier = BeanCopier.create(Term.class, KTerms.class, false);

	private final BeanCopier termTypeCopier = BeanCopier.create(Term.class, KTermType.class, false);

	@Value("${wldos_cms_defaultTermTypeId:1}")
	private Long defaultTermTypeId;

	private final TermTypeRepo termTypeRepo;

	private final TermObjectRepo termObjectRepo;

	private final TermObjectService termObjectService;

	public TermService(TermTypeRepo termTypeRepo, TermObjectRepo termObjectRepo, TermObjectService termObjectService) {
		this.termTypeRepo = termTypeRepo;
		this.termObjectRepo = termObjectRepo;
		this.termObjectService = termObjectService;
	}

	/**
	 * 通过实体bean、组合查询，查询分类树，支持分页、查询条件和排序
	 *
	 * @param term 实体Bean作为主表，子表及其实体在函数体内部体现
	 * @param pageQuery 分页参数
	 * @param isPage 是否分页，分类树状表格暂不需要分页，也可以改成左树右表两个组件的组合联动，标签列表没有层次关系可以分页
	 * @return 与sql列匹配的vo树
	 */
	public PageableResult<TermTree> queryTermForTree(KTerms term, PageQuery pageQuery, boolean isPage) {
		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();

		List<Object> params = new ArrayList<>(); // @todo 此处应考虑term metadata扩展
		StringBuilder sql = new StringBuilder(50).append("select a.*, o.id term_type_id, o.class_type, o.description,")
				.append(" o.parent_id, o.count from k_terms a join k_term_type o on a.id=o.term_id where 1=1 ");
		if (!condition.isEmpty()) {
			StringBuilder finalSql1 = new StringBuilder();
			condition.forEach((key, value) -> {
				if (ObjectUtils.isBlank(value))
					return;
				Field field = ReflectionUtils.findField(term.getClass(), key);
				if (field == null) {
					field = ReflectionUtils.findField(KTermType.class, key);
					if (field == null)
						return;
					if (field.getType().equals(String.class)) {
						finalSql1.append(" and instr(o.").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
					}
					else if (field.getType().equals(Timestamp.class)) { // 日期默认匹配当天
						finalSql1.append(" and date_format(o.").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = date_format(?, '%Y-%m-%d') ");
					}
					else { // 包含所有的数字类型、日期类型。日期等的有界查询请移步前面自定义sql方法。
						finalSql1.append(" and o.").append(NameConvert.humpToUnderLine(key)).append("= ? ");
					}
					params.add(value);
					return;
				}
				if (field.getType().equals(String.class)) {
					finalSql1.append(" and instr(a.").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
				}
				else if (field.getType().equals(Timestamp.class)) { // 日期默认匹配当天
					finalSql1.append(" and date_format(a.").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = date_format(?, '%Y-%m-%d') ");
				}
				else { // 包含所有的数字类型、日期类型。日期等的有界查询请移步前面自定义sql方法。
					finalSql1.append(" and a.").append(NameConvert.humpToUnderLine(key)).append("= ? ");
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

		List<Map<String, Object>> all = this.commonOperate.getJdbcOperations()
				.queryForList("select count(1) as total from ( " + sql + " ) b", params.toArray());
		int total = all.isEmpty() ? 0 : Integer.parseInt(ObjectUtils.string(all.get(0).get("total")));

		if (!sort.isEmpty()) {
			StringBuffer temp = new StringBuffer(" order by ");
			sort.stream().iterator().forEachRemaining(s ->
					temp.append(NameConvert.humpToUnderLine(s.getProperty())).append(" ").append(s.isAscending() ? "" : " desc ")
			);
			sql.append(temp);
		}

		List<TermTree> list;

		if (isPage) { // 查询分类项列表分页
			int totalPageNum = (total - 1) / pageSize + 1;
			currentPage = Math.min(currentPage, totalPageNum);
			list = this.commonOperate.execQueryForPage(TermTree.class, sql.toString(), currentPage, pageSize, params.toArray());
		}
		else { // 查询分类树
			list = this.commonOperate.getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(TermTree.class), params.toArray());

			list = TreeUtils.build(list, Constants.TOP_TERM_ID);
		}

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	/**
	 * 查询某域下的所有分类（默认按展示顺序排序）
	 *
	 * @return 分类项集合
	 */
	public List<Term> findAllCategory() {
		String key = RedisKeyEnum.WLDOS_TERM.toString();
		String value = ObjectUtils.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<Term> terms = this.entityRepo.findAllByClassType(TermTypeEnum.CATEGORY.toString());

				if (ObjectUtils.isBlank(terms))
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

	/**
	 * 查询所有标签
	 *
	 * @return 分类项集合
	 */
	public List<Term> findAllTag() {
		String key = RedisKeyEnum.WLDOS_TAG.toString();
		String value = ObjectUtils.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<Term> terms = this.entityRepo.findAllByClassType(TermTypeEnum.TAG.toString());

				if (ObjectUtils.isBlank(terms))
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

	/**
	 * 查询全平台的大类、小类分类树，信息发布专用
	 * 为了规避不需要在门户展现的分类，检查信息发布打开标记
	 *
	 * @return 分类树
	 */
	public List<Category> queryFlatCategoryTree() {
		String key = RedisKeyEnum.WLDOS_TERM_TREE.toString();
		String value = ObjectUtils.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {

				List<Term> allTerms = this.findAllCategory();

				// 全站分类树，为了规避不需要在门户展现的分类过滤
				List<Category> viewNodes = allTerms.parallelStream().filter(v -> BoolEnum.YES.toString().equals(v.getInfoFlag()))
						.map(res ->
								Category.of(res.getId(), res.getParentId(), res.getName(), res.getId().toString(), res.getSlug(), res.getDisplayOrder())).collect(Collectors.toList());

				viewNodes = TreeUtils.buildFlatTree(viewNodes, Constants.TOP_TERM_ID);
				// 没有孩子的无效，过滤掉
				viewNodes = viewNodes.parallelStream().filter(v -> v.getChildren() != null).collect(Collectors.toList());

				if (ObjectUtils.isBlank(viewNodes))
					return new ArrayList<>();

				value = om.writeValueAsString(viewNodes);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return viewNodes;
			}

			return om.readValue(value, new TypeReference<List<Category>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取域的分类树异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}

	/**
	 * 查询全平台的分层分类树
	 *
	 * @return 分类树
	 */
	public List<TreeSelectOption> queryLayerCategoryTree() {
		String key = RedisKeyEnum.WLDOS_TERM_TREE_LAYER.toString();
		String value = ObjectUtils.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {

				List<Term> allTerms = this.findAllCategory();

				// 全站分类树
				List<TreeSelectOption> infoCats = allTerms.parallelStream().map(term -> {
					Long termTypeId = term.getTermTypeId();
					return TreeSelectOption.of(termTypeId, term.getParentId(), term.getName(), termTypeId.toString(), termTypeId.toString(), term.getDisplayOrder());
				}).collect(Collectors.toList());

				String topTermId = String.valueOf(Constants.TOP_TERM_ID);
				// 新增根分类
				TreeSelectOption infoCate = TreeSelectOption.of(Constants.TOP_TERM_ID, Constants.TOP_VIR_ID, "根分类", topTermId, topTermId, 0L);

				infoCats.add(0, infoCate);

				infoCats = TreeUtils.build(infoCats, Constants.TOP_VIR_ID);

				if (infoCats.isEmpty())
					return infoCats;

				value = om.writeValueAsString(infoCats);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return infoCats;
			}

			return om.readValue(value, new TypeReference<List<TreeSelectOption>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取分层分类树异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}

	/**
	 * 根据父分类id查询子分类（含自身）
	 *
	 * @param parentId 父分类id
	 * @return 分类列表
	 */
	public List<Category> queryCategoriesFromPid(Long parentId) {
		List<LevelNode> nodes = this.queryTermTreeByParentId(parentId);
		// 分类类型id用于从对象分类关联表[存档]中作筛选分类条件，filter: termTypeIds ? {termTypeId: termTypeIds} : {}
		List<Long> typeIds = nodes.stream().map(LevelNode::getId).collect(Collectors.toList());
		if (ObjectUtils.isBlank(typeIds))
			return new ArrayList<>();
		List<Term> terms = this.entityRepo.queryAllTermsByTermTypeIds(typeIds);
		if (terms == null)
			return new ArrayList<>();

		// 排序
		return terms.stream().map(res -> {
			Category c = Category.of(res.getTermTypeId(), res.getParentId(), res.getName(), res.getTermTypeId().toString(), res.getSlug(), res.getDisplayOrder());
			if (c.getId().equals(parentId)) { // 父节点居首位
				c.setDisplayOrder(0L);
			}
			return c;
		}).sorted(Comparator.nullsLast(
				Comparator.comparing(
						TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo)))).collect(Collectors.toList());
	}

	/**
	 * 根据父分类别名查询子分类（含自身）
	 *
	 * @param slugTerm 父分类别名
	 * @return 分类列表
	 */
	public List<Category> queryCategoriesFromPlug(String slugTerm) {
		Term term = this.queryTermBySlugTerm(slugTerm);
		if (term == null)
			return new ArrayList<>();

		// 分类类型id用于从对象分类关联表[存档]中作筛选分类条件，filter: termTypeIds ? {termTypeId: termTypeIds} : {}
		List<Long> typeIds;
		boolean isTop = Constants.TOP_TERM_SLUG.equals(slugTerm);
		if (isTop) {
			typeIds = this.termTypeRepo.queryIdsByParentId(term.getTermTypeId(), TermTypeEnum.CATEGORY.toString());
		} else {
			List<LevelNode> nodes = this.queryTermTreeByParentId(term.getTermTypeId());
			typeIds = nodes.stream().map(LevelNode::getId).collect(Collectors.toList());
		}

		if (ObjectUtils.isBlank(typeIds))
			return new ArrayList<>();

		List<Term> terms = this.entityRepo.queryAllTermsByTermTypeIds(typeIds);
		if (terms == null)
			return new ArrayList<>();

		// 排序
		if (isTop) {
			terms.add(term);
			return terms.stream().filter(v -> BoolEnum.YES.toString().equals(v.getInfoFlag())).map(res ->
					Category.of(res.getTermTypeId(), res.getParentId(), res.getName(), res.getTermTypeId().toString(), res.getSlug(), res.getDisplayOrder()))
					.sorted(Comparator.nullsLast(
							Comparator.comparing(
									TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo)))).collect(Collectors.toList());
		}

		return terms.stream().filter(v -> BoolEnum.YES.toString().equals(v.getInfoFlag())).map(res -> {
			Category c = Category.of(res.getTermTypeId(), res.getParentId(), res.getName(), res.getTermTypeId().toString(), res.getSlug(), res.getDisplayOrder());
			if (c.getId().equals(term.getTermTypeId())) { // 父节点居首位
				c.setDisplayOrder(0L);
			}
			return c;
		}).sorted(Comparator.nullsLast(
				Comparator.comparing(
						TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo)))).collect(Collectors.toList());
	}

	/**
	 * 查询顶级分类
	 *
	 * @return 顶级分类下拉列表
	 */
	public List<SelectOption> queryTopCategories() {
		List<Term> terms = this.findAllCategory();
		return terms.parallelStream().filter(t -> t.getParentId().equals(Constants.TOP_TERM_ID)).map(term -> SelectOption.of(term.getName(), term.getTermTypeId().toString())).collect(Collectors.toList());
	}

	/**
	 * 查询平台标签列表
	 *
	 * @return 标签列表
	 */
	public List<SelectOption> queryFlatTags() {
		String key = RedisKeyEnum.WLDOS_TAG_OPT.toString();
		String value = ObjectUtils.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {

				List<Term> allTerms = this.findAllTag();

				// 全站标签, 标签特殊处理(label、value相等)
				List<SelectOption> options = allTerms.parallelStream().map(term -> SelectOption.of(term.getName(), term.getName())).collect(Collectors.toList());

				if (options.isEmpty())
					return options;

				value = om.writeValueAsString(options);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return options;
			}

			return om.readValue(value, new TypeReference<List<SelectOption>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存平台标签列表异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}

	/**
	 * 追加标签缓存(用于新增)
	 *
	 * @param newValue 新增标签
	 */
	public void appendCacheTag(Term newValue) {
		if (newValue == null)
			return;

		String key = RedisKeyEnum.WLDOS_TAG.toString();

		try {
			List<Term> terms = this.findAllTag();
			terms.add(newValue);

			String value = new ObjectMapper().writeValueAsString(terms);

			this.cache.set(key, value, 12, TimeUnit.HOURS);
		}
		catch (JsonProcessingException e) {
			getLog().error("追加标签缓存异常: {}", e.getMessage());
		}
	}

	/**
	 * 追加标签缓存(用于新增)
	 *
	 * @param termList 新增标签集合
	 */
	public void appendCacheTag(List<Term> termList) {
		if (ObjectUtils.isBlank(termList))
			return;

		String key = RedisKeyEnum.WLDOS_TAG.toString();

		try {
			List<Term> terms = this.findAllTag();
			terms.addAll(termList);

			String value = new ObjectMapper().writeValueAsString(terms);

			this.cache.set(key, value, 12, TimeUnit.HOURS);
		}
		catch (JsonProcessingException e) { // 此处进行异常处理不抛出的原因在于：标签加缓存不关键，缓存过期后也会自动更新
			getLog().error("追加标签缓存异常: {}", e.getMessage());
		}
	}

	/**
	 * 批量查询分类及其所有子类的id
	 *
	 * @param termTypeIds 待取子分类id列表
	 * @return 命中分类id列表
	 */
	public List<Long> queryChildIds(List<Long> termTypeIds) {
		List<LevelNode> nodes = new ArrayList<>();
		for (Long id : termTypeIds) {
			List<LevelNode> temp = this.queryTermTreeByParentId(id);
			nodes.addAll(temp);
		}

		// 分类类型id用于从对象分类关联表[存档]中作筛选分类条件，filter: termTypeIds ? {termTypeId: termTypeIds} : {}
		return nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
	}

	/**
	 * 查询分类及其子节点id
	 *
	 * @param termTypeId 分类id
	 * @return 分类及其子分类id
	 */
	public List<Long> queryChildIds(Long termTypeId) {
		List<LevelNode> nodes = this.queryTermTreeByParentId(termTypeId);
		// 分类类型id用于从对象分类关联表[存档]中作筛选分类条件，filter: termTypeIds ? {termTypeId: termTypeIds} : {}
		return nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
	}

	/**
	 * 根据批量对象id和分类法类型取出对象对应的所有分类项信息
	 *
	 * @param objIds 批量对象id
	 * @param termType 分类法类型
	 * @return 对象的分类项信息列表
	 */
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

	/**
	 * 后台管理员新增一个分类或标签
	 *
	 * @param term 分类项
	 * @param userId 操作人id
	 * @param userIp 操作人ip
	 */
	public String addTerm(Term term, Long userId, String userIp) {

		if (this.entityRepo.existsTermBySlugOrName(term.getName(), term.getSlug()))
			return "同名或同别名的分类项已存在"; // 简单处理：分类和标签也不能重名，无论名字还是别名

		// 新增term
		KTerms kTerm = new KTerms();
		this.termCopier.copy(term, kTerm, null);
		EntityAssists.beforeInsert(kTerm, this.nextId(), userId, userIp, false);
		this.insertSelective(kTerm);
		// 新增term type
		KTermType termType = new KTermType();
		this.termTypeCopier.copy(term, termType, null);
		termType.setId(kTerm.getId());
		termType.setTermId(kTerm.getId());
		this.insertOtherEntitySelective(termType);
		return "ok";
	}

	/**
	 * 更新分类或标签
	 *
	 * @param term 分类项
	 * @param userId 操作人id
	 * @param userIp 操作人ip
	 */
	public String updateTerm(Term term, Long userId, String userIp) {
		if (this.entityRepo.existsDifTermByNameOrSlugAndId(term.getName(), term.getSlug(), term.getId()))
			return "同名或同别名的分类项已存在"; // 简单处理：分类和标签也不能重名，无论名字还是别名
		KTerms kTerms = new KTerms();
		this.termCopier.copy(term, kTerms, null);
		EntityAssists.beforeUpdated(kTerms, userId, userIp);
		this.update(kTerms);

		KTermType termType = new KTermType();
		this.termTypeCopier.copy(term, termType, null);
		termType.setId(term.getTermTypeId());
		this.updateOtherEntity(termType);
		return "ok";
	}

	/**
	 * 批量新增分类或标签
	 *
	 * @param terms 分类项集合, 已设置id
	 */
	private void batchAddTerm(List<Term> terms, Long userId, String userIp) {
		List<KTerms> kTerms = new ArrayList<>();
		List<KTermType> kTermTypes = new ArrayList<>();
		for (Term term : terms) {
			KTerms kTerm = new KTerms();
			this.termCopier.copy(term, kTerm, null);
			EntityAssists.beforeInsert(kTerm, kTerm.getId(), userId, userIp, false);
			kTerms.add(kTerm);

			KTermType termType = new KTermType();
			this.termTypeCopier.copy(term, termType, null);
			termType.setId(term.getTermTypeId());
			termType.setTermId(term.getId());
			kTermTypes.add(termType);
		}
		// 新增term
		this.insertSelectiveAll(kTerms);
		// 新增term type
		this.insertOtherEntitySelective(kTermTypes);
	}

	/**
	 * 删除分类
	 *
	 * @param term 分类项
	 */
	public void deleteTerm(Term term) {
		// 包含下级分类，不允许删除
		if (this.entityRepo.existsTermByTermTypeId(term.getTermTypeId())) {
			getLog().warn("包含下级分类，不允许删除，请确定是合法请求：termId={}", term.getId());
		}
		// 类目或标签下的内容关联关系级联删除，相关内容如果没有其他分类，将默认按未分类处理，归属‘未分类’目录
		List<KTermObject> termObjects = this.termObjectRepo.findAllByTermTypeId(term.getTermTypeId());
		if (!ObjectUtils.isBlank(termObjects)) {
			this.deleteByEntityAndIds(termObjects.get(0), false, termObjects.parallelStream().map(KTermObject::getId).toArray());
		}

		// 删除term
		this.deleteByEntityAndIds(new KTerms(), false, term.getId());

		// 删除term type
		this.deleteByEntityAndIds(new KTermType(), false, term.getTermTypeId());
	}

	/**
	 * 批量删除分类
	 *
	 * @param terms 分类项集合
	 */
	public void deleteTerms(List<Term> terms) {
		for (Term term : terms) {
			this.deleteTerm(term);
		}
	}

	/**
	 * 关联发布内容、分类
	 *
	 * @param termObject 分类关系
	 */
	public void relTermObject(KTermObject termObject) {
		// 数据保存
		this.insertOtherEntitySelective(termObject);
		// 计数+1
		this.termTypeRepo.countPlus(termObject.getTermTypeId());
	}

	/**
	 * 根据对象id查询归属的分类关系列表（包含所有分类法：分类目录、标签、其他，请慎用！），单纯取分类或标签，请使用<code>findAllByObjectAndClassType</code>
	 *
	 * @param pid 对象id
	 * @return 分类id列表，涉及所有分类法：分类目录、标签、其他
	 */
	public List<Long> findAllByObjectId(Long pid) {
		List<KTermObject> termObjects = this.termObjectRepo.findAllByObjectId(pid);
		if (ObjectUtils.isBlank(termObjects)) { // 没有分类，即未分组，一律归到默认分类目录下（一般默认分类是“未分组”）
			// 修复无分类对象
			this.saveTermObject(this.defaultTermTypeId, pid);
			List<Long> termTypeIds = new ArrayList<>();
			termTypeIds.add(this.defaultTermTypeId);
			return termTypeIds;
		}
		return termObjects.parallelStream().map(KTermObject::getTermTypeId).collect(Collectors.toList());
	}

	/**
	 * 根据对象id、分类类型查询某一类型的分类项列表
	 *
	 * @param objectId 对象id
	 * @param classType 分类类型：分类目录、标签等
	 * @return 分类项列表，不会为空，默认未分组
	 */
	public List<Term> findAllByObjectAndClassType(Long objectId, String classType) {
		List<Term> terms = this.entityRepo.findAllByObjectAndClassType(objectId, classType);
		if (TermTypeEnum.CATEGORY.toString().equals(classType) && ObjectUtils.isBlank(terms)) { // 没有分类，即未分组，一律归到默认分类目录下（一般默认分类是“未分组”）
			// 修复无分类对象, 不同域
			this.saveTermObject(this.defaultTermTypeId, objectId);
			terms = new ArrayList<>();
			Term term = this.entityRepo.queryTermByTermTypeId(this.defaultTermTypeId);
			terms.add(term);
		}

		return terms;
	}

	/**
	 * 保存分类关系，并增加计数
	 *
	 * @param termTypeId 分类id
	 * @param pId 发布内容id
	 */
	public void saveTermObject(Long termTypeId, Long pId) {
		// 关联帖子分类并计数
		KTermObject termObject = new KTermObject();
		termObject.setId(this.nextId());
		termObject.setTermTypeId(termTypeId);
		termObject.setObjectId(pId);
		termObject.setTermOrder(0L);

		this.relTermObject(termObject);
	}

	/**
	 * 批量保存分类关系，并增加计数
	 *
	 * @param termTypeIds 分类id
	 * @param pId 帖子id
	 */
	public void saveTermObject(List<Long> termTypeIds, Long pId) {
		if (ObjectUtils.isBlank(termTypeIds))
			return;

		// 批量封装
		List<KTermObject> termObjects = termTypeIds.parallelStream().map(tId -> {
			KTermObject termObject = new KTermObject();
			termObject.setId(this.nextId());
			termObject.setTermTypeId(tId);
			termObject.setObjectId(pId);
			termObject.setTermOrder(0L);

			return termObject;
		}).collect(Collectors.toList());

		// 数据保存
		this.termObjectService.insertSelectiveAll(termObjects);
		// 计数+1
		this.termTypeRepo.countPlus(termTypeIds);
	}

	/**
	 * 批量更新分类关系，并增加计数
	 *
	 * @param termTypeIds 分类id
	 * @param termType 分类法类型：分类目录、标签等，见TermTypeEnum
	 * @param pId 帖子id
	 */
	public void updateTermObject(List<Long> termTypeIds, Long pId, String termType) {
		if (ObjectUtils.isBlank(termTypeIds))
			return;
		// 取出帖子已存在分类
		List<Term> termObjects = this.findAllByObjectAndClassType(pId, termType);
		List<Long> oriIds = termObjects.stream().map(Term::getTermTypeId).collect(Collectors.toList());
		// 取本次取消的分类
		List<Long> delIds = oriIds.stream().filter(termTypeId -> !termTypeIds.contains(termTypeId)).collect(Collectors.toList());
		// 取新增的分类
		List<Long> addIds = termTypeIds.stream().filter(t -> !oriIds.contains(t)).collect(Collectors.toList());

		// 先删除本次取消的分类
		if (!delIds.isEmpty()) {
			this.termObjectRepo.deleteAllByTermTypeIdAndObjectId(delIds, pId);
			this.termTypeRepo.countSubtract(delIds);
		}

		if (addIds.isEmpty())
			return;

		// 再增加新增分类，批量封装
		List<KTermObject> tObjects = addIds.parallelStream().map(tId ->
				KTermObject.of(this.nextId(), tId, pId, 0L)).collect(Collectors.toList());

		// 数据保存
		this.termObjectService.insertSelectiveAll(tObjects);
		// 计数+1
		this.termTypeRepo.countPlus(termTypeIds);
	}

	/**
	 * 根据分类别名查询某域下分类的类型
	 *
	 * @param slugCategory 分类项别名
	 * @return 分类类型实体
	 */
	public KTermType queryTermTypeBySlug(String slugCategory) {
		return this.entityRepo.queryTermTypeBySlug(slugCategory);
	}

	/**
	 * 根据分类类型id查询分类类型
	 *
	 * @param termTypeId 分类类型id
	 * @return 分类类型实体
	 */
	public KTermType queryTermTypeById(Long termTypeId) {
		return this.entityRepo.queryTermTypeById(termTypeId);
	}

	/**
	 * 通过分类项别名查询分类项
	 *
	 * @param slugTerm 分类项别名
	 * @return 分类项信息
	 */
	public Term queryTermBySlugTerm(String slugTerm) {
		return Constants.TOP_TERM_SLUG.equals(slugTerm) ? Term.of(Constants.TOP_TERM_ID, "1", 0L, Constants.TOP_TERM_ID, TermTypeEnum.CATEGORY.toString(),  0L, "所有分类", Constants.TOP_TERM_SLUG)
				: this.entityRepo.queryTermBySlugTerm(slugTerm);
	}

	/**
	 * 根据类型id批量查询所有分类项及其类型
	 *
	 * @param termTypeIds 批量类型id
	 * @return 分类项列表
	 */
	public List<Term> queryAllByTermTypeIds(List<Long> termTypeIds) {
		if (ObjectUtils.isBlank(termTypeIds))
			return new ArrayList<>();
		return this.entityRepo.queryAllTermsByTermTypeIds(termTypeIds);
	}

	/**
	 * 通过父节点id查询所有子节点(含父节点自身)并缓存
	 *
	 * @param pId 要查询的父节点id
	 * @return 一棵节点树
	 */
	public List<LevelNode> queryTermTreeByParentId(Object pId) { // 可以改造为从所有分类的缓存中使用搜索算法查找，需要比较性能再取舍
		String table = "k_term_type";
		String key = RedisKeyEnum.WLDOS_TERM + table + pId + ":child";
		String value = ObjectUtils.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<LevelNode> levelNodes = this.queryTreeByParentId(table, pId);

				if (ObjectUtils.isBlank(levelNodes))
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

	/**
	 * 通过子节点id查询所有父节点(含子节点自身)并缓存
	 *
	 * @param cId 要查询的子节点id
	 * @return 一棵节点树
	 */
	public List<LevelNode> queryTermTreeByChildId(Object cId) {
		String table = "k_term_type";
		String key = RedisKeyEnum.WLDOS_TERM.toString() + table + cId + ":parent";
		String value = ObjectUtils.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<LevelNode> levelNodes = this.queryTreeByChildId(table, cId);

				if (ObjectUtils.isBlank(levelNodes))
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

	/** 清除分类相关的缓存 */
	public void refreshTerm() {
		this.cache.removeByPrefix(RedisKeyEnum.WLDOS_TERM.toString()); // 包含扁平树、分层树
		this.cache.remove(RedisKeyEnum.WLDOS_TAG.toString());
		this.cache.remove(RedisKeyEnum.WLDOS_TAG_OPT.toString());
	}

	/** 清除tag相关的缓存 */
	public void refreshTag() {
		this.cache.remove(RedisKeyEnum.WLDOS_TAG.toString());
		this.cache.remove(RedisKeyEnum.WLDOS_TAG_OPT.toString());
	}

	/**
	 * 批量查询分类及其所有子类的id
	 *
	 * @param termTypeIds 待取子分类id列表
	 * @return 命中分类id列表
	 */
	private List<Object> queryOwnIds(List<Long> termTypeIds) {
		List<LevelNode> nodes = new ArrayList<>();
		for (Long id : termTypeIds) {
			List<LevelNode> temp = this.queryTermTreeByParentId(id);
			nodes.addAll(temp);
		}

		// 分类类型id用于从对象分类关联表[存档]中作筛选分类条件，filter: termTypeIds ? {termTypeId: termTypeIds} : {}
		return nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
	}

	/**
	 * 判断多个分类目录是否合法数据，用于新增作品分类
	 *
	 * @param termTypeIds 分类目录id
	 * @return 是否
	 */
	public boolean isValidTerm(List<Long> termTypeIds) {
		List<KTermType> termTypes = (List<KTermType>) this.termTypeRepo.findAllById(termTypeIds);

		// 非法数据，返回false,用于防止真假数据试探
		return !ObjectUtils.isBlank(termTypes) && termTypes.size() >= termTypeIds.size();
	}

	/**
	 * 批量处理前端自定义标签的保存，已存在的直接返回，不存在的创建新标签返回
	 *
	 * @param tagNames 用户设置的标签
	 * @return 设置标签的id
	 */
	public List<Long> handleTag(List<String> tagNames, Long userId, String userIp) {
		if (ObjectUtils.isBlank(tagNames))
			return new ArrayList<>();
		// 批量查询标签项
		List<Term> terms = this.entityRepo.findAllByNameAndClassType(tagNames, TermTypeEnum.TAG.toString());
		List<String> existsTermNames = terms.stream().map(term -> term.getName().toLowerCase()).collect(Collectors.toList());
		List<Long> tagIds;
		if (terms.size() > 0) {
			// 查找不存在的标签
			List<String> noTagNames = tagNames.stream().filter(name -> !existsTermNames.contains(name.trim().toLowerCase())).collect(Collectors.toList());

			if (ObjectUtils.isBlank(noTagNames)) // 全部存在，无需保存，直接复用
				return terms.stream().map(Term::getTermTypeId).collect(Collectors.toList());
			// 部分存在的标签，直接复用
			tagIds = terms.stream().map(Term::getTermTypeId).collect(Collectors.toList());

			// 不存在的标签先保存，再合并返回
			List<Long> newTags = this.addNewTags(noTagNames, userId, userIp);

			tagIds.addAll(newTags);

			return tagIds;
		}

		// 全部新增
		return this.addNewTags(tagNames, userId, userIp);
	}

	@Value("${wldos_cms_tag_tagLength:30}")
	private int tagLength;

	/*
	 * 新增标签时系统已存在别名则自动加1去重
	 */
	private String existsAutoDifSlugBySlugAndName(String slug, String name) {
		// 同名标签且别名相同，保持原样
		if (this.entityRepo.existsSameTermBySlugAndName(slug, name))
			return slug;
		// 不同标签，别名冲突，必须处理
		if (this.entityRepo.existsDifTermBySlugAndName(slug, name))
			// 存在，自动加1再判断
			return existsAutoDifSlugBySlugAndName(slug + "1", name);
		// 不重复的别名
		return slug;
	}

	// 到这里来的都是库里名字不存在的标签，但是自动生成的拼音别名可能是重复的
	private List<Long> addNewTags(List<String> tagNames, Long userId, String userIp) {
		List<Term> newTerms = tagNames.stream().map(n -> {
			// 标签超长，抛弃
			if (ObjectUtils.isOutBounds(n, this.tagLength)) {
				getLog().info("设置了超长标签，系统自动忽略：{}", n);
				return null;
			}
			// 别名存在的标签，别名自动加1消除冲突
			String slug = this.existsAutoDifSlugBySlugAndName(ChineseUtils.hanZi2Pinyin(n, true), n);

			Long id = this.nextId();
			return Term.of(id, id, TermTypeEnum.TAG.toString(), Constants.TOP_TERM_ID, n, slug);
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (newTerms.isEmpty())
			return new ArrayList<>();

		this.batchAddTerm(newTerms, userId, userIp);

		this.appendCacheTag(newTerms); // 新增标签追加缓存

		return newTerms.stream().map(Term::getTermTypeId).collect(Collectors.toList());
	}

	public Long queryMaxOrder(Long parentId) {
		return this.entityRepo.queryMaxOrder(parentId);
	}

	/**
	 * 批量给分类项设置信息发布状态，设置为开的分类项可以在信息发布门户展示和发布该类信息
	 *
	 * @param termIds 分类项ids
	 */
	public void infoFlagByIds(List<Long> termIds) {
		this.entityRepo.infoFlagByIds(termIds);
	}
}
