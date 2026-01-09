/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.wldos.platform.auth.model.AccBind;
import com.wldos.platform.auth.model.AccMes;
import com.wldos.platform.auth.model.AccSecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户账户信息。
 *
 * @author 元悉宇宙
 * @date 2021/4/30
 * @version 1.0
 */
@ApiModel(description = "用户账户信息")
@Getter
@Setter
public class AccountInfo {
	@ApiModelProperty(value = "用户ID", example = "1")
	private long id;

	@ApiModelProperty(value = "用户名", example = "admin")
	private String username;

	@ApiModelProperty(value = "昵称", example = "管理员")
	private String nickname;

	@ApiModelProperty(value = "个人简介、个性签名", example = "这是我的个人简介")
	private String remark;

	@ApiModelProperty(value = "头像URL", example = "/avatar/user.jpg")
	private String avatar;

	@ApiModelProperty(value = "用户状态", example = "active")
	private String status;

	@ApiModelProperty(value = "域名ID", example = "1")
	private Long domainId;

	@ApiModelProperty(value = "身份证号", example = "110101199001011234")
	private String idCard;

	@ApiModelProperty(value = "性别", example = "男")
	private String sex;

	@ApiModelProperty(value = "生日", example = "1990-01-01")
	private Timestamp birthday;

	@ApiModelProperty(value = "手机号", example = "13800138000")
	private String mobile;

	@ApiModelProperty(value = "电话", example = "010-12345678")
	private String telephone;

	@ApiModelProperty(value = "地址", example = "北京市朝阳区")
	private String address;

	@ApiModelProperty(value = "QQ号", example = "123456789")
	private String qq;

	@ApiModelProperty(value = "邮箱", example = "user@example.com")
	private String email;

	@ApiModelProperty(value = "是否实名：1=是, 0=否", example = "1")
	private String isReal;

	@ApiModelProperty(value = "国家", example = "中国")
	private String country;

	@ApiModelProperty(value = "省份", example = "北京市")
	private String province;

	@ApiModelProperty(value = "城市", example = "北京市")
	private String city;

	@ApiModelProperty(value = "区县", example = "朝阳区")
	private String area;

	@ApiModelProperty(value = "邀请码", example = "INV001")
	private String inviteCode;

	@ApiModelProperty(value = "推荐码", example = "REF001")
	private String recommendCode;

	@ApiModelProperty(value = "安全设置")
	private AccSecurity sec;

	@ApiModelProperty(value = "账号绑定")
	private AccBind bind;

	@ApiModelProperty(value = "消息通知设置")
	private AccMes accMes;

	@ApiModelProperty(value = "头衔", example = "高级工程师")
	private String title;

	@ApiModelProperty(value = "组织，主企业，根据用户设置获取", example = "示例公司")
	private String company;

	@ApiModelProperty(value = "标签列表")
	private List<Map<String, String>> tags;

	@ApiModelProperty(value = "群组，根据用户所属系统会员组获取")
	private List<Group> group;
}
