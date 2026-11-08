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
 * 单站角色实例推送前的授权资源 id 列表。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Getter
@Setter
public class RoleAuthBatchSnapshotEntry {

	private Long domainId;

	private Long roleId;

	private List<Long> resourceIds = new ArrayList<>();
}
