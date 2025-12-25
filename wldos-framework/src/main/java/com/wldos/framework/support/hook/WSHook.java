/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.hook;

import com.wldos.framework.support.plugins.Handler;
import com.wldos.framework.support.plugins.Invoker;

/**
 * WLDOS钩子管理器
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/15
 */
public interface WSHook {
    /**
     * 挂载一个操作
     *
     * @param extName 扩展点
     * @param instClazz 扩展实现类
     * @param priority 执行优先级
     * @param numArgs 参数个数
     */
    default void addInvoke(String extName, Invoker instClazz, int priority, int numArgs) {
        // do nothing
    }

    /**
     * 调起扩展操作
     *
     * @param extName 扩展点
     * @param args 参数
     */
    default void doInvoke(String extName, Object... args) {}

    /**
     * 挂载一个处理
     *
     * @param extName 扩展点
     * @param instClazz 扩展实现类
     * @param priority 执行优先级
     * @param numArgs 参数个数
     */
    default void addHandler(String extName, Handler instClazz, int priority, int numArgs) {}

    /**
     * 应用扩展处理
     *
     * @param extName 扩展点
     * @param args 参数，约定可变参数的第一个参数是加工对象，处理后返回第一个参数的处理结果
     * @return 可变参数第一个参数的处理结果
     */
    default Object applyHandler(String extName, Object... args) {return null;}

    /**
     * 删除某个具体的hook实例
     *
     * @param hookVO 绑定扩展点和实现类的hook实例
     */
    default void removeHook(Hook hookVO) {}

    /**
     * 删除某个扩展点挂载的所有插件
     *
     * @param extName 扩展点
     */
    default void removeExt(String extName) {}

    /**
     * 触发一个扩展点
     *
     * @param extName 扩展点
     * @param args 参数
     * @return 扩展点返回值
     */
    default Object triggerHook(String extName, Object... args) {return null;}
}
