/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.repo;

import java.util.List;

import com.wldos.sys.core.entity.WoRegion;
import com.wldos.sys.core.vo.Prov;
import com.wldos.sys.core.vo.City;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 行政区划仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface RegionRepo extends PagingAndSortingRepository<WoRegion, Long> {
	@Query("select g.id, g.name from wo_region g where g.`level`=:regionLevel")
	List<Prov> queryByLevel(String regionLevel);

	@Query("select r.id, r.name, r.parent_id, g.`name` prov_name from wo_region r JOIN wo_region g on r.parent_id=g.id "
			+ "where r.is_valid='1' and r.delete_flag='normal' and g.is_valid='1' and g.delete_flag='normal' "
			+ "and r.`level`=:cityLevel and r.parent_id=:provId order by r.display_order")
	List<City> queryCity(Long provId, String cityLevel);

	@Query("select r.id, r.name, r.parent_id, g.`name` prov_name from wo_region r JOIN wo_region g on r.parent_id=g.id "
			+ "where r.is_valid='1' and r.delete_flag='normal' and g.is_valid='1' and g.delete_flag='normal' "
			+ "and r.`level`=:cityLevel and r.id=:cityId ")
	City queryCityById(Long cityId, String cityLevel);

	/**
	 * 注意：约定code字面值必须等于id
	 *
	 * @param cityCode 编码
	 * @param cityLevel 市级级别
	 * @return 市信息
	 */
	@Query("select r.id, r.name, r.parent_id, g.`name` prov_name from wo_region r JOIN wo_region g on r.parent_id=g.id "
			+ "where r.is_valid='1' and r.delete_flag='normal' and g.is_valid='1' and g.delete_flag='normal' "
			+ "and r.`level`=:cityLevel and r.region_code=:cityCode ")
	City queryCityByCode(String cityCode, String cityLevel);
}
