/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.hook;

import java.util.Objects;

import com.wldos.framework.support.plugins.Invoker;

/**
 * 一个invoke是指无返回值的hook。
 *
 */
public class InvokeVO extends Hook {

	public InvokeVO(String extName, Invoker clazz, int priority, int numArgs) {
        super(HookType.INVOKE, extName, priority, numArgs, clazz);
    }

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || this.getClass() != o.getClass())
			return false;
		InvokeVO invoke = (InvokeVO) o;
		return (invoke.extName.equals(this.extName) && this.inst.equals(invoke.inst) && this.priority == invoke.priority && this.numArgs == invoke.numArgs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(extName, priority, numArgs);
	}

	public Invoker getInst() {
		return (Invoker) inst;
	}

	public int getPriority() {
		return priority;
	}

	public int getNumArgs() {
		return numArgs;
	}

	@Override
	public String toString() {
		return "{extName: " + this.extName + ", beanName: " + this.inst + ", priority: " + priority + ", numArgs: " + numArgs+ "}";
	}
}
