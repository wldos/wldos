/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.system;

import java.util.List;

import com.wldos.platform.support.system.entity.WoOptions;

/**
 * 系统选项操作开瓶器。
 *
 * @author 元悉宇宙
 * @date 2023/4/9
 * @version 1.0
 */
public interface OptionsOpener {
	List<WoOptions> getAllSysOptionsByOptionType(String optionType);
	String getWebRoot();
}
