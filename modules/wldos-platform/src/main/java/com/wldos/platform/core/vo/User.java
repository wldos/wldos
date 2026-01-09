/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import java.util.List;
import java.util.Map;

import com.wldos.framework.support.auth.vo.Token;
import com.wldos.platform.support.auth.vo.UserInfo;
import com.wldos.platform.support.resource.vo.Menu;
import com.wldos.platform.support.resource.vo.Route;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录后返回用户信息。
 *
 * @author 元悉宇宙
 * @date 2021-04-30
 * @version V1.0
 */
@ApiModel(description = "用户登录信息")
@SuppressWarnings("unused")
@Getter
@Setter
public class User {
	@ApiModelProperty(value = "用户基本信息")
	private UserInfo userInfo;

	@ApiModelProperty(value = "用户菜单列表")
	private List<Menu> menu;

	@ApiModelProperty(value = "路由信息，键为路由路径，值为路由配置")
	private Map<String, Route> route;

	@ApiModelProperty(value = "当前用户权限列表")
	private List<String> currentAuthority;

	@ApiModelProperty(value = "认证令牌")
	private Token token;

	@ApiModelProperty(value = "是否为管理端，0=否，1=是", example = "0")
	private int isManageSide = 0;
}