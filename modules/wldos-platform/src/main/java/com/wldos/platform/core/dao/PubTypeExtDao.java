/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.KModelPubTypeExt;
import com.wldos.platform.support.cms.dto.PubTypeExt;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 发布类型自定义扩展属性（不含系统模型公共属性）repository操作类
 *
 * @author 元悉宇宙
 * @date 2021/4/17
 * @version 1.0
 */
public interface PubTypeExtDao extends BaseDao<KModelPubTypeExt, Long> {
	@Query("select e.* from k_model_pub_type_ext e where e.pub_type=:pubType")
	List<PubTypeExt> queryExtPropsByPubType(@Param("pubType") String pubType);
}
