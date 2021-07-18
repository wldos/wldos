/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.boot.util.LambdaSafe;
import org.springframework.boot.util.LambdaSafe.Callbacks;
import org.springframework.stereotype.Component;

/**
 * hook是程序扩展的设计，在指定的地方声明hook调用，在指定的地方定义某个hook的函数，在运行时发起定制hook函数的调用以实现定制化的能力扩展。
 * hook包括两种：无返回值的action操作、有返回值的filter过滤器操作。
 *
 * @author 树悉猿
 * @date 2021/6/15
 * @version 1.0
 */
@Component
public class WSHook {
	/** Hook回调 */
	public Vector callbacks = new Vector();

	/** Hook按优先级排序的键 */
	public Vector iterations = new Vector();

	/** Hook迭代的当前优先级 */
	public Vector curPriority = new Vector();

	/** 本hook递归调用的层数 */
	private int nestingLevel = 0;

	/** 是否在执行action钩子 */
	private boolean isExecAction = false;

	public <T> void addFilter(String hookName, Consumer<T> callback, int priority, int numArgs) {

	}

	public <R, T>  R applyFilter(Function<T, R> function, Object... args) {
		//return LambdaSafe.callback(function.getClass(), function, args).withLogger();
		return null;
	}
}
