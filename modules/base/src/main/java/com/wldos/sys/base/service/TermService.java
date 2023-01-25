/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.base.entity.EntityAssists;
import com.wldos.common.dto.LevelNode;
import com.wldos.common.enums.BoolEnum;
import com.wldos.sys.base.dto.Term;
import com.wldos.sys.base.dto.TermObject;
import com.wldos.sys.base.enums.TermTypeEnum;
import com.wldos.sys.base.repo.TermObjectRepo;
import com.wldos.sys.base.repo.TermTypeRepo;
import com.wldos.sys.base.repo.TermRepo;
import com.wldos.sys.base.vo.Category;
import com.wldos.sys.base.vo.TermTree;
import com.wldos.common.res.PageableResult;
import com.wldos.base.service.BaseService;
import com.wldos.common.utils.ChineseUtils;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;
import com.wldos.common.Constants;
import com.wldos.sys.base.entity.KModelIndustry;
import com.wldos.sys.base.entity.KTermObject;
import com.wldos.sys.base.entity.KTermType;
import com.wldos.sys.base.entity.KTerms;
import com.wldos.sys.base.repo.IndustryRepo;
import com.wldos.common.utils.NameConvert;
import com.wldos.common.utils.TreeUtils;
import com.wldos.common.vo.SelectOption;
import com.wldos.common.vo.TreeSelectOption;
import com.wldos.common.enums.RedisKeyEnum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分类操作service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TermService extends BaseService<TermRepo, KTerms, Long> {

	private final BeanCopier termCopier = BeanCopier.create(Term.class, KTerms.class, false);

	private final BeanCopier termTypeCopier = BeanCopier.create(Term.class, KTermType.class, false);

	@Value("${wldos.cms.defaultTermTypeId}")
	private Long defaultTermTypeId;

	private final TermTypeRepo termTypeRepo;

	private final IndustryRepo industryRepo;

	private final TermObjectRepo termObjectRepo;

	private final TermObjectService termObjectService;

	public TermService(TermTypeRepo termTypeRepo, IndustryRepo industryRepo, TermObjectRepo termObjectRepo, TermObjectService termObjectService) {
		this.termTypeRepo = termTypeRepo;
		this.industryRepo = industryRepo;
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
		StringBuilder sql = new StringBuilder(50).append("select a.*, o.id term_type_id, o.class_type, o.industry_id, o.description,")
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
	 * 查询某域下的所有分类
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
						new Category(res.getId(), res.getParentId(), res.getName(), res.getId().toString(), res.getSlug(), res.getIndustryType())).collect(Collectors.toList());

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
					return TreeSelectOption.of(termTypeId, term.getParentId(), term.getName(), termTypeId.toString(), termTypeId.toString());
				}).collect(Collectors.toList());

				String topTermId = String.valueOf(Constants.TOP_TERM_ID);
				// 新增根分类
				TreeSelectOption infoCate = TreeSelectOption.of(Constants.TOP_TERM_ID, Constants.TOP_VIR_ID, "根分类", topTermId, topTermId);

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
		List<Long> typeIds = nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
		if (ObjectUtils.isBlank(typeIds))
			return new ArrayList<>();
		List<Term> terms = this.entityRepo.queryAllTermsByTermTypeIds(typeIds);
		if (terms == null)
			return new ArrayList<>();

		return terms.parallelStream().map(res ->
				new Category(res.getId(), res.getParentId(), res.getName(), res.getId().toString(), res.getSlug(), res.getIndustryType())).collect(Collectors.toList());
	}

	/**
	 * 根据父分类别名查询子分类（含自身）
	 *
	 * @param slugTerm 父分类别名
	 * @return 分类列表
	 */
	public List<Category> queryCategoriesFromPlug(String slugTerm) {
		Term term = this.entityRepo.queryTermBySlugTerm(slugTerm);
		List<LevelNode> nodes = this.queryTermTreeByParentId(term.getTermTypeId());
		// 分类类型id用于从对象分类关联表[存档]中作筛选分类条件，filter: termTypeIds ? {termTypeId: termTypeIds} : {}
		List<Long> typeIds = nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
		if (ObjectUtils.isBlank(typeIds))
			return new ArrayList<>();

		List<Term> terms = this.entityRepo.queryAllTermsByTermTypeIds(typeIds);
		if (terms == null)
			return new ArrayList<>();

		return terms.parallelStream().map(res ->
				new Category(res.getId(), res.getParentId(), res.getName(), res.getId().toString(), res.getSlug(), res.getIndustryType())).collect(Collectors.toList());
	}

	/**
	 * 查询顶级分类
	 *
	 * @return 顶级分类下拉列表
	 */
	public List<SelectOption> queryTopCategories() {
		List<Term> terms = this.findAllCategory();
		return terms.parallelStream().filter(t -> t.getParentId().equals(Constants.TOP_TERM_ID)).map(term -> new SelectOption(term.getName(), term.getSlug())).collect(Collectors.toList());
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
				List<SelectOption> options = allTerms.parallelStream().map(term -> new SelectOption(term.getName(), term.getName())).collect(Collectors.toList());

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
	 * 查询某行业门类下的所有分类
	 *
	 * @param industryType 行业门类
	 * @return 分类项集合
	 */
	public List<Term> findCategoryByIndustryType(String industryType) {
		KModelIndustry modelIndustry = this.industryRepo.findByIndustryType(industryType);
		return this.entityRepo.findByContType(TermTypeEnum.CATEGORY.toString(), modelIndustry.getId());
	}

	/**
	 * 查询某行业门类下的所有标签
	 *
	 * @param industryType 行业门类编码
	 * @return 分类项集合
	 */
	public List<Term> findTagByIndustryType(String industryType) {
		KModelIndustry modelIndustry = this.industryRepo.findByIndustryType(industryType);
		return this.entityRepo.findByContType(TermTypeEnum.TAG.toString(), modelIndustry.getId());
	}

	/**
	 * 新增一个分类或标签
	 *
	 * @param term 分类项
	 * @param userId 操作人id
	 * @param userIp 操作人ip
	 */
	public String addTerm(Term term, Long userId, String userIp) {
		boolean exists = this.entityRepo.existsTermBySlug(term.getSlug());
		if (exists)
			return "别名已存在";
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
	public void updateTerm(Term term, Long userId, String userIp) {
		KTerms kTerms = new KTerms();
		this.termCopier.copy(term, kTerms, null);
		EntityAssists.beforeUpdated(kTerms, userId, userIp);
		this.update(kTerms);

		KTermType termType = new KTermType();
		this.termTypeCopier.copy(term, termType, null);
		termType.setId(term.getTermTypeId());
		this.updateOtherEntity(termType);
	}

	/**
	 * 批量新增分类或标签
	 *
	 * @param terms 分类项集合, 已设置id
	 */
	private void batchAddTerm(List<Term> terms) {
		List<KTerms> kTerms = new ArrayList<>();
		List<KTermType> kTermTypes = new ArrayList<>();
		for (Term term : terms) {
			KTerms kTerm = new KTerms();
			this.termCopier.copy(term, kTerm, null);
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
			List<Long> ids = termObjects.parallelStream().map(KTermObject::getId).collect(Collectors.toList());
			this.deleteByEntityAndIds(termObjects.get(0), false, ids.toArray());
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
	 * 根据分类类型id获取对应的行业门类
	 *
	 * @param termTypeId 分类类型id
	 * @return 内容模型
	 */
	public KModelIndustry queryIndustryTypeByTermType(Long termTypeId) {
		return this.termTypeRepo.queryIndustryTypeByTermType(termTypeId);
	}

	/**
	 * 根据行业门类编码获取对应的行业门类
	 *
	 * @param industryType 行业门类编码
	 * @return 内容模型
	 */
	public KModelIndustry queryModelIndustryByTypeCode(String industryType) {
		return this.industryRepo.findByIndustryType(industryType);
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
		List<KTermObject> tObjects = addIds.parallelStream().map(tId -> {
			KTermObject termObject = new KTermObject();
			termObject.setId(this.nextId());
			termObject.setTermTypeId(tId);
			termObject.setObjectId(pId);
			termObject.setTermOrder(0L);

			return termObject;
		}).collect(Collectors.toList());

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
		return this.entityRepo.queryTermBySlugTerm(slugTerm);
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
		String key = RedisKeyEnum.WLDOS_TERM.toString() + table + pId + ":child";
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
	 * 判断多个分类目录是否类型同源，用于新增作品分类
	 *
	 * @param termTypeIds 分类目录id
	 * @return 是否
	 */
	public boolean isSameIndustryType(List<Long> termTypeIds) {
		if (ObjectUtils.isBlank(termTypeIds) || termTypeIds.size() == 1)
			return true;
		List<KTermType> termTypes = (List<KTermType>) this.termTypeRepo.findAllById(termTypeIds);

		if (ObjectUtils.isBlank(termTypes) || termTypes.size() < termTypeIds.size()) // 非法数据，返回false,用于防止真假数据试探
			return false;
		Long cId0 = termTypes.get(0).getIndustryId();
		return termTypes.stream().allMatch(t -> t.getIndustryId().equals(cId0));
	}

	/**
	 * 判断多个分类目录是否同行业，用于更新作品分类
	 *
	 * @param termTypeIds 分类目录id
	 * @param industryId 待更新作品所归属的行业门类id
	 * @return 是否
	 */
	public boolean isSameIndustryType(List<Long> termTypeIds, Long industryId) {
		if (ObjectUtils.isBlank(termTypeIds))
			return true;
		List<KTermType> termTypes = (List<KTermType>) this.termTypeRepo.findAllById(termTypeIds);

		return termTypes.stream().allMatch(t -> t.getIndustryId().equals(industryId));
	}

	/**
	 * 批量处理前端自定义标签的保存，已存在的直接返回，不存在的创建新标签返回
	 *
	 * @param tagNames 用户设置的标签
	 * @param industryId 待设置标签的归属行业id
	 * @return 设置标签的id
	 */
	public List<Long> handleTag(List<String> tagNames, Long industryId) {
		if (ObjectUtils.isBlank(tagNames))
			return new ArrayList<>();
		// 批量查询标签项
		List<Term> terms = this.entityRepo.findAllByNameAndClassType(tagNames, TermTypeEnum.TAG.toString());
		List<Long> tagIds;
		if (terms != null) {
			// 查找不存在的标签
			List<String> noTagNames = tagNames.stream().filter(name -> terms.stream().noneMatch(t -> t.getName().equals(name))).collect(Collectors.toList());

			if (ObjectUtils.isBlank(noTagNames))
				return terms.stream().map(Term::getTermTypeId).collect(Collectors.toList());
			// 已经存在的标签，直接复用
			tagIds = terms.stream().map(Term::getTermTypeId).collect(Collectors.toList());


			List<Long> newTags = this.addNewTags(noTagNames, industryId);

			tagIds.addAll(newTags);

			return tagIds;
		}

		// 全部新增
		return this.addNewTags(tagNames, industryId);
	}

	@Value("${wldos.cms.tag.tagLength}")
	private int tagLength;

	private List<Long> addNewTags(List<String> tagNames, Long industryId) {
		List<Term> newTerms = tagNames.stream().map(n -> {
			// 标签超长，抛弃
			if (ObjectUtils.isOutBounds(n, this.tagLength))
				return null;
			String slug = ChineseUtils.hanZi2Pinyin(n, true);
			if (this.entityRepo.existsTermBySlug(slug))
				return null; // @todo 由于性能和缓存一致性冲突，通过查询判断别名重复的直接抛弃

			Term t = new Term();
			Long id = this.nextId();
			t.setId(id);
			t.setTermTypeId(id);
			t.setClassType(TermTypeEnum.TAG.toString());
			t.setParentId(Constants.TOP_TERM_ID);
			t.setIndustryId(industryId);
			t.setName(n);
			t.setSlug(slug);

			return t;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (newTerms.isEmpty())
			return new ArrayList<>();

		this.batchAddTerm(newTerms);

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
