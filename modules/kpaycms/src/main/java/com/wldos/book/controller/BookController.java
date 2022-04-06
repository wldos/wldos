/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.book.controller;

import java.util.Map;

import com.wldos.base.controller.RepoController;
import com.wldos.book.entity.Book;
import com.wldos.book.service.BookService;
import com.wldos.book.vo.BookVO;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.PageQuery;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 年谱模块controller。
 *
 * @author 树悉猿
 * @date 2021-02-12
 * @version V1.0
 */
@RequestMapping("book")
@RestController
public class BookController extends RepoController<BookService, Book> {

	public String listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		PageableResult<BookVO> res = this.service.execQueryForPage(pageQuery);
		return resJson.ok(res);
	}
}
