/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.res;

/**
 * 对象类消息响应模板。
 *
 * @author 树悉猿
 * @date 2021/4/29
 * @version 1.0
 */
public class DomainResult extends Result {

    private Object data;
    
    public DomainResult data(Object data) {
    	this.setStatus(200);
    	this.setMessage("ok");
        this.setData(data);
        return this;
    }

    public Object getData() {
        return data;
    }

	public void setData(Object data) {
		this.data = data;
	}

    public DomainResult modify(Object data) {
        this.setData(data);
        return this;
    }
}