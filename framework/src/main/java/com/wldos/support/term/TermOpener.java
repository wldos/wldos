/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.term;

import java.util.List;

import com.wldos.common.dto.LevelNode;
import com.wldos.support.term.dto.Term;

/**
 * 分类项操作开瓶器，仅用于移花接木。
 *
 * @author 树悉猿
 * @date 2023/3/22
 * @version 1.0
 */
public interface TermOpener {
	boolean isAdmin(Long userId);

	/**
	 * 根据类型id批量查询所有分类项及其类型
	 *
	 * @param termTypeIds 批量类型id
	 * @return 分类项列表
	 */
	List<Term> queryAllByTermTypeIds(List<Long> termTypeIds);

	/**
	 * 通过分类项别名查询分类项
	 *
	 * @param slugTerm 分类项别名
	 * @return 分类项信息
	 */
	Term queryTermBySlugTerm(String slugTerm);

	/**
	 * 通过子节点id查询所有父节点(含子节点自身)并缓存
	 *
	 * @param cId 要查询的子节点id
	 * @return 一棵节点树
	 */
	List<LevelNode> queryTermTreeByChildId(Object cId);

	/**
	 * 根据对象id、分类类型查询某一类型的分类项列表
	 *
	 * @param objectId 对象id
	 * @param classType 分类类型：分类目录、标签等
	 * @return 分类项列表，不会为空，默认未分组
	 */
	List<Term> findAllByObjectAndClassType(Long objectId, String classType);
}
