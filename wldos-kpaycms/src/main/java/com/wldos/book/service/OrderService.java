/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.book.service;

import com.wldos.book.entity.Order;
import com.wldos.book.repo.OrderRepo;
import com.wldos.support.service.BaseService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单操作service。
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderService extends BaseService<OrderRepo, Order, Long> {
}
