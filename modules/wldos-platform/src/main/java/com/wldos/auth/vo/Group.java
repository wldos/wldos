/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.auth.vo;

/**
 * 群组/团队/圈子。
 *
 * @author 树悉猿
 * @date 2022/5/18
 * @version 1.0
 */
public class Group {
	// id
	private Long id;

	// orgName
	private String orgName;

	// logo
	private String orgLogo;
	// href: 固定模板直拼

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
