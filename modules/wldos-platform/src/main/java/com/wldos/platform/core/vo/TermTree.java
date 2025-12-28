/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import com.wldos.common.vo.TreeNode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 分类树节点。
 *
 * @author 元悉宇宙
 * @date 2021/6/17
 * @version 1.0
 */
@ApiModel(description = "分类树节点")
@Getter
@Setter
public class TermTree extends TreeNode<TermTree> {
	@ApiModelProperty(value = "分类项ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "分类项名称", example = "技术分类")
	private String name;

	@ApiModelProperty(value = "分类项别名", example = "tech")
	private String slug;

	@ApiModelProperty(value = "信息标志", example = "1")
	private String infoFlag;

	@ApiModelProperty(value = "显示顺序", example = "1")
	private Long displayOrder;

	@ApiModelProperty(value = "是否有效", example = "1")
	private String isValid;

	@ApiModelProperty(value = "分类类型ID", example = "1")
	private Long termTypeId;

	@ApiModelProperty(value = "分类类型", example = "category")
	private String classType;

	@ApiModelProperty(value = "描述", example = "技术相关分类")
	private String description;

	@ApiModelProperty(value = "父分类ID", example = "0")
	private Long parentId;

	@ApiModelProperty(value = "关联数量", example = "10")
	private Long count;
}
