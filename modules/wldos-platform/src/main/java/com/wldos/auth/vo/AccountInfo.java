/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.auth.vo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.wldos.auth.model.AccBind;
import com.wldos.auth.model.AccMes;
import com.wldos.auth.model.AccSecurity;

/**
 * 用户账户信息。
 *
 * @author 树悉猿
 * @date 2021/4/30
 * @version 1.0
 */
public class AccountInfo {
	private long id;

	private String username;

	private String nickname;

	/** 个人简介、个性签名 */
	private String remark;

	private String avatar;

	private String status;

	private Long domainId;

	private String idCard;

	private String sex;

	private Timestamp birthday;

	private String mobile;

	private String telephone;

	private String address;

	private String qq;

	private String email;

	private String isReal;

	private String country;

	private String province;

	private String city;

	private String area;

	/** 邀请码 */
	private String inviteCode;

	/** 推荐码 */
	private String recommendCode;

	/** 安全设置 */
	private AccSecurity sec;

	/** 账号绑定 */
	private AccBind bind;

	/** 消息通知设置 */
	private AccMes accMes;

	/** 头衔 */
	private String title;

	/** 组织，主企业，根据用户设置获取，@todo 后期统一关联公司表 */
	private String company;

	/** 标签 */
	private List<Map<String, String>> tags;

	/** 群组，根据用户所属系统会员组获取，@todo 后期改成群组/团队/圈子 */
	private List<Group> group;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String headImg) {
		this.avatar = headImg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Timestamp getBirthday() {
		return birthday;
	}

	public void setBirthday(Timestamp birthday) {
		this.birthday = birthday;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIsReal() {
		return isReal;
	}

	public void setIsReal(String isReal) {
		this.isReal = isReal;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public String getRecommendCode() {
		return recommendCode;
	}

	public void setRecommendCode(String recommendCode) {
		this.recommendCode = recommendCode;
	}

	public AccSecurity getSec() {
		return sec;
	}

	public void setSec(AccSecurity sec) {
		this.sec = sec;
	}

	public AccBind getBind() {
		return bind;
	}

	public void setBind(AccBind bind) {
		this.bind = bind;
	}

	public AccMes getAccMes() {
		return accMes;
	}

	public void setAccMes(AccMes accMes) {
		this.accMes = accMes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public List<Map<String, String>> getTags() {
		return tags;
	}

	public void setTags(List<Map<String, String>> tags) {
		this.tags = tags;
	}

	public List<Group> getGroup() {
		return group;
	}

	public void setGroup(List<Group> group) {
		this.group = group;
	}
}
