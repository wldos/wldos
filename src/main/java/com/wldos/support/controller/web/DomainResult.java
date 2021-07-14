/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.controller.web;


public class DomainResult extends Result {

    Object data;
    
    public DomainResult data(Object data) {
    	this.setStatus(200);
    	this.setMessage("ok");
        this.setData(data);
        return this;
    }
    public Object getData() {
        return data;
    }

    public DomainResult setData(Object data) {
        this.data = data;
        return this;
    }
}
