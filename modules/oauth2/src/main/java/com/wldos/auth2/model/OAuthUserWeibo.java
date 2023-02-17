/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
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
 * OAuth登录微博用户信息。
 *
 * @author 树悉猿
 * @date 2023/2/15
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthUserWeibo implements OAuthUser{
	@JsonProperty("id")
	private String openId;
	@JsonProperty("screen_name")
	private String nickname;
	private String gender;
	private String province;
	private String city;
	private String location;
	private String description;
	private String url;
	@JsonProperty("profile_image_url")
	private String headImgUrl;
	@JsonProperty("idstr")
	private String unionId;
	@JsonIgnore
	private String oauthType;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickname() {
		return nickname;
	}

	@Override
	public String getSex() {
		return this.gender;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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