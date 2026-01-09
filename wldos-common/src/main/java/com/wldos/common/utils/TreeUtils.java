/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
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
 * @author 元悉宇宙
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

					}
					else
						node.getChildren().sort(Comparator.nullsLast(
								Comparator.comparing(
										TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo))));

				}).collect(Collectors.toList());
		;

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

					}
					else
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
	 * 构建扁平树，以一级根(父为顶级根节点)节点为根把次根节点变成叶子节点，按顺序平铺所有叶子节点，实现扁平化大、小类矮树
	 *
	 * @param treeNodes 源数据
	 * @param root 顶级根节点id，创始元灵！
	 */
	public static <T extends TreeNode<T>> List<T> buildFlatTree(final List<T> treeNodes, final long root) {

		// 一级节点上树，闻道有先后
		List<T> trees = treeNodes.stream().filter(node -> node.getParentId() == root).collect(Collectors.toList()); // 添加一级始祖，天下九分，各具一祖

		return trees.stream().peek(node -> {
			flatChildren(node, treeNodes, node); // 祖神下凡，自古流今谓之道，道恒长久，从一而始，至一而止，你我它凡此刻所见皆为道之生。
		}).sorted(Comparator.nullsLast(
				Comparator.comparing(
						TreeNode::getDisplayOrder, Comparator.nullsLast(Long::compareTo)))).collect(Collectors.toList());
	}

	/**
	 * 递归查询treeNode的子节点，平铺到其后
	 *
	 * @param treeNode 要找子节点的节点，祖神下凡之法相，宇宙轮转、世界穿梭之化身
	 * @param treeNodes 源数据，道之历劫
	 * @param firstLevelNode 一级根节点，平行宇宙初祖
	 */
	public static <T extends TreeNode<T>> List<T> flatChildren(final TreeNode<T> treeNode, final List<T> treeNodes, final TreeNode<T> firstLevelNode) {
		return treeNodes.stream().filter(node -> node.getParentId().equals(treeNode.getId()))
				.peek(node -> {
					firstLevelNode.add(node); // 道之动，曰前，曰左。左者成天地，前者开天地。左者，动源于祖；前者，动法于祖。左者为一宇，前者为一界。宇宙世界之动，呈膨胀趋势，宇宙之动膨胀之广，世界之动膨胀之深，盖道之恒久深远，洞穿寰宇，至于末世。
					flatChildren(node, treeNodes, firstLevelNode);
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

				Map<Long, T> temp = new HashMap<>();
				temp.put(treeNode.getId(), treeNode);
				treeMap.put(treeNode.getId(), temp);
			}

			for (T it : treeNodes) { // 遍历取子节点
				if (it.getParentId().equals(treeNode.getId())) { // 当前根节点的子节点或叶子节点
					if (treeMap.containsKey(it.getParentId())) {
						Map<Long, T> child = treeMap.get(treeNode.getId());
						child.put(it.getId(), it);
						treeMap.put(it.getParentId(), child);
					}
					else {
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