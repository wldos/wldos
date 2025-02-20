/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.base.dto;

import java.util.List;

import com.wldos.support.term.dto.Term;

/**
 * 对象分类关系。
 *
 * @author 元悉宇宙
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
