/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.term;

import java.util.List;

import com.wldos.support.term.dto.Term;

/**
 * 分类项操作。
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
}
