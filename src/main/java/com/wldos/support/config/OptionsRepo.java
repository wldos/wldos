/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.config;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 系统配置项仓库操作类。
 *
 * @Title OptionsRepo
 * @Package com.wldos.support.config
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/7/13
 * @Version 1.0
 */
public interface OptionsRepo extends PagingAndSortingRepository<WoOptions, Long> {

	List<WoOptions> findAllByAppTypeIn(List appType);
}
