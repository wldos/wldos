/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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
 * 用户
 */
@Table
@Getter
@Setter
public class WoUser {
	@Id
	private Long id;

	private String loginName;

	private String nickname;

	private String passwd;

	private String status;

	private Long domainId;

	private String username;

	private String idCard;

	private String sex;

	private Timestamp birthday;

	private String mobile;

	private String telephone;

	private String title;

	private String company;

	private String address;

	private String qq;

	private String email;

	private String avatar;

	private String remark;

	private Long displayOrder;

	private String isReal;

	private String country;

	private String province;

	private String city;

	private String area;

	private String inviteCode;

	private String recommendCode;

	private String registerIp;

	private Long createBy;

	private Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	/**
	 * 乐观锁
	 */
	@Version
	private Integer versions;
}