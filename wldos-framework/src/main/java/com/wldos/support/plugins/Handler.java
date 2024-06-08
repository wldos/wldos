/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.support.plugins;

/**
 * 业务扩展处理器，给某个功能增加再处理操作。
 *
 * @author 树悉猿
 * @date 2023/9/12
 * @version 1.0
 */
public interface Handler {
	/**
	 * 处理扩展
	 *
	 * @param args 参数，约定args[0]为处理对象，其余为辅助参数
	 * @return 对args[0]的处理结果
	 */
	Object applyHandler(Object... args);
}
