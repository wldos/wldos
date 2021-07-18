/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.util;

import java.util.ArrayList;
import java.util.List;

import com.wldos.support.vo.TreeNode;

/**
 * 树形结构查询。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
public class TreeUtil {
	/**
	 * 把数据库实体bean包装为TreeNode，免去VO创建树形结构
	 *
	 * @param treeNodeList
	 * @param root
	 * @param <Entity>
	 * @return
	 */
	public static <Entity> List<TreeNode<Entity>> buildEntity(List<TreeNode<Entity>> treeNodeList, long root) {
		return build(treeNodeList, root);
	}

	/**
	 * 两层循环实现建树
	 *
	 * @param treeNodes 传入的树节点列表
	 * @return
	 */
	public static <T extends TreeNode> List<T> build(List<T> treeNodes, long root) {

		List<T> trees = new ArrayList<T>();

		for (T treeNode : treeNodes) {
			if (root == treeNode.getParentId()) {

				trees.add(treeNode);
			}

			for (T it : treeNodes) {
				if (it.getParentId().equals(treeNode.getId())) {
					if (treeNode.getChildren() == null) {
						treeNode.setChildren(new ArrayList<TreeNode>());
					}
					treeNode.add(it);
				}
			}
		}
		return trees;
	}

	/**
	 * 使用递归方法建树
	 *
	 * @param treeNodes
	 * @return
	 */
	public static <T extends TreeNode> List<T> buildByRecursive(List<T> treeNodes, long root) {
		List<T> trees = new ArrayList<T>();
		for (T treeNode : treeNodes) {
			if (root == treeNode.getParentId()) {
				trees.add(findChildren(treeNode, treeNodes));
			}
		}
		return trees;
	}

	/**
	 * 递归查找子节点
	 *
	 * @param treeNodes
	 * @return
	 */
	public static <T extends TreeNode> T findChildren(T treeNode, List<T> treeNodes) {
		for (T it : treeNodes) {
			if (treeNode.getId().equals(it.getParentId())) {
				if (treeNode.getChildren() == null) {
					treeNode.setChildren(new ArrayList<TreeNode>());
				}
				treeNode.add(findChildren(it, treeNodes));
			}
		}
		return treeNode;
	}

}

