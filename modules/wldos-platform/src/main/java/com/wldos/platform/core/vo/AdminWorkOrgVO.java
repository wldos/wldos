package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("管理工作组织")
@Getter
@Setter
public class AdminWorkOrgVO {

	@ApiModelProperty("组织 id")
	private Long orgId;

	@ApiModelProperty("组织名称")
	private String orgName;

	@ApiModelProperty("体系 id")
	private Long archId;

	@ApiModelProperty("体系名称")
	private String archName;

	@ApiModelProperty("是否为当前工作组织")
	private Boolean current;
}
