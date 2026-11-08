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
 * 主体认证列表行。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("主体认证列表行")
@Getter
@Setter
public class SubjectAuthVO {

	@ApiModelProperty("认证id")
	private Long id;

	@ApiModelProperty("主体类型id")
	private Long subjectTypeId;

	@ApiModelProperty("主体类型名")
	private String subjectTypeName;

	@ApiModelProperty("用户id")
	private Long userId;

	@ApiModelProperty("登录名")
	private String loginName;

	@ApiModelProperty("昵称")
	private String nickname;

	@ApiModelProperty("归属租户id（0/空=平台共享）")
	private Long comId;

	@ApiModelProperty("归属站点id")
	private Long domainId;

	@ApiModelProperty("站点名")
	private String domainName;

	@ApiModelProperty("创建时间")
	private Timestamp createTime;
}
