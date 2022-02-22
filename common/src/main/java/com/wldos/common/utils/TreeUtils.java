/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.common.vo.TreeNode;

/**
 * 树形结构查询。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@SuppressWarnings("unused")
public class TreeUtils {

	/**
	 * 两层循环实现建树
	 *
	 * @param treeNodes 传入的树节点列表
	 * @return 树状结构列表
	 */
	public static <T extends TreeNode<T>> List<T> build(List<T> treeNodes, long root) {

		List<T> trees = new ArrayList<>();

		for (T treeNode : treeNodes) {
			if (root == treeNode.getParentId()) {

				trees.add(treeNode);
			}

			for (T it : treeNodes) {
				if (it.getParentId().equals(treeNode.getId())) {
					if (treeNode.getChildren() == null) {
						treeNode.setChildren(new ArrayList<>());
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
	 * @param treeNodes 树节点类别
	 * @return 树状列表
	 */
	public static <T extends TreeNode<T>> List<T> buildByRecursive(List<T> treeNodes, long root) {
		List<T> trees = new ArrayList<>();
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
	 * @param treeNodes 树节点类别
	 * @return 树状列表
	 */
	public static <T extends TreeNode<T>> T findChildren(T treeNode, List<T> treeNodes) {
		for (T it : treeNodes) {
			if (treeNode.getId().equals(it.getParentId())) {
				if (treeNode.getChildren() == null) {
					treeNode.setChildren(new ArrayList<>());
				}
				treeNode.add(findChildren(it, treeNodes));
			}
		}
		return treeNode;
	}

	/**
	 * 构建扁平树，以一级根(父为顶级根节点)节点为根把子根节点变成叶子节点，平铺所有叶子节点，实现扁平化大、小类矮树
	 *
	 * @param treeNodes 传入的树节点列表
	 * @return 树状结构列表
	 */
	public static <T extends TreeNode<T>> List<T> buildFlatTree(List<T> treeNodes, long root) {

		List<T> trees = new ArrayList<>();

		Map<Long, Map<Long, T>> treeMap = new HashMap<>();

		for (T treeNode : treeNodes) {// 遍历取出根节点
			if (root == treeNode.getParentId()) { // 一级根节点

				Map<Long , T> temp = new HashMap<>();
				temp.put(treeNode.getId(), treeNode);
				treeMap.put(treeNode.getId(), temp);
			}

			for (T it : treeNodes) { // 遍历取子节点
				if (it.getParentId().equals(treeNode.getId())) { // 当前根节点的子节点或叶子节点
					if (treeMap.containsKey(it.getParentId())) {
						Map<Long, T> child = treeMap.get(treeNode.getId());
						child.put(it.getId(), it);
						treeMap.put(it.getParentId(), child);
					} else {
						for (Map.Entry<Long, Map<Long, T>> parent : treeMap.entrySet()) {
							Map<Long, T> child = parent.getValue();
							if (child.containsKey(it.getParentId())) {
								child.put(it.getId(), it);
								treeMap.put(parent.getKey(), child);
								break;
							}
						}
					}
				}
			}
		}

		return treeMap.entrySet().parallelStream().map(node -> {
			Long key = node.getKey();
			Map<Long, T> temp = node.getValue();
			T rootNode = temp.get(key);
			temp.entrySet().stream().filter(entry -> !entry.getKey().equals(key)).forEach(t -> rootNode.add(t.getValue()));
			return rootNode;
		}).collect(Collectors.toList());
	}
}

