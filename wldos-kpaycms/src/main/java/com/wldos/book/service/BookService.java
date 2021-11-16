/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.book.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.wldos.book.entity.Book;
import com.wldos.book.repo.BookRepo;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.PageQuery;
import com.wldos.system.storage.IStore;
import com.wldos.book.vo.BookVO;
import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.service.UserService;

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

	private final IStore store;

	public BookService(UserService userService, IStore store) {
		this.userService = userService;
		this.store = store;
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
