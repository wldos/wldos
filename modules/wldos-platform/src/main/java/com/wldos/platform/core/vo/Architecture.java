/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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

@ApiModel(description = "体系结构信息")
@Getter
@Setter
public class Architecture extends TreeNode<Architecture> {

	@ApiModelProperty(value = "体系编码", example = "ARCH001")
	private String archCode;

	@ApiModelProperty(value = "体系名称", example = "技术部")
	private String archName;

	@ApiModelProperty(value = "体系描述", example = "技术部门体系结构")
	private String archDesc;

	@ApiModelProperty(value = "公司ID", example = "1")
	private Long comId;

	@ApiModelProperty(value = "显示顺序", example = "1")
	private Long displayOrder;

	@ApiModelProperty(value = "是否有效", example = "1")
	private String isValid;

	@ApiModelProperty(value = "创建人ID", hidden = true)
	private Long createBy;

	@ApiModelProperty(value = "创建时间", hidden = true)
	private Timestamp createTime;

	@ApiModelProperty(value = "创建IP", hidden = true)
	private String createIp;

	@ApiModelProperty(value = "更新人ID", hidden = true)
	private Long updateBy;

	@ApiModelProperty(value = "更新IP", hidden = true)
	private String updateIp;

	@ApiModelProperty(value = "更新时间", hidden = true)
	private Timestamp updateTime;

	@ApiModelProperty(value = "删除标志", hidden = true)
	private String deleteFlag;

	@ApiModelProperty(value = "版本号", hidden = true)
	private Integer versions;
}
