/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import com.wldos.system.entity.WoDomain;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 平台域仓库操作类。
 *
 * @Title WoDomainRepo
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public interface WoDomainRepo extends PagingAndSortingRepository<WoDomain, Long> {
}
