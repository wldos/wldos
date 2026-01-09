/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.TermObjectDao;

import com.wldos.platform.core.entity.KTermObject;
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
public class TermObjectService extends EntityService<TermObjectDao, KTermObject, Long> {
}
