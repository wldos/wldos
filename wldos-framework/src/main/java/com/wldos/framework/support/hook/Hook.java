/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.hook;

/**
 * wldos Hook。
 *
 * @author 元悉宇宙
 * @date 2023/9/10
 * @version 1.0
 */
public class Hook {
	protected final String extName;

	protected final int priority;

	protected final int numArgs;

	protected final Object inst; // 可为Handler、Invoker等

	protected final HookType type;

	public Hook(HookType type, String extName, int priority, int numArgs, Object inst) {
		this.type = type;
		this.extName = extName;
		this.priority = priority;
		this.numArgs = numArgs;
		this.inst = inst;
	}

	public HookType getType() { return type; }
	public String getExtName() { return extName; }
	public int getPriority() { return priority; }
	public int getNumArgs() { return numArgs; }
	public Object getInst() { return inst; }


}



