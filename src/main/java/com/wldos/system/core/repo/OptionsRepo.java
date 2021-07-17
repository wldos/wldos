/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.List;

import com.wldos.system.core.entity.WoOptions;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 系统配置项仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/7/13
 * @version 1.0
 */
public interface OptionsRepo extends PagingAndSortingRepository<WoOptions, Long> {

	List<WoOptions> findAllByAppTypeIn(List<String> appType);
}
