/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.support.plugins;

/**
 * 业务扩展调用器，给某个功能追加无返回操作。
 *
 * @author 元悉宇宙
 * @date 2023/9/12
 * @version 1.0
 */
public interface Invoker {
	void invoke(Object... args);
}
