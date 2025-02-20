/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.repo;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 基础仓接口。
 *
 * @author 元悉宇宙
 * @date 2023/10/21
 * @version 1.0
 */
@NoRepositoryBean
public interface BaseRepo<E, PK> extends PagingAndSortingRepository<E, PK> {
}
