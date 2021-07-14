/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import java.util.List;

import com.wldos.system.entity.WoApp;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 平台应用仓库操作类。
 *
 * @Title WoAppRepo
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public interface AppRepo extends PagingAndSortingRepository<WoApp, Long> {

	List<WoApp> findAllByDeleteFlagEqualsAndIsValidEquals(String deleteFlag, String isValid);
}
