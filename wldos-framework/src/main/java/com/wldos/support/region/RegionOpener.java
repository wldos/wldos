/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.region;

import com.wldos.support.region.vo.City;

/**
 * 区域操作开瓶器。
 *
 * @author 元悉宇宙
 * @date 2023/3/25
 * @version 1.0
 */
public interface RegionOpener {
	City queryRegionInfoByCode(String city);
}
