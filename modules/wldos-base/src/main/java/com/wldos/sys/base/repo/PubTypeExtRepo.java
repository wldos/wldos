/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.support.cms.dto.PubTypeExt;
import com.wldos.sys.base.entity.KModelPubTypeExt;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 发布类型自定义扩展属性（不含系统模型公共属性）repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface PubTypeExtRepo extends PagingAndSortingRepository<KModelPubTypeExt, Long> {
	@Query("select e.* from k_model_pub_type_ext e where e.pub_type=:pubType")
	List<PubTypeExt> queryExtPropsByPubType(@Param("pubType") String pubType);
}
