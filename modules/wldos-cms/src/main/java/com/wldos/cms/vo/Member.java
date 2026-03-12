/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 成员。
 *
 * @author 元悉宇宙
 * @date 2021/6/20
 * @version 1.0
 */
@ApiModel(description = "成员信息")
@Getter
@Setter
public class Member {
	@ApiModelProperty(value = "用户ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "昵称", example = "用户名")
	private String nickname;

	@ApiModelProperty(value = "头像URL", example = "https://example.com/avatar.jpg")
	private String avatar;
}
