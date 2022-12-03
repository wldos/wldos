/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.sys.base.entity.WoOptions;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 系统配置项仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/7/13
 * @version 1.0
 */
public interface OptionsRepo extends PagingAndSortingRepository<WoOptions, Long> {

	List<WoOptions> findAllByAppTypeIn(@Param("appType") List<String> appType);

	List<WoOptions> findAllByKey(@Param("key") String key);
}
