/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.service;

import com.wldos.support.service.BaseService;
import com.wldos.system.entity.WoCompany;
import com.wldos.system.repo.CompanyRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司管理service。
 *
 * @Title CompanyService
 * @Package com.wldos.system.service
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/28
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CompanyService extends BaseService<CompanyRepo, WoCompany, Long> {
}
