/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 树结构节点。
 *
 * @author 元悉宇宙
 * @date 2021/4/30
 * @version 1.0
 */
@Getter
@Setter
public class TreeNode<T> {
	protected Long id;

	protected Long parentId;

	// 排序时必须设置displayOrder
	protected Long displayOrder;

	protected List<T> children = null;

	public TreeNode() {
	}

	public TreeNode(Long id) {
		this();
		this.id = id;
	}

	public TreeNode(Long id, Long parentId) {
		this(id);
		this.parentId = parentId;
	}

	public TreeNode(Long id, Long parentId, Long displayOrder) {
		this(id, parentId);
		this.displayOrder = displayOrder;
	}

	public TreeNode(Long id, Long parentId, List<T> child) {
		this(id, parentId);
		this.children = child;
	}

	public void add(T node) {
		if (this.children == null) {
			this.children = new ArrayList<T>();
		}
		this.children.add(node);
	}

	public void addAll(List<T> nodes) {
		if (this.children == null) {
			this.children = new ArrayList<T>();
		}
		this.children.addAll(nodes);
	}
}