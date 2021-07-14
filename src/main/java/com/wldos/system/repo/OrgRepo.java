/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import com.wldos.system.entity.WoOrg;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 组织结构仓库操作类。
 *
 * @Title OrgRepo
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public interface OrgRepo extends PagingAndSortingRepository<WoOrg, Long> {
}
