/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 组织机构(公司/租户/社会主体)类型枚举。根据国标GBT20091-2006组织机构类型定义组织类型的社会主体。组织机构类型用于在身份认证及商业场景中确定业务规则。
 * 公司、体系结构、组织机构、角色、用户的关系：公司(子公司、体系结构(子体系结构、组织机构(子组织、角色(子角色、用户))))
 *
 * @author 树悉猿
 * @date 2021/11/29
 * @version 1.0
 */
public enum ComTypeEnum {

	/**    企业 */
	ENTERPRISE("企业", "1"),
	/** 公司 */
	COMPANY("公司", "11"),
	/** 非公司制企业法人：是指经过登记取得法人资格但不称公司也不按公司法组建的经济组织。非公司制企业法人的登记管理适用企业
	 * 法人登记管理条例及其施行细则。
	 * 按出资方式分，有拨款、投资、联营等。
	 * 按所有者划分，有国有企业、集体所有制企业、联营企业等。
	 * */
	NON_COMPANY("非公司制企业法人", "13"),
	/** 企业分支机构 */
	BRANCH_OFFICE("企业分支机构", "15"),
	/** 个人独资企业、合伙企业 */
	PARTNERSHIP("个独、合伙企业", "17"),
	/** 其他企业 */
	OTHER_ENTER("其他企业", "19"),
	/** 机关 */
	OFFICE("机关", "3"),
	/** 党 */
	PARTY("中国共产党", "31");

	private final String label;

	private final String value;

	ComTypeEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{label: '" + this.label + "', value: '" + this.value + "'}";
	}
}
