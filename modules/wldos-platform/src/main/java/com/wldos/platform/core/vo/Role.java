/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import io.github.wldos.common.vo.TreeNode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@ApiModel(description = "角色信息")
@Getter
@Setter
public class Role extends TreeNode<Role> {

	@ApiModelProperty(value = "角色ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "角色编码", example = "admin")
	private String roleCode;

	@ApiModelProperty(value = "角色名称", example = "管理员")
	private String roleName;

	@ApiModelProperty(value = "角色描述", example = "系统管理员")
	private String roleDesc;

	@ApiModelProperty(value = "角色类型", example = "system")
	private String roleType;

	@ApiModelProperty(value = "数据策略", example = "ORG_SUBTREE",
			allowableValues = "SELF,ORG,ORG_SUBTREE,TENANT,DOMAIN,TENANT_DOMAIN,ALL")
	private String dataPolicy;

	@ApiModelProperty(value = "租户公司ID", example = "0")
	private Long comId;

	@ApiModelProperty(value = "站点域ID", example = "1")
	private Long domainId;

	@ApiModelProperty(value = "父角色ID", example = "0")
	private Long parentId;

	@ApiModelProperty(value = "显示顺序", example = "1")
	private Long displayOrder;

	@ApiModelProperty(value = "是否有效", example = "1")
	private String isValid;

	@ApiModelProperty(value = "创建人ID", hidden = true)
	private Long createBy;

	@ApiModelProperty(value = "创建IP", hidden = true)
	private String createIp;

	@ApiModelProperty(value = "创建时间", hidden = true)
	private Timestamp createTime;

	@ApiModelProperty(value = "更新人ID", hidden = true)
	private Long updateBy;

	@ApiModelProperty(value = "更新IP", hidden = true)
	private String updateIp;

	@ApiModelProperty(value = "更新时间", hidden = true)
	private Timestamp updateTime;

	@ApiModelProperty(value = "删除标志", hidden = true)
	private String deleteFlag;

	@ApiModelProperty(value = "是否可改角色定义（false=仅可配三权）", example = "true")
	private Boolean managed;

	@ApiModelProperty(value = "是否可授权/配管理范围", example = "true")
	private Boolean assignable;

	@ApiModelProperty(value = "是否系统内置会员角色（编码不可改，可配授权）", example = "false")
	private Boolean systemBuiltIn;

	@ApiModelProperty(value = "平台模板行（com_id=0 且 domain_id 为空）", example = "false")
	private Boolean kindTemplate;

	@ApiModelProperty(value = "是否允许物理删除", example = "true")
	private Boolean deletable;

	@ApiModelProperty(value = "版本号", hidden = true)
	private Long versions;
}
