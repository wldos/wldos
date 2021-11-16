/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.List;

import com.wldos.system.core.entity.WoApp;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 平台应用仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface AppRepo extends PagingAndSortingRepository<WoApp, Long> {

	List<WoApp> findAllByDeleteFlagEqualsAndIsValidEquals(String deleteFlag, String isValid);
}
