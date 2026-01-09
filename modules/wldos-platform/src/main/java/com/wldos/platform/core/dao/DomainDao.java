/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;


import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoDomain;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 租户域管理仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface DomainDao extends BaseDao<WoDomain, Long> {

	List<WoDomain> findAllByDeleteFlagAndIsValid(@Param("deleteFlag") String deleteFlag, @Param("isValid") String isValid);

	/**
	 * 根据域名查询域信息
	 *
	 * @param siteDomain 域名
	 * @param isValid 是否有效
	 * @param deleteFlag 是否删除
	 * @return 域实例
	 */
	WoDomain findBySiteDomainAndIsValidAndDeleteFlag(@Param("siteDomain") String siteDomain, @Param("isValid") String isValid, @Param("deleteFlag") String deleteFlag);
}
