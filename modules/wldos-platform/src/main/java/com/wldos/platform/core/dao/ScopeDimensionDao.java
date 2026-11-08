/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoScopeDimension;

public interface ScopeDimensionDao extends BaseDao<WoScopeDimension, Long> {

	@Query("SELECT * FROM wo_scope_dimension WHERE enabled = '1' AND delete_flag = 'normal' AND is_valid = '1' ORDER BY sort_order, id")
	List<WoScopeDimension> findAllEnabled();

	@Query("SELECT * FROM wo_scope_dimension WHERE enabled = '1' AND dimension_code NOT IN ('com_id','domain_id') "
			+ "AND delete_flag = 'normal' AND is_valid = '1' ORDER BY sort_order, id")
	List<WoScopeDimension> findAllFilterCatalog();

	@Query("SELECT * FROM wo_scope_dimension WHERE dimension_code = :dimensionCode AND delete_flag = 'normal' AND is_valid = '1' LIMIT 1")
	WoScopeDimension findByDimensionCode(@Param("dimensionCode") String dimensionCode);

	@Query("SELECT * FROM wo_scope_dimension WHERE delete_flag = 'normal' AND is_valid = '1' "
			+ "AND (value_source LIKE 'DICT:%' OR value_source LIKE 'TERM_CLASS:%') ORDER BY sort_order, id")
	List<WoScopeDimension> findAllDictDimensions();

	@Query("SELECT COALESCE(MAX(sort_order), 0) FROM wo_scope_dimension WHERE delete_flag = 'normal' AND is_valid = '1'")
	Integer findMaxSortOrder();
}
