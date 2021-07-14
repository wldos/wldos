/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 树结构节点。
 *
 * @Title TreeNode
 * @Package com.wldos.support.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/30
 * @Version 1.0
 */
public class TreeNode<T> {
	protected Long id;

	protected Long parentId;

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
}
