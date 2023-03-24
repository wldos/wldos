/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 不确定具体仓库操作的service继承此类，拥有基础会话和通用操作能力。
 *
 * @author 树悉猿
 * @date 2021/5/5
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultNoRepoService extends NoRepoService {
}