/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.core.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wldos.support.resource.vo.RouteNode;

/**
 * 路由。
 *
 * @author 元悉宇宙
 * @date 2023/4/6
 * @version 1.0
 */
@JsonInclude(Include.NON_NULL)
public class Router extends RouteNode<Router> {

}
