/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.domain.vo;

import com.wldos.common.vo.TreeNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainResource extends TreeNode<DomainResource> {
	private Long id;

	private Long resourceId;

	private String moduleName;

	private String resourceCode;

	private String resourceName;

	private String resourcePath;

	private String icon;

	private String resourceType;

	private String requestMethod;

	private String target;

	private Long appId;

	private Long parentId;

	private String isValid;

	private String remark;

	private Long displayOrder;

	private Long termTypeId;

	private String url;

	/** 分类目录名称 */
	private String termName;

	private Long domainId;

	private String deleteFlag;
}
