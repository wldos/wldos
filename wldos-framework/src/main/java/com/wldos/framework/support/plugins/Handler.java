/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.plugins;

/**
 * 业务扩展处理器，给某个功能增加再处理操作。
 *
 * @author 元悉宇宙
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
