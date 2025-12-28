/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.model;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 在CMS-Pub基础上的知识元模型定义。知识内容模型包括两部分：数据和行为。数据定义了结构，行为定义了流程、逻辑和展现，本系统以相对固定的行为和数据模板实现一种模式，
 * 基于数据和行为的扩展同样遵循这个模式。
 *
 * @author 元悉宇宙
 * @date 2021/6/19
 * @version 1.0
 */
@ApiModel(description = "知识元模型定义，在CMS-Pub基础上的扩展")
@Getter
@Setter
public class KModelMeta extends CMeta {

	@ApiModelProperty(value = "副标题", example = "示例副标题")
	protected String subTitle = "未设置";

	@ApiModelProperty(value = "原价", example = "100.00")
	protected BigDecimal ornPrice = BigDecimal.valueOf(0);

	@ApiModelProperty(value = "现价", example = "80.00")
	protected BigDecimal pstPrice = BigDecimal.valueOf(0);

	@ApiModelProperty(value = "联系人", example = "张三")
	protected String contact = "未设置";

	@ApiModelProperty(value = "联系电话", example = "13800138000")
	protected String telephone = "未设置";

	@ApiModelProperty(value = "所在省", example = "北京市")
	protected String prov = "未知";

	@ApiModelProperty(value = "所在市", example = "北京市")
	protected String city = "未知";

	@ApiModelProperty(value = "所在区县", example = "朝阳区")
	protected String county = "未知";

	@ApiModelProperty(value = "主图列表")
	protected List<MainPicture> mainPic;

	@ApiModelProperty(value = "附件列表（图片、文件、音频、视频等）")
	protected List<Attachment> attachment;
}
