/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.plugins;

/**
 * 插件数据管理服务接口
 * 
 * @author 元悉宇宙
 * @date 2026-01-10
 * @version 1.0
 */
public interface IPluginDataService {
    /**
     * 卸载插件（使用指定的app_id）
     *
     * @param appId 应用ID
     */
    boolean uninstallPluginWithAppId(Long appId);
}
