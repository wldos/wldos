/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.book.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.wldos.base.service.BaseService;
import com.wldos.book.entity.Book;
import com.wldos.book.repo.BookRepo;
import com.wldos.book.vo.BookVO;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.PageQuery;
import com.wldos.sys.core.service.UserService;
import com.wldos.support.auth.vo.UserInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 年谱service。
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BookService extends BaseService<BookRepo, Book, Long> {
	private final UserService userService;

	public BookService(UserService userService) {
		this.userService = userService;
	}

	public PageableResult<BookVO> execQueryForPage(PageQuery pageQuery) {
		PageableResult<BookVO> bookVOPage = this.execQueryForPage(new BookVO(), new Book(), pageQuery);
		List<BookVO> bookVOList = bookVOPage.getData().getRows();

		AtomicInteger count = new AtomicInteger(0);

		bookVOList = bookVOList.parallelStream().map(item -> {

			String coverUrl = this.store.getFileUrl(item.getCover(), this.defaultCover(count.getAndIncrement()));
			UserInfo userInfo = this.userService.queryUserInfo(item.getWriter());
			item.setCover(coverUrl);
			item.setWriterInfo(userInfo);

			return item;
		}).collect(Collectors.toList());

		return bookVOPage.setDataRows(bookVOList);
	}


	private String defaultCover(int count) {

		String[] covers = {
				"https://gw.alipayobjects.com/zos/rmsportal/uMfMFlvUuceEyPpotzlq.png",
				"https://gw.alipayobjects.com/zos/rmsportal/iZBVOIhGJiAnhplqjvZW.png",
				"https://gw.alipayobjects.com/zos/rmsportal/iXjVmWVHbCJAyqvDxdtx.png",
				"https://gw.alipayobjects.com/zos/rmsportal/gLaIAoVWTtLbBWZNYEMg.png"
		};

		int coverNum = covers.length;

		return (count / coverNum) % 2 == 0 ? covers[count / coverNum] : covers[(coverNum - 1) - (count % coverNum)];
	}
}
