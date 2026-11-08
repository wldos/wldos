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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色授权批量推送执行结果（含可回滚快照 id）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("角色授权批量推送结果")
@Getter
@Setter
public class RoleAuthBatchApplyResultVO {

	@ApiModelProperty(value = "推送前快照 id，可用于回滚（有效期内单次）")
	private String snapshotId;

	@ApiModelProperty(value = "快照覆盖的目标站点名称（与推送目标一致）")
	private List<String> snapshotDomainNames = new ArrayList<>();
}
