/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 守信人登记册：可信会员身份的权威判据（平台级），can_trust 身份组仅作权限容器。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Table("wo_trust_man")
@Getter
@Setter
public class WoTrustMan {
	@Id
	private Long id;

	private Long userId;

	private String isValid;

	private Long createBy;

	private String createIp;

	private Timestamp createTime;

	private Long updateBy;

	private String updateIp;

	private Timestamp updateTime;

	private String deleteFlag;

	@Version
	private Integer versions;
}
