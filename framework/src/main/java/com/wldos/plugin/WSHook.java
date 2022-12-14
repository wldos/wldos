/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.plugin;

import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.stereotype.Component;

/**
 * @author 树悉猿
 * @date 2021/6/15
 * @version 1.0
 */
@Component
public class WSHook {
	public Vector callbacks = new Vector();

	public Vector iterations = new Vector();

	public Vector curPriority = new Vector();

	private int nestingLevel = 0;

	private boolean isExecAction = false;

	public <T> void addFilter(String hookName, Consumer<T> callback, int priority, int numArgs) {

	}

	public <R, T>  R applyFilter(Function<T, R> function, Object... args) {
		//return LambdaSafe.callback(function.getClass(), function, args).withLogger();
		return null;
	}
}