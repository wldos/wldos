/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OAuth登录用户信息。
 *
 * @author 树悉猿
 * @date 2022/10/21
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true) // 防止多余未知字段导致的转换异常
public class OAuthUser {
	/*
		{
		  "openid": "OPENID",
		  "nickname": "NICKNAME",
		  "sex": 1,
		  "province": "PROVINCE",
		  "city": "CITY",
		  "country": "COUNTRY",
		  "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
		  "privilege": ["PRIVILEGE1", "PRIVILEGE2"],
		  "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
		}
	 */
	@JsonProperty("openid")
	private String openId;
	private String nickname;
	private String sex;
	private String province;
	private String city;
	private String country;
	@JsonProperty("headimgurl")
	private String headImgUrl;
	private String[] privilege;
	private String unionId;
	@JsonIgnore
	private String oauthType; // 内部数据传递参数

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String[] getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String[] privilege) {
		this.privilege = privilege;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getOauthType() {
		return oauthType;
	}

	public void setOauthType(String oauthType) {
		this.oauthType = oauthType;
	}
}
