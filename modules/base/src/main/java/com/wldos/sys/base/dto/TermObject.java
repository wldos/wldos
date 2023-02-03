/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.dto;

import java.util.List;

/**
 * 对象分类关系。
 *
 * @author 树悉猿
 * @date 2021/6/22
 * @version 1.0
 */
public class TermObject {
	private Long id;

	private List<Term> terms;

	public TermObject() {
	}

	public TermObject(Long id, List<Term> terms) {
		this.id = id;
		this.terms = terms;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Term> getTerms() {
		return terms;
	}

	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}
}
