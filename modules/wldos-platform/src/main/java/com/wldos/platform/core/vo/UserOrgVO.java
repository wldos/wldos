/**
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license. 
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import com.wldos.platform.core.entity.WoUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户管理列表视图：在 {@link WoUser} 基础上附带所属公司/组织，供用户管理列表展示与按组织筛选使用。
 * 公司名来自 {@code wo_user.main_com_id} 关联 {@code wo_company}；{@code main_com_id=0} 为平台轴，无公司行时展示「平台」。
 * 站点名来自 {@code wo_user.domain_id} 关联 {@code wo_domain}。组织取 {@code wo_org_user} 最早一条有效记录。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("用户（含所属公司/组织）")
@Getter
@Setter
public class UserOrgVO extends WoUser {

	@ApiModelProperty("主组织 id")
	private Long orgId;

	@ApiModelProperty("组织名称")
	private String orgName;

	@ApiModelProperty("全部归属组织名称（顿号分隔，多组织时列表展示用）")
	private String orgNames;

	@ApiModelProperty("主公司名称")
	private String comName;

	@ApiModelProperty("主公司 id")
	private Long comId;

	@ApiModelProperty("归属站点名称（列表展示）")
	private String domainName;

	@ApiModelProperty("当前操作者可写（目标身份层级低于操作者或为本人）")
	private Boolean writable;
}
