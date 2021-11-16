/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.book.controller;

import com.wldos.book.entity.Order;
import com.wldos.book.service.OrderService;
import com.wldos.support.controller.RepoController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 下订单购买。
 *
 * @author 树悉猿
 * @date 2021/6/12
 * @version 1.0
 */
@RequestMapping("order")
@RestController
public class OrderController extends RepoController<OrderService, Order> {

	@PostMapping("pay")
	public String purchase() {

		return this.resJson.ok("");
	}
}
