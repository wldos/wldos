/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import java.util.List;

import com.wldos.system.entity.WoRegion;
import com.wldos.system.vo.Prov;
import com.wldos.system.vo.City;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 行政区划仓库操作类。
 *
 * @Title RegionRepo
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
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
			+ "and r.`level`=:cityLevel and r.id=:cityId order by r.display_order")
	City queryCityById(Long cityId, String cityLevel);
}
