/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import java.sql.Timestamp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 身份登记册成员视图：登记册行 + 用户基础信息；租户管理员带受托租户，站点管理员带绑定站点。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("身份登记册成员")
@Getter
@Setter
public class IdentityMemberVO {

	@ApiModelProperty("绑定记录id（站点管理员为 wo_domain_admin 行 id，其余为登记册行 id）")
	private Long id;

	@ApiModelProperty("用户id")
	private Long userId;

	@ApiModelProperty("登录名")
	private String loginName;

	@ApiModelProperty("昵称")
	private String nickname;

	@ApiModelProperty("受托租户id（仅租户管理员）")
	private Long comId;

	@ApiModelProperty("受托租户名称（仅租户管理员）")
	private String comName;

	@ApiModelProperty("绑定站点id（仅站点管理员）")
	private Long domainId;

	@ApiModelProperty("绑定站点名称（仅站点管理员）")
	private String domainName;

	@ApiModelProperty("登记时间")
	private Timestamp createTime;
}
