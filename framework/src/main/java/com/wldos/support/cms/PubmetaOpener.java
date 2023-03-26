/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.cms;

import java.util.List;

import com.wldos.support.cms.entity.KPubmeta;

/**
 * 发布元语操作开瓶器。
 *
 * @author 树悉猿
 * @date 2023/3/26
 * @version 1.0
 */
public interface PubmetaOpener {
	List<KPubmeta> queryPubMetaByPubIds(List<Long> pids);
}
