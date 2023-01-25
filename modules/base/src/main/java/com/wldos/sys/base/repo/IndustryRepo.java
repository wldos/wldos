/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import com.wldos.sys.base.entity.KModelIndustry;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 行业门类repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface IndustryRepo extends PagingAndSortingRepository<KModelIndustry, Long> {

	@Query("select a.* from k_model_industry a where a.industry_code=:industryType")
	KModelIndustry findByIndustryType(@Param("industryType") String industryType);
}
