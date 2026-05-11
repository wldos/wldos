/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */

/**
 * 文件存储与 {@link io.github.wldos.framework.support.internal.Base#store}（{@link io.github.wldos.framework.support.storage.IStore}）对齐：
 * 上传、URL 解析、落盘摘要、本地读流等均以契约为准；业务在 Controller / Service 中通过基类字段 {@code store} 调用即可。
 */
package com.wldos.framework.support.storage;
