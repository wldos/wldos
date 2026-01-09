/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.plugins;

public interface IPluginDataService {
    /**
     * 安装插件（使用指定的app_id）
     *
     * @param appId 应用ID
     */
    boolean uninstallPluginWithAppId(Long appId);
}
