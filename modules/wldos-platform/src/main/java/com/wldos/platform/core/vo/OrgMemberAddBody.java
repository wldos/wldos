/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 组织/分群添加成员请求体（数据面 scope：comId、domainId 与 org 行一致）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("组织添加成员请求")
@Getter
@Setter
public class OrgMemberAddBody {

	@ApiModelProperty(value = "组织 id", required = true)
	private Long orgId;

	@ApiModelProperty(value = "用户 id 列表", required = true)
	private List<String> userIds;

	@ApiModelProperty(value = "编制体系 id")
	private Long archId;

	@ApiModelProperty(value = "租户 company id（须 >0；超管代管必填或与 domain 一致）")
	private Long comId;

	@ApiModelProperty(value = "目标站点 domain id（数据面写操作推荐必填）")
	private Long domainId;

	@ApiModelProperty(value = "添加租户管理员时的受托租户 userComId")
	private Long userComId;
}
