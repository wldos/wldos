/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoUsermeta;

/**
 * 帖子扩展数据repository操作类
 *
 * @author 元悉宇宙
 * @date 2021/4/17
 * @version 1.0
 */
public interface UsermetaDao extends BaseDao<WoUsermeta, Long> {
	WoUsermeta findByUserIdAndMetaKey(Long userId, String MetaKey);

	List<WoUsermeta> findAllByUserId(Long userId);
}
