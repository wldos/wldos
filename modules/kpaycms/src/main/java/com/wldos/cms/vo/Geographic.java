/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

/**
 * 地理信息。
 *
 * @author 树悉猿
 * @date 2021/12/12
 * @version 1.0
 */
public class Geographic {
	private Province province;

	private City city;

	public Geographic() {
	}

	public static Geographic of(Province province, City city) {
		return new Geographic(province, city);
	}

	public static Geographic of(com.wldos.sys.core.vo.City region) {
		return new Geographic(region);
	}

	private Geographic(Province province, City city) {
		this.province = province;
		this.city = city;
	}

	private Geographic(com.wldos.sys.core.vo.City region) {
		this.province = Province.of(region.getParentId().toString(), region.getProvName());
		this.city = City.of(region.getId().toString(), region.getName());
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
}

class Province {
	String label;

	String key = "0";

	public Province() {}

	public static Province of(String key, String label) {
		return new Province(key, label);
	}

	private Province(String key, String label) {
		this.key = key;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}

class City {
	String label;

	String key = "0";

	public City() {}

	public static City of(String key, String label) {
		return new City(key, label);
	}

	private City(String key, String label) {
		this.key = key;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
