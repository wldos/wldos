/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import java.util.List;
import java.util.Map;

import io.github.wldos.framework.support.auth.vo.Token;
import io.github.wldos.platform.support.auth.vo.UserInfo;
import io.github.wldos.platform.support.resource.vo.Menu;
import io.github.wldos.platform.support.resource.vo.Route;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录后返回用户信息。
 *
 * <p>{@code isManageSide} 语义上表示用户端 / 管理端产品语境（与 wldos 双端一致），便于前端差异化渲染；服务端当前不在 {@link com.wldos.platform.core.service.UserService#queryUser} 中计算，DTO 默认 0；是否出现在响应体视序列化配置。</p>
 *
 * @author Yuanxi Universe
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

	@ApiModelProperty(value = "用户端/管理端语境占位：0=用户端向，1=管理端向；由 wldos-ui 前端区分，服务端当前默认不计算", example = "0")
	private int isManageSide = 0;
}