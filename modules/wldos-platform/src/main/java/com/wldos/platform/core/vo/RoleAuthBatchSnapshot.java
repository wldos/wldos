/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 批量推送前各目标站角色实例的默认授权快照（缓存序列化用）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Getter
@Setter
public class RoleAuthBatchSnapshot {

	private String snapshotId;

	private Long operatorUserId;

	private Long tenantId;

	private List<RoleAuthBatchSnapshotEntry> entries = new ArrayList<>();
}
