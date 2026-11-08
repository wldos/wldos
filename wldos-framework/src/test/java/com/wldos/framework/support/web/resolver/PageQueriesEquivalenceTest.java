/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.web.resolver;

import io.github.wldos.common.res.PageQuery;
import io.github.wldos.platform.support.mp.query.PageParamConverter;
import io.github.wldos.platform.support.mp.query.PageParam;
import io.github.wldos.platform.support.mp.query.PagedQuery;
import io.swagger.annotations.ApiModelProperty;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PageQueriesEquivalenceTest {

	@Test
	void pageRequestMatchesPageQueryMap() {
		Map<String, Object> params = new HashMap<>();
		params.put("current", "2");
		params.put("pageSize", "20");
		params.put("keyword", "foo");
		params.put("sorter", "{\"id\":\"desc\"}");

		PageQuery fromMap = new PageQuery(params);

		GameListQuery query = new GameListQuery();
		query.setCurrent(2);
		query.setPageSize(20);
		query.setKeyword("foo");
		query.setSorterFromJson("{\"id\":\"desc\"}");

		PageQuery fromBean = PageParamConverter.toPageQuery(query);

		assertEquals(fromMap.getCurrent(), fromBean.getCurrent());
		assertEquals(fromMap.getPageSize(), fromBean.getPageSize());
		assertEquals(fromMap.getCondition().get("keyword"), fromBean.getCondition().get("keyword"));
		assertEquals(fromMap.getSorter().toString(), fromBean.getSorter().toString());
	}

	@Test
	void pagedQuerySetsConditionBean() {
		GameListFilter filter = new GameListFilter();
		filter.setStatus(1);

		PagedQuery<GameListFilter> pagedQuery = new PagedQuery<>();
		pagedQuery.setCurrent(1);
		pagedQuery.setPageSize(10);
		pagedQuery.setParams(filter);

		PageQuery pageQuery = PageParamConverter.toPageQuery(pagedQuery);
		assertEquals(filter, pageQuery.getConditionBean());
		assertEquals(1, pageQuery.getCondition().get("status"));
	}

	@Test
	void pageNumMapsToCurrent() {
		GameListQuery query = new GameListQuery();
		query.setPageNum(3);
		query.setPageSize(15);
		PageQuery pageQuery = PageParamConverter.toPageQuery(query);
		assertEquals(3, pageQuery.getCurrent());
		assertEquals(15, pageQuery.getPageSize());
	}

	@Test
	void emptyMapPageQuery() {
		PageQuery pageQuery = new PageQuery(null);
		assertEquals(1, pageQuery.getCurrent());
		assertNull(pageQuery.getConditionBean());
	}

	@Test
	void swaggerHiddenPropertySkippedInCondition() {
		GameListQuery query = new GameListQuery();
		query.setKeyword("k");
		query.setInternalHint("must-not-leak");
		PageQuery pageQuery = PageParamConverter.toPageQuery(query);
		assertEquals("k", pageQuery.getCondition().get("keyword"));
		assertNull(pageQuery.getCondition().get("internalHint"));
	}

	public static class GameListQuery extends PageParam {
		private String keyword;

		@ApiModelProperty(hidden = true)
		private String internalHint;

		public String getInternalHint() {
			return internalHint;
		}

		public void setInternalHint(String internalHint) {
			this.internalHint = internalHint;
		}

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
	}

	public static class GameListFilter {
		private Integer status;

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}
	}
}
