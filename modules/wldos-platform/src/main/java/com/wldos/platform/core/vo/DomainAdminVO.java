/**
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license. 
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 站点域管理员绑定视图。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("域管理员")
@Getter
@Setter
public class DomainAdminVO {

	@ApiModelProperty("绑定记录 id")
	private Long id;

	@ApiModelProperty("站点 id")
	private Long domainId;

	@ApiModelProperty("用户 id")
	private Long userId;

	@ApiModelProperty("昵称")
	private String nickname;

	@ApiModelProperty("登录名")
	private String loginName;

	@ApiModelProperty("邮箱")
	private String email;

	@ApiModelProperty("用户状态")
	private String status;

	@ApiModelProperty("简介")
	private String remark;
}
