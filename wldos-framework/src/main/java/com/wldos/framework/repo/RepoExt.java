/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.repo;

import com.wldos.base.tools.CommonOperation;

/**
 * 在repo层引入数据库和支撑平台的增强扩展。
 * 当前BaseRepo提供的框架功能不够时，引入此接口，可以获得更多扩展能力。当这些能力仍不足够时，可以自行扩展。
 *
 * @author 元悉宇宙
 * @date 2023/10/23
 * @version 1.0
 */
public interface RepoExt {
	CommonOperation getCommonOperation();

	/**
	 * 有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entity 实体
	 * @param isAutoFill 是否自动填充公共字段，不存在或需要手动设置公共字段时设置为false，比mybatis-plus更快捷
	 */
	<E> Long insertSelective(E entity, boolean isAutoFill);


}
