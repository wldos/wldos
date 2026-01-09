/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.plugins.core;

import java.sql.Timestamp;

/**
 * 插件元数据
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/15
 */
public interface IPluginMetadata {
    String getPluginCode();

    void setPluginCode(String pluginCode);

    String getPluginName();

    void setPluginName(String pluginName);

    String getVersion();

    void setVersion(String version);

    String getDescription();

    void setDescription(String description);

    String getAuthor();

    void setAuthor(String author);

    String getMainClass();

    void setMainClass(String mainClass);

    String getPluginStatus();

    void setPluginStatus(String pluginStatus);

    String getAutoStart();

    void setAutoStart(String autoStart);

    String getErrorMessage();

    void setErrorMessage(String errorMessage);

    Timestamp getCreateTime();

    void setCreateTime(Timestamp createTime);

    Timestamp getUpdateTime();

    void setUpdateTime(Timestamp updateTime);
}
