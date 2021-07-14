/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.service;

import java.util.List;

import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.PageQuery;
import com.wldos.system.entity.WoRegion;
import com.wldos.system.repo.RegionRepo;
import com.wldos.system.sysenum.RegionLevelEnum;
import com.wldos.system.vo.City;
import com.wldos.system.vo.Prov;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 行政区划管理service。
 *
 * @Title RegionService
 * @Package com.wldos.system.service
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/28
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RegionService extends BaseService<RegionRepo, WoRegion, Long> {

	public PageableResult<WoRegion> execQueryForPage(Class<? extends Class> aClass, PageQuery pageQuery, Object[] toArray) {
		return this.execQueryForPage(aClass, pageQuery, toArray);
	}

	public List<Prov> queryProvince() {
		return this.entityRepo.queryByLevel(RegionLevelEnum.prov.toString());
	}

	public List<City> queryCityByProvId(Long provId) {

		return this.entityRepo.queryCity(provId, RegionLevelEnum.city.toString());
	}

	public City queryRegionInfo(Long cityId) {
		return this.entityRepo.queryCityById(cityId, RegionLevelEnum.city.toString());
	}
}
