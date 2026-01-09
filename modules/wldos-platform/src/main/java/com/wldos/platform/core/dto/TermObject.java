/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dto;

import java.util.List;

import com.wldos.platform.support.term.dto.Term;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 对象分类关系。
 *
 * @author 元悉宇宙
 * @date 2021/6/22
 * @version 1.0
 */
@ApiModel(description = "对象分类关系")
@Getter
@Setter
public class TermObject {
	@ApiModelProperty(value = "对象ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "分类项列表")
	private List<Term> terms;

	public TermObject() {
	}

	public TermObject(Long id, List<Term> terms) {
		this.id = id;
		this.terms = terms;
	}
}
