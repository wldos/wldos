/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.system;

import java.util.List;

import com.wldos.support.system.entity.WoOptions;

/**
 * 系统选项操作开瓶器。
 *
 * @author 树悉猿
 * @date 2023/4/9
 * @version 1.0
 */
public interface OptionsOpener {
	List<WoOptions> getAllSysOptionsByOptionType(String optionType);
	String getWebRoot();
}
