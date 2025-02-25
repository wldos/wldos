/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.plugin.extension;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * WLDOS系统扩展点定义
 */
@Getter
public enum ExtensionPoint {

    // 用户相关扩展点
    USER_LOGIN("user.login", "用户登录", "用户登录时的扩展处理", ExtensionType.INVOKE),
    USER_REGISTER("user.register", "用户注册", "用户注册时的扩展处理", ExtensionType.HANDLER),
    USER_LOGOUT("user.logout", "用户登出", "用户登出时的扩展处理", ExtensionType.INVOKE),

    // 文章相关扩展点
    ARTICLE_PRE_PUBLISH("article.prePublish", "文章发布前", "文章发布前的内容处理", ExtensionType.HANDLER),
    ARTICLE_POST_PUBLISH("article.postPublish", "文章发布后", "文章发布后的处理", ExtensionType.INVOKE),
    ARTICLE_VIEW("article.view", "文章查看", "文章内容查看时的处理", ExtensionType.HANDLER),

    // 订单相关扩展点
    ORDER_CREATE("order.create", "订单创建", "订单创建时的处理", ExtensionType.HANDLER),
    ORDER_PAY("order.pay", "订单支付", "订单支付时的处理", ExtensionType.HANDLER),
    ORDER_CANCEL("order.cancel", "订单取消", "订单取消时的处理", ExtensionType.INVOKE),

    // 系统相关扩展点
    SYSTEM_STARTUP("system.startup", "系统启动", "系统启动时的处理", ExtensionType.INVOKE),
    SYSTEM_SHUTDOWN("system.shutdown", "系统关闭", "系统关闭时的处理", ExtensionType.INVOKE),

    // 数据处理扩展点
    DATA_IMPORT("data.import", "数据导入", "数据导入时的处理", ExtensionType.HANDLER),
    DATA_EXPORT("data.export", "数据导出", "数据导出时的处理", ExtensionType.HANDLER),
    DATA_SYNC("data.sync", "数据同步", "数据同步时的处理", ExtensionType.HANDLER);

    private final String code;        // 扩展点编码
    private final String name;        // 扩展点名称
    private final String description; // 扩展点描述
    private final ExtensionType type; // 扩展点类型

    ExtensionPoint(String code, String name, String description, ExtensionType type) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    /**
     * 根据编码获取扩展点
     */
    public static Optional<ExtensionPoint> getByCode(String code) {
        return Arrays.stream(values())
                .filter(point -> point.getCode().equals(code))
                .findFirst();
    }

    /**
     * 检查扩展点是否存在
     */
    public static boolean exists(String code) {
        return getByCode(code).isPresent();
    }

    /**
     * 验证扩展点类型是否匹配
     */
    public static boolean validateType(String code, ExtensionType type) {
        return getByCode(code)
                .map(point -> point.getType() == type)
                .orElse(false);
    }
}

/**
 * 扩展点类型
 */
enum ExtensionType {
    INVOKE,  // 无返回值的扩展点
    HANDLER  // 有返回值的扩展点
}