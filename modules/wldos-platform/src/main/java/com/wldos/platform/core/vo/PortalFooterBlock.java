package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 门户页脚块
 * 
 * @author Yuanxi Universe (306991142@qq.com)
 * @since 3.0.1
 * @version 1.0.0
 */
@ApiModel(description = "门户页脚块")
@Getter
@Setter
public class PortalFooterBlock {
	@ApiModelProperty(value = "页脚主体 HTML")
	private String foot;

	@ApiModelProperty(value = "友情链接 HTML")
	private String flink;

	@ApiModelProperty(value = "版权 HTML")
	private String copy;
}
