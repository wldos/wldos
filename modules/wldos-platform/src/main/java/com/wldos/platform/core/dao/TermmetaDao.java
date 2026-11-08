/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.KTermmeta;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 分类项扩展元数据。
 */
public interface TermmetaDao extends BaseDao<KTermmeta, Long> {

	@Modifying
	@Query("delete from k_termmeta where term_id = :termId")
	void deleteByTermId(@Param("termId") Long termId);
}
