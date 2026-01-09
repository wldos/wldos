/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wldos.platform.support.resource.vo.RouteNode;

import io.swagger.annotations.ApiModel;

import lombok.Getter;
import lombok.Setter;

/**
 * 路由。
 *
 * @author 元悉宇宙
 * @date 2023/4/6
 * @version 1.0
 */
@ApiModel(description = "路由信息")
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class Router extends RouteNode<Router> {

}
