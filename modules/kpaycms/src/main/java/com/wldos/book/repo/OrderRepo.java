/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.book.repo;

import com.wldos.book.entity.Order;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 订单repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface OrderRepo extends PagingAndSortingRepository<Order, Long> {
}
