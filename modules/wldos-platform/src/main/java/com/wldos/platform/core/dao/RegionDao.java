/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoRegion;
import io.github.wldos.platform.support.region.vo.City;
import com.wldos.platform.core.vo.Prov;
import com.wldos.platform.core.vo.RegionSearchRow;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 行政区划仓库操作类。
 *
 * @author Yuanxi Universe
 * @date 2021/4/27
 * @version 1.0
 */
public interface RegionDao extends BaseDao<WoRegion, Long> {
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

	@Query("select r.id, r.name from wo_region r where r.is_valid='1' and r.delete_flag='normal' "
			+ "and r.`level`=:countyLevel and r.parent_id=:cityId order by r.display_order, r.id")
	List<Prov> queryCounty(Long cityId, String countyLevel);

	@Query("select r.id, r.name, r.`level`, r.parent_id from wo_region r "
			+ "where r.is_valid='1' and r.delete_flag='normal' "
			+ "and r.`level` in ('1','2','3') and r.name like CONCAT('%',:keyword,'%') "
			+ "order by r.`level`, r.display_order, r.id limit :limit")
	List<RegionSearchRow> searchByName(@Param("keyword") String keyword, @Param("limit") int limit);
}
