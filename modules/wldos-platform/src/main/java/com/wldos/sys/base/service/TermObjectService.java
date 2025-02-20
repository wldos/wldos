/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.base.service;

import com.wldos.framework.service.RepoService;
import com.wldos.sys.base.entity.KTermObject;
import com.wldos.sys.base.repo.TermObjectRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务对象类型关联service。
 *
 * @author 元悉宇宙
 * @date 2021/12/13
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TermObjectService extends RepoService<TermObjectRepo, KTermObject, Long> {
}
