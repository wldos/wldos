/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.List;

import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.RegionDao;
import com.wldos.platform.core.enums.RegionLevelEnum;
import com.wldos.platform.support.region.RegionOpener;
import com.wldos.platform.support.region.vo.City;
import com.wldos.platform.core.entity.WoRegion;
import com.wldos.platform.core.vo.Prov;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 行政区划管理service。
 *
 * @author 元悉宇宙
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RegionService extends EntityService<RegionDao, WoRegion, Long> implements RegionOpener {

	public List<Prov> queryProvince() {
		return this.entityRepo.queryByLevel(RegionLevelEnum.PROV.toString());
	}

	public List<City> queryCityByProvId(Long provId) {

		return this.entityRepo.queryCity(provId, RegionLevelEnum.CITY.toString());
	}

	public City queryRegionInfo(Long cityId) {
		return this.entityRepo.queryCityById(cityId, RegionLevelEnum.CITY.toString());
	}

	public City queryRegionInfoByCode(String cityCode) {
		return this.entityRepo.queryCityByCode(cityCode, RegionLevelEnum.CITY.toString());
	}
}
