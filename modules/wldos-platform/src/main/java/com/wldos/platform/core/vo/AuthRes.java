/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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
 * 授权资源树节点。
 *
 * @author 元悉宇宙
 * @date 2021/5/21
 * @version 1.0
 */
@ApiModel(description = "授权资源树节点")
@Getter
@Setter
public class AuthRes extends TreeNode<AuthRes> {
	@ApiModelProperty(value = "资源标题", example = "用户管理")
	private String title;

	@ApiModelProperty(value = "资源键值", example = "1")
	private String key;

	@ApiModelProperty(value = "是否禁用", example = "false")
	private boolean disabled;

	public AuthRes() {
	}

	private AuthRes(String title, String key, Long id, Long parentId) {
		this.title = title;
		this.key = key;
		this.id = id;
		this.parentId = parentId;
	}

	public static AuthRes of(String title, String key, Long id, Long parentId) {
		return new AuthRes(title, key, id, parentId);
	}

	@Override
	public String toString() {
		return "{title: " + title + ", key: " + key + ", children: " + this.children + ", disabled: " + disabled + "}";
	}
}
