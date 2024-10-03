/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.framework.repo.BaseRepo;
import com.wldos.sys.base.entity.WoDomainApp;

import org.springframework.data.repository.query.Param;

/**
 * 租户领域应用管理仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface AppComRepo extends BaseRepo<WoDomainApp, Long> {

	List<WoDomainApp> findAllByDeleteFlagEqualsAndIsValidEquals(@Param("deleteFlag") String deleteFlag, @Param("isValid") String isValid);
}
