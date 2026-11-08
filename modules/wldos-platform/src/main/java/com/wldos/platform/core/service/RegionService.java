/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.RegionDao;
import com.wldos.platform.core.enums.RegionLevelEnum;
import io.github.wldos.platform.support.region.RegionOpener;
import io.github.wldos.platform.support.region.vo.City;
import com.wldos.platform.core.entity.WoRegion;
import com.wldos.platform.core.vo.Prov;
import com.wldos.platform.core.vo.RegionSearchPathVO;
import com.wldos.platform.core.vo.RegionSearchRow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 行政区划管理service。
 *
 * @author Yuanxi Universe
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RegionService extends EntityService<RegionDao, WoRegion, Long> implements RegionOpener {

	private static final int SEARCH_DEFAULT_LIMIT = 30;

	private static final int SEARCH_MAX_LIMIT = 100;

	private final ConcurrentHashMap<String, List<RegionSearchPathVO>> searchCache = new ConcurrentHashMap<>();

	public List<Prov> queryProvince() {
		return this.entityRepo.queryByLevel(RegionLevelEnum.PROV.toString());
	}

	/** 按级别列出全部区划（1=省 2=市 3=区县），供表头多选筛选等 */
	@Transactional(readOnly = true)
	public List<Prov> listByLevel(String level) {
		if (!StringUtils.hasText(level)) {
			return Collections.emptyList();
		}
		return this.entityRepo.queryByLevel(level.trim());
	}

	public List<City> queryCityByProvId(Long provId) {

		return this.entityRepo.queryCity(provId, RegionLevelEnum.CITY.toString());
	}

	public List<Prov> queryCountyByCityId(Long cityId) {
		if (cityId == null) {
			return Collections.emptyList();
		}
		return this.entityRepo.queryCounty(cityId, RegionLevelEnum.COUNTY.toString());
	}

	public City queryRegionInfo(Long cityId) {
		return this.entityRepo.queryCityById(cityId, RegionLevelEnum.CITY.toString());
	}

	public City queryRegionInfoByCode(String cityCode) {
		return this.entityRepo.queryCityByCode(cityCode, RegionLevelEnum.CITY.toString());
	}

	@Transactional(readOnly = true)
	public List<RegionSearchPathVO> searchPaths(String keyword, Integer limit) {
		if (!StringUtils.hasText(keyword)) {
			return Collections.emptyList();
		}
		String key = keyword.trim();
		if (key.length() < 1) {
			return Collections.emptyList();
		}
		int cappedLimit = limit == null ? SEARCH_DEFAULT_LIMIT : Math.min(Math.max(limit, 1), SEARCH_MAX_LIMIT);
		String cacheKey = key + "|" + cappedLimit;
		List<RegionSearchPathVO> cached = this.searchCache.get(cacheKey);
		if (cached != null) {
			return cached;
		}
		List<RegionSearchRow> hits = this.entityRepo.searchByName(key, cappedLimit);
		if (hits == null || hits.isEmpty()) {
			List<RegionSearchPathVO> empty = Collections.emptyList();
			this.searchCache.put(cacheKey, empty);
			return empty;
		}
		Map<String, RegionSearchPathVO> dedup = new LinkedHashMap<>(hits.size());
		for (RegionSearchRow hit : hits) {
			if (hit == null || hit.getId() == null) {
				continue;
			}
			RegionSearchPathVO path = buildPath(hit);
			if (path == null || path.getIds().isEmpty()) {
				continue;
			}
			dedup.putIfAbsent(pathKey(path), path);
		}
		List<RegionSearchPathVO> result = new ArrayList<>(dedup.values());
		this.searchCache.put(cacheKey, result);
		return result;
	}

	private RegionSearchPathVO buildPath(RegionSearchRow hit) {
		List<Long> ids = new ArrayList<>(3);
		List<String> labels = new ArrayList<>(3);
		WoRegion current = toRegion(hit);
		while (current != null && current.getId() != null) {
			ids.add(0, current.getId());
			labels.add(0, current.getName());
			if (current.getParentId() == null || current.getParentId() == 0L) {
				break;
			}
			if (RegionLevelEnum.PROV.toString().equals(current.getLevel())) {
				break;
			}
			Optional<WoRegion> parent = this.entityRepo.findById(current.getParentId());
			if (!parent.isPresent()) {
				break;
			}
			current = parent.get();
		}
		return RegionSearchPathVO.of(ids, labels);
	}

	private static WoRegion toRegion(RegionSearchRow row) {
		WoRegion r = new WoRegion();
		r.setId(row.getId());
		r.setName(row.getName());
		r.setLevel(row.getLevel());
		r.setParentId(row.getParentId());
		return r;
	}

	private static String pathKey(RegionSearchPathVO path) {
		StringBuilder sb = new StringBuilder();
		for (Long id : path.getIds()) {
			sb.append(id).append('-');
		}
		return sb.toString();
	}
}
