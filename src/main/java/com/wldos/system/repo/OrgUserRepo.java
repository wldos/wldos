/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import com.wldos.system.entity.WoOrgUser;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 用户归属组仓库操作类。
 *
 * @Title OrgUserRepo
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/9
 * @Version 1.0
 */
public interface OrgUserRepo extends PagingAndSortingRepository<WoOrgUser, Long> {
}
