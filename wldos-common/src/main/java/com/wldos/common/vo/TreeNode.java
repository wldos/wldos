/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 树结构节点。
 *
 * @author 树悉猿
 * @date 2021/4/30
 * @version 1.0
 */
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}

	public List<T> getChildren() {
		return children;
	}

	public void setChildren(List<T> children) {
		this.children = children;
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