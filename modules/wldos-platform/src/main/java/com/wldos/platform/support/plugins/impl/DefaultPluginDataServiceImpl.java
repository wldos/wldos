/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.plugins.impl;

import com.wldos.platform.support.plugins.IPluginDataService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

/**
 * 默认 IPluginDataService 实现类（开源版本）
 * 当 Agent 模块中的 PluginDataService 不存在时使用此实现
 * 使用降级处理：开源版本插件功能不可用，返回 false 表示卸载失败
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2026/01/10
 */
@ConditionalOnMissingClass("com.wldos.support.plugins.service.PluginDataService")
@Component
public class DefaultPluginDataServiceImpl implements IPluginDataService {

	@Override
	public boolean uninstallPluginWithAppId(Long appId) {
		// 开源版本：插件功能不可用，返回 false 表示卸载失败
		// AppService 中有兜底逻辑处理这种情况
		return false;
	}
}
