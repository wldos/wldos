/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import io.github.wldos.platform.support.rbac.RbacRowScope;

/**
 * 应用定义（TENANT_SCOPED）：租户级共享，站点只读挂载（{@code wo_domain_app}），不可管理定义本体。
 */
@Table
@Getter
@Setter
@RbacRowScope(comId = true)
public class WoApp {
	@Id
	private Long id;

	private String appName;

    // 可见范围/管理边界（AppScope），沿用 AppTypeEnum
    private String appType;

	private String appCode;

	private String appSecret;

    private String appDesc;
    // 应用来源（AppOriginEnum）：internal/plugin/external
    private String appOrigin;

	private Long comId;

	private String isValid;

	private Long createBy;

	private java.sql.Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private java.sql.Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	/** 本站 wo_domain_app.is_valid，仅域应用列表联查填充 */
	@Transient
	private String mountValid;

	/** 域应用选择列表：是否已挂载到当前站点 */
	@Transient
	private Boolean selected;

	@Version
	private Integer versions;
}
