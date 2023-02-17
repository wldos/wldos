/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import java.util.ArrayList;
import java.util.Comparator;
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
	 * 给定根节点和源数据强制生成树(特殊情况下存在的孤点或断层按顺序排列在一级节点上)
	 * 虽然正常的业务数据应该是完整有根树，但是仍然存在非有根树情况
	 *
	 * @param treeNodes 源数据
	 * @param root 顶级根节点id
	 */
	public static <T extends TreeNode<T>> List<T> build(final List<T> treeNodes, final long root) {

		List<T> record = new ArrayList<>(); // 记录上树节点，防止孤点丢失

		List<T> trees = treeNodes.stream().filter(node -> node.getParentId() == root).peek(
				node -> {
					record.add(node); // 一级节点上树
					node.setChildren(getChildren(node, treeNodes, record));
					// 子节点排序
					if (node.getChildren().isEmpty()) {
						node.setChildren(null); // 空集合，会导致前端出现叶子节点的+号

					} else
						node.getChildren().sort(Comparator.nullsLast(
								Comparator.comparing(
										TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));

				}).collect(Collectors.toList());;

		// 求差集
		if (record.size() < treeNodes.size()) {
			List<T> lonely = treeNodes.stream().filter(node -> !record.contains(node)).collect(Collectors.toList());
			trees.addAll(lonely);
		}

		trees.sort(Comparator.nullsLast(
				Comparator.comparing(
						TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));

		return trees;
	}

	/**
	 * 递归查询treeNode的子节点
	 *
	 * @param treeNode 要找子节点的节点
	 * @param treeNodes 源数据
	 * @param record 记录上树节点
	 */
	public static <T extends TreeNode<T>> List<T> getChildren(final TreeNode<T> treeNode, final List<T> treeNodes, final List<T> record) {
		return treeNodes.stream().filter(node -> node.getParentId().equals(treeNode.getId()))
				.peek(node -> {
					record.add(node); // 子节点上树
					node.setChildren(getChildren(node, treeNodes, record));
					// 子节点排序
					if (node.getChildren().isEmpty()) {
						node.setChildren(null); // 空集合，会导致前端出现叶子节点的+号

					} else
						node.getChildren().sort(Comparator.nullsLast(
								Comparator.comparing(
										TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));
				}).collect(Collectors.toList());
	}

	/**
	 * 两层循环实现建树(存在丢失孤点现象)
	 *
	 * @param treeNodes 传入的树节点列表
	 * @param root 树的根节点id
	 * @return 树状结构列表
	 */
	public static <T extends TreeNode<T>> List<T> buildFor(List<T> treeNodes, long root) {

		List<T> trees = new ArrayList<>();

		for (T treeNode : treeNodes) {
			if (root == treeNode.getParentId()) {

				trees.add(treeNode);
			}

			for (T it : treeNodes) {
				if (it.getParentId().equals(treeNode.getId())) {
					treeNode.add(it);
				}
			}
			if (!ObjectUtils.isBlank(treeNode.getChildren())) {
				treeNode.getChildren().sort(Comparator.nullsLast(
						Comparator.comparing(
								TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));
			}
		}

		trees.sort(Comparator.nullsLast(
				Comparator.comparing(
						TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));
		return trees;
	}

	/**
	 * 使用递归方法建树(存在丢失孤点现象)
	 *
	 * @param treeNodes 树节点类别
	 * @param root 树根节点id
	 * @return 树状列表
	 */
	public static <T extends TreeNode<T>> List<T> buildByRecursive(List<T> treeNodes, long root) {
		List<T> trees = new ArrayList<>();
		for (T treeNode : treeNodes) {
			if (root == treeNode.getParentId()) {
				trees.add(findChildren(treeNode, treeNodes));
			}
		}
		trees.sort(Comparator.nullsLast(
				Comparator.comparing(
						TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));
		return trees;
	}

	/**
	 * 递归查找子节点
	 *
	 * @param treeNode 当前树节点
	 * @param treeNodes 树节点类别
	 * @return 树状列表
	 */
	public static <T extends TreeNode<T>> T findChildren(T treeNode, List<T> treeNodes) {
		for (T it : treeNodes) {
			if (treeNode.getId().equals(it.getParentId())) {
				treeNode.add(findChildren(it, treeNodes));
			}
		}
		if (!ObjectUtils.isBlank(treeNode.getChildren())) {
			treeNode.getChildren().sort(Comparator.nullsLast(
					Comparator.comparing(
							TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));
		}
		return treeNode;
	}

	/**
	 * 构建扁平树，以一级根(父为顶级根节点)节点为根把子根节点变成叶子节点，平铺所有叶子节点，实现扁平化大、小类矮树
	 *
	 * @param treeNodes 源数据
	 * @param root 顶级根节点id
	 */
	public static <T extends TreeNode<T>> List<T> buildFlatTree(final List<T> treeNodes, final long root) {

		// 一级节点上树
		List<T> trees = treeNodes.stream().filter(node -> node.getParentId() == root)
				.sorted(Comparator.nullsLast(
				Comparator.comparing(
						TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo)))).collect(Collectors.toList());

		List<T> recordWithChild = new ArrayList<>();

		trees.forEach(node -> {
			List<T> nodeChildren = flatChildren(node, treeNodes);
			// 子节点排序
			if (nodeChildren.isEmpty()) {
				return;
			}
			nodeChildren.sort(Comparator.nullsLast(
					Comparator.comparing(
							TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));
			node.setChildren(nodeChildren);
			recordWithChild.add(node);
		});

		return recordWithChild;
	}

	/**
	 * 递归查询treeNode的子节点，平铺到其后
	 *
	 * @param treeNode 要找子节点的节点
	 * @param treeNodes 源数据
	 */
	public static <T extends TreeNode<T>> List<T> flatChildren(final TreeNode<T> treeNode, final List<T> treeNodes) {
		return treeNodes.stream().filter(node -> node.getParentId().equals(treeNode.getId()))
				.peek(node -> {
					List<T> nodeChildren = flatChildren(node, treeNodes);

					if (nodeChildren.isEmpty()) {
						return;
					}
					// 子节点排序
					nodeChildren.sort(Comparator.nullsLast(
							Comparator.comparing(
									TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));
				}).collect(Collectors.toList());
	}

	/**
	 * 构建扁平树，以一级根(父为顶级根节点)节点为根把子根节点变成叶子节点，平铺所有叶子节点，实现扁平化大、小类矮树
	 *
	 * @param treeNodes 传入的树节点列表
	 * @return 树状结构列表
	 */
	public static <T extends TreeNode<T>> List<T> buildFlatTreeOld(List<T> treeNodes, long root) {

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