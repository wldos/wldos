package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.TermDao;
import com.wldos.platform.core.dict.DictRegistry;
import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.BoolEnum;
import io.github.wldos.common.utils.TreeUtils;
import io.github.wldos.common.vo.SelectOption;
import io.github.wldos.common.vo.TreeSelectOption;
import io.github.wldos.framework.support.plugins.core.PluginStatus;
import io.github.wldos.platform.support.term.dto.Term;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 统一字典消费：{@code GET /dict/select/{code}}。
 * 
 * @author Yuanxi Universe (306991142@qq.com)
 * @since 3.0.1
 * @version 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class DictService extends NonEntityService {

	@Autowired
	@Lazy
	private TermDao termDao;

	static {
		DictRegistry.registerEnum("pluginStatus", "插件状态", () -> Arrays.stream(PluginStatus.values())
				.map(e -> SelectOption.of(e.getDescription(), e.name()))
				.collect(Collectors.toList()));
		DictRegistry.registerEnum("bool", "是/否", () -> Arrays.asList(
				SelectOption.of("是", BoolEnum.YES.toString()),
				SelectOption.of("否", BoolEnum.NO.toString())));
		DictRegistry.registerEnum("dataScope", "数据策略", () -> Arrays.stream(io.github.wldos.platform.support.rbac.DataScope.values())
				.map(e -> SelectOption.of(e.getLabel(), e.getValue()))
				.collect(Collectors.toList()));
	}

	public List<SelectOption> select(String code) {
		if (code == null || code.trim().isEmpty()) {
			return Collections.emptyList();
		}
		String dictCode = code.trim();
		String boundClassType = DictRegistry.termClassType(dictCode);
		if (boundClassType != null) {
			List<SelectOption> fromTerms = selectFromTerms(boundClassType);
			if (!fromTerms.isEmpty()) {
				return fromTerms;
			}
			Supplier<List<SelectOption>> enumSource = DictRegistry.enumSource(dictCode);
			if (enumSource != null) {
				List<SelectOption> list = enumSource.get();
				return list != null ? list : Collections.emptyList();
			}
			return Collections.emptyList();
		}
		Supplier<List<SelectOption>> enumSource = DictRegistry.enumSource(dictCode);
		if (enumSource != null) {
			List<SelectOption> list = enumSource.get();
			return list != null ? list : Collections.emptyList();
		}
		return selectFromTerms(resolveTermClassType(dictCode));
	}

	public boolean isTreeDict(String code) {
		if (code == null || code.trim().isEmpty()) {
			return false;
		}
		String dictCode = code.trim();
		if (DictRegistry.enumSource(dictCode) != null) {
			return false;
		}
		if (DictRegistry.termClassType(dictCode) != null) {
			return false;
		}
		return true;
	}

	public List<TreeSelectOption> selectTree(String code) {
		if (!isTreeDict(code)) {
			return Collections.emptyList();
		}
		String classType = resolveTermClassType(code.trim());
		List<Term> terms = this.termDao.findAllByClassType(classType);
		if (terms == null || terms.isEmpty()) {
			return Collections.emptyList();
		}
		List<TreeSelectOption> nodes = new ArrayList<>();
		for (Term term : terms) {
			Long termId = term.getId();
			String value = term.getSlug() != null ? term.getSlug() : String.valueOf(termId);
			nodes.add(TreeSelectOption.of(
					termId,
					term.getParentId(),
					term.getName(),
					value,
					String.valueOf(termId),
					term.getDisplayOrder()));
		}
		return TreeUtils.build(nodes, Constants.TOP_VIR_ID);
	}

	private String resolveTermClassType(String dictCode) {
		String classType = DictRegistry.termClassType(dictCode);
		return classType != null ? classType : dictCode;
	}

	private List<SelectOption> selectFromTerms(String classType) {
		List<Term> terms = this.termDao.findAllByClassType(classType);
		if (terms == null || terms.isEmpty()) {
			return Collections.emptyList();
		}
		return terms.stream()
				.map(t -> SelectOption.of(t.getName(), t.getSlug() != null ? t.getSlug() : String.valueOf(t.getId())))
				.collect(Collectors.toList());
	}
}
