/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import com.wldos.sys.base.entity.WoSubject;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 社会主体仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface SubjectRepo extends PagingAndSortingRepository<WoSubject, Long> {
}
