/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.service.impl;

import com.wldos.framework.mvc.service.NonEntityService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 不确定具体仓库操作也没有专门要实现的service时使用此类作为service注入无仓controller，拥有基础会话和通用操作能力。
 *
 * @author 元悉宇宙
 * @date 2021/5/5
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class NullService extends NonEntityService {
}