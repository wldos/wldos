/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.vo.RegionCascadeNode;

/**
 * 行政区划级联与搜索（角色范围维 UI）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class RegionCascadeService extends NonEntityService {

	private static final int SEARCH_LIMIT = 50;

	/** 国标地级占位名（wo_region.level=2），见 init.sql / GB2260 数据 */
	private static final String SHIXIAQU_NAME = "市辖区";

	private final JdbcOperations jdbcOperations;

	private final ConcurrentHashMap<Long, Boolean> shixiaquLayoutCache = new ConcurrentHashMap<>();

	public RegionCascadeService(JdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}

	public List<RegionCascadeNode> listChildren(Long parentId, int selectLevel) {
		long pid = parentId == null ? 0L : parentId;
		int targetLevel = normalizeSelectLevel(selectLevel);
		if (pid == 0 && targetLevel == 2) {
			return listCityLevelRoots();
		}
		if (targetLevel == 3 && hasShixiaquLayout(pid)) {
			return listCountiesUnderProvince(pid, targetLevel);
		}
		List<Map<String, Object>> rows = queryDirectChildren(pid);
		if (rows.isEmpty()) {
			return Collections.emptyList();
		}
		List<RegionCascadeNode> nodes = new ArrayList<>(rows.size());
		for (Map<String, Object> row : rows) {
			nodes.add(toDisplayNode(row, targetLevel, null));
		}
		if (targetLevel == 2) {
			relabelShixiaquWithProvinceName(pid, nodes);
		}
		return nodes;
	}

	public List<List<RegionCascadeNode>> resolvePaths(List<String> regionCodes, int selectLevel) {
		if (regionCodes == null || regionCodes.isEmpty()) {
			return Collections.emptyList();
		}
		int targetLevel = normalizeSelectLevel(selectLevel);
		List<String> codes = regionCodes.stream()
				.filter(StringUtils::hasText)
				.map(String::trim)
				.distinct()
				.collect(Collectors.toList());
		if (codes.isEmpty()) {
			return Collections.emptyList();
		}
		String placeholders = codes.stream().map(c -> "?").collect(Collectors.joining(","));
		String sql = "select id, region_code, name, level, parent_id from wo_region "
				+ "where delete_flag='normal' and is_valid='1' and region_code in (" + placeholders + ")";
		List<Map<String, Object>> matched = this.jdbcOperations.queryForList(sql, codes.toArray());
		if (matched == null || matched.isEmpty()) {
			return Collections.emptyList();
		}
		matched = matched.stream()
				.filter(row -> parseLevel(row.get("level")) == targetLevel)
				.collect(Collectors.toList());
		if (matched.isEmpty()) {
			return Collections.emptyList();
		}
		Map<Long, Map<String, Object>> byId = loadAncestorChain(matched);
		List<List<RegionCascadeNode>> paths = new ArrayList<>(matched.size());
		for (Map<String, Object> row : matched) {
			List<RegionCascadeNode> path = buildPath(row, byId, targetLevel);
			path = collapsePathForDisplay(path, targetLevel, byId);
			if (!path.isEmpty()) {
				paths.add(path);
			}
		}
		return paths;
	}

	public List<List<RegionCascadeNode>> search(String keyword, int selectLevel) {
		if (!StringUtils.hasText(keyword)) {
			return Collections.emptyList();
		}
		int targetLevel = normalizeSelectLevel(selectLevel);
		String like = "%" + keyword.trim() + "%";
		String sql = "select r.id, r.region_code, r.name, r.level, r.parent_id from wo_region r "
				+ "left join wo_region p on r.parent_id = p.id "
				+ "where r.delete_flag='normal' and r.is_valid='1' and r.level = ? "
				+ "and (r.name like ? or (r.name = ? and p.name like ?)) "
				+ "order by r.display_order, r.region_code limit ?";
		List<Map<String, Object>> matched = this.jdbcOperations.queryForList(sql, String.valueOf(targetLevel), like,
				SHIXIAQU_NAME, like, SEARCH_LIMIT);
		if (matched == null || matched.isEmpty()) {
			return Collections.emptyList();
		}
		Map<Long, Map<String, Object>> byId = loadAncestorChain(matched);
		List<List<RegionCascadeNode>> results = new ArrayList<>(matched.size());
		for (Map<String, Object> row : matched) {
			List<RegionCascadeNode> path = buildPath(row, byId, targetLevel);
			path = collapsePathForDisplay(path, targetLevel, byId);
			if (!path.isEmpty()) {
				results.add(path);
			}
		}
		return results;
	}

	private List<RegionCascadeNode> listCityLevelRoots() {
		List<Map<String, Object>> provinces = queryDirectChildren(0L);
		if (provinces.isEmpty()) {
			return Collections.emptyList();
		}
		List<RegionCascadeNode> nodes = new ArrayList<>(provinces.size());
		for (Map<String, Object> row : provinces) {
			Long provId = toLong(row.get("id"));
			if (provId != null && isSingleShixiaquProvince(provId)) {
				Map<String, Object> shixiaqu = queryShixiaquChild(provId);
				if (shixiaqu != null) {
					nodes.add(toDisplayNode(shixiaqu, 2, null));
					continue;
				}
			}
			nodes.add(toDisplayNode(row, 2, null));
		}
		return nodes;
	}

	private boolean isSingleShixiaquProvince(long provId) {
		if (!hasShixiaquLayout(provId)) {
			return false;
		}
		List<Map<String, Object>> cities = queryDirectChildren(provId);
		return cities.size() == 1 && isShixiaquName(String.valueOf(cities.get(0).get("name")));
	}

	private Map<String, Object> queryShixiaquChild(long provId) {
		List<Map<String, Object>> rows = this.jdbcOperations.queryForList(
				"select id, region_code, name, level, parent_id from wo_region "
						+ "where parent_id = ? and level = '2' and name = ? "
						+ "and delete_flag='normal' and is_valid='1' "
						+ "order by display_order, region_code limit 1",
				provId, SHIXIAQU_NAME);
		if (rows == null || rows.isEmpty()) {
			return null;
		}
		return rows.get(0);
	}

	private List<RegionCascadeNode> collapsePathForDisplay(List<RegionCascadeNode> path, int targetLevel,
			Map<Long, Map<String, Object>> byId) {
		if (path == null || path.isEmpty()) {
			return path;
		}
		if (targetLevel == 2 && path.size() == 2) {
			RegionCascadeNode city = path.get(1);
			Long provId = resolveParentProvinceId(city, byId);
			if (provId != null && isSingleShixiaquProvince(provId)) {
				return Collections.singletonList(city);
			}
		}
		return path;
	}

	private Long resolveParentProvinceId(RegionCascadeNode cityNode, Map<Long, Map<String, Object>> byId) {
		if (cityNode == null || cityNode.getId() == null) {
			return null;
		}
		if (byId != null) {
			for (Map<String, Object> row : byId.values()) {
				if (cityNode.getId().equals(toLong(row.get("id")))) {
					return toLong(row.get("parent_id"));
				}
			}
		}
		List<Map<String, Object>> rows = this.jdbcOperations.queryForList(
				"select parent_id from wo_region where id = ? and delete_flag='normal' and is_valid='1' limit 1",
				cityNode.getId());
		if (rows == null || rows.isEmpty()) {
			return null;
		}
		return toLong(rows.get(0).get("parent_id"));
	}

	private Map<Long, Map<String, Object>> loadAncestorChain(List<Map<String, Object>> matched) {
		Map<Long, Map<String, Object>> byId = new LinkedHashMap<>();
		for (Map<String, Object> row : matched) {
			putRow(byId, row);
		}
		List<Long> pendingParents = new ArrayList<>();
		for (Map<String, Object> row : matched) {
			collectMissingParents(row, byId, pendingParents);
		}
		while (!pendingParents.isEmpty()) {
			List<Long> batch = new ArrayList<>(pendingParents);
			pendingParents.clear();
			String placeholders = batch.stream().map(id -> "?").collect(Collectors.joining(","));
			String sql = "select id, region_code, name, level, parent_id from wo_region "
					+ "where delete_flag='normal' and is_valid='1' and id in (" + placeholders + ")";
			List<Map<String, Object>> parents = this.jdbcOperations.queryForList(sql, batch.toArray());
			if (parents == null || parents.isEmpty()) {
				break;
			}
			for (Map<String, Object> parent : parents) {
				putRow(byId, parent);
				collectMissingParents(parent, byId, pendingParents);
			}
		}
		return byId;
	}

	private void collectMissingParents(Map<String, Object> row, Map<Long, Map<String, Object>> byId,
			List<Long> pendingParents) {
		Long parentId = toLong(row.get("parent_id"));
		if (parentId == null || parentId <= 0L || byId.containsKey(parentId)) {
			return;
		}
		if (!pendingParents.contains(parentId)) {
			pendingParents.add(parentId);
		}
	}

	private List<RegionCascadeNode> buildPath(Map<String, Object> leafRow, Map<Long, Map<String, Object>> byId,
			int targetLevel) {
		List<Map<String, Object>> chain = new ArrayList<>();
		Map<String, Object> cursor = leafRow;
		while (cursor != null) {
			chain.add(0, cursor);
			Long parentId = toLong(cursor.get("parent_id"));
			if (parentId == null || parentId <= 0L) {
				break;
			}
			cursor = byId.get(parentId);
		}
		List<RegionCascadeNode> path = new ArrayList<>(chain.size());
		for (Map<String, Object> node : chain) {
			int level = parseLevel(node.get("level"));
			if (level > targetLevel) {
				continue;
			}
			if (targetLevel == 3 && level == 2 && isShixiaquName(String.valueOf(node.get("name")))) {
				continue;
			}
			path.add(toDisplayNode(node, targetLevel, byId));
		}
		return path;
	}

	private List<RegionCascadeNode> listCountiesUnderProvince(Long provId, int targetLevel) {
		String sql = "select c.id, c.region_code, c.name, c.level, c.parent_id from wo_region c "
				+ "inner join wo_region city on c.parent_id = city.id "
				+ "where city.parent_id = ? and city.level = '2' and c.level = '3' "
				+ "and c.delete_flag='normal' and c.is_valid='1' and city.delete_flag='normal' "
				+ "and city.is_valid='1' order by c.display_order, c.region_code limit 500";
		List<Map<String, Object>> rows = this.jdbcOperations.queryForList(sql, provId);
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}
		List<RegionCascadeNode> nodes = new ArrayList<>(rows.size());
		for (Map<String, Object> row : rows) {
			nodes.add(toDisplayNode(row, targetLevel, null));
		}
		return nodes;
	}

	private List<Map<String, Object>> queryDirectChildren(long parentId) {
		String sql = "select id, region_code, name, level, parent_id from wo_region "
				+ "where delete_flag='normal' and is_valid='1' and parent_id = ? "
				+ "order by display_order, region_code limit 500";
		List<Map<String, Object>> rows = this.jdbcOperations.queryForList(sql, parentId);
		return rows == null ? Collections.emptyList() : rows;
	}

	private RegionCascadeNode toDisplayNode(Map<String, Object> row, int targetLevel,
			Map<Long, Map<String, Object>> byId) {
		RegionCascadeNode node = toNode(row, targetLevel);
		if (targetLevel == 2 && parseLevel(row.get("level")) == 2 && isShixiaquName(String.valueOf(row.get("name")))) {
			node.setLabel(resolveProvinceName(toLong(row.get("parent_id")), byId));
		}
		return node;
	}

	private void relabelShixiaquWithProvinceName(Long parentId, List<RegionCascadeNode> nodes) {
		if (!hasShixiaquLayout(parentId)) {
			return;
		}
		String provName = resolveProvinceName(parentId, null);
		for (RegionCascadeNode node : nodes) {
			if (isShixiaquName(node.getLabel())) {
				node.setLabel(provName);
			}
		}
	}

	private boolean hasShixiaquLayout(long parentId) {
		if (parentId <= 0L) {
			return false;
		}
		return this.shixiaquLayoutCache.computeIfAbsent(parentId, id -> {
			Integer cnt = this.jdbcOperations.queryForObject(
					"select count(1) from wo_region where parent_id = ? and level = '2' and name = ? "
							+ "and delete_flag='normal' and is_valid='1' limit 1",
					Integer.class, id, SHIXIAQU_NAME);
			return cnt != null && cnt > 0;
		});
	}

	private String resolveProvinceName(Long provId, Map<Long, Map<String, Object>> byId) {
		if (provId == null) {
			return SHIXIAQU_NAME;
		}
		if (byId != null) {
			Map<String, Object> prov = byId.get(provId);
			if (prov != null && prov.get("name") != null) {
				return String.valueOf(prov.get("name"));
			}
		}
		List<Map<String, Object>> rows = this.jdbcOperations.queryForList(
				"select name from wo_region where id = ? and delete_flag='normal' and is_valid='1' limit 1", provId);
		if (rows != null && !rows.isEmpty() && rows.get(0).get("name") != null) {
			return String.valueOf(rows.get(0).get("name"));
		}
		return SHIXIAQU_NAME;
	}

	private boolean isShixiaquName(String name) {
		return SHIXIAQU_NAME.equals(name);
	}

	private RegionCascadeNode toNode(Map<String, Object> row, int targetLevel) {
		Long id = toLong(row.get("id"));
		String code = String.valueOf(row.get("region_code"));
		String name = String.valueOf(row.get("name"));
		String level = String.valueOf(row.get("level"));
		int nodeLevel = parseLevel(level);
		boolean leaf = nodeLevel >= targetLevel;
		return new RegionCascadeNode(id, code, name, level, leaf);
	}

	private void putRow(Map<Long, Map<String, Object>> byId, Map<String, Object> row) {
		Long id = toLong(row.get("id"));
		if (id != null) {
			byId.put(id, row);
		}
	}

	private int normalizeSelectLevel(int selectLevel) {
		if (selectLevel < 1) {
			return 1;
		}
		if (selectLevel > 3) {
			return 3;
		}
		return selectLevel;
	}

	private int parseLevel(Object level) {
		if (level == null) {
			return 0;
		}
		try {
			return Integer.parseInt(String.valueOf(level));
		}
		catch (NumberFormatException ex) {
			return 0;
		}
	}

	private Long toLong(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		try {
			return Long.parseLong(String.valueOf(value));
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}
}
