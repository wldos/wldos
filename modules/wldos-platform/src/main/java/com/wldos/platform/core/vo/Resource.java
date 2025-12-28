/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import com.wldos.common.vo.TreeNode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@ApiModel(description = "资源信息")
@Getter
@Setter
public class Resource extends TreeNode<Resource> {

	@ApiModelProperty(value = "资源ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "资源编码", example = "RES001")
	private String resourceCode;

	@ApiModelProperty(value = "资源名称", example = "用户管理")
	private String resourceName;

	@ApiModelProperty(value = "资源路径", example = "/admin/sys/user")
	private String resourcePath;

	@ApiModelProperty(value = "组件路径", example = "/admin/sys/user/index")
	private String componentPath;

	@ApiModelProperty(value = "图标", example = "user")
	private String icon;

	@ApiModelProperty(value = "资源类型", example = "menu")
	private String resourceType;

	@ApiModelProperty(value = "请求方法", example = "GET")
	private String requestMethod;

	@ApiModelProperty(value = "打开方式", example = "_self")
	private String target;

	@ApiModelProperty(value = "应用ID", example = "1")
	private Long appId;

	@ApiModelProperty(value = "父资源ID", example = "0")
	private Long parentId;

	@ApiModelProperty(value = "显示顺序", example = "1")
	private Long displayOrder;

	@ApiModelProperty(value = "是否有效", example = "1")
	private String isValid;

	@ApiModelProperty(value = "备注", example = "用户管理菜单")
	private String remark;

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

	@ApiModelProperty(value = "版本号", hidden = true)
	private Long versions;
}
