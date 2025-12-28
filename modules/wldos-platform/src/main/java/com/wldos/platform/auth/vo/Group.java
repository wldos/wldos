/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 群组/团队/圈子。
 *
 * @author 元悉宇宙
 * @date 2022/5/18
 * @version 1.0
 */
@ApiModel(description = "群组/团队/圈子")
public class Group {
	@ApiModelProperty(value = "群组ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "组织名称", example = "技术部")
	private String orgName;

	@ApiModelProperty(value = "组织Logo", example = "/logo/org.jpg")
	private String orgLogo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgLogo() {
		return orgLogo;
	}

	public void setOrgLogo(String orgLogo) {
		this.orgLogo = orgLogo;
	}
}
