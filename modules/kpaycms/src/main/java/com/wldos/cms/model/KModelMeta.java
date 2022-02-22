/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * 知识元模型定义。
 *
 * @author 树悉猿
 * @date 2021/6/19
 * @version 1.0
 */
public class KModelMeta extends CMeta {

	/** 副标题 */
	protected String subTitle = "未设置";

	/** 原价 */
	protected BigDecimal ornPrice = BigDecimal.valueOf(0);

	/** 现价 */
	protected BigDecimal pstPrice = BigDecimal.valueOf(0);

	/** 联系人 */
	protected String contact = "未设置";

	/** 联系电话 */
	protected String telephone = "未设置";

	/** 所在省 */
	protected String prov = "未知";

	/** 所在市 */
	protected String city = "未知";

	/** 所在区县 */
	protected String county = "未知";

	/** 主图 */
	protected List<MainPicture> mainPic;

	/** 附件图片等 */
	protected List<Attachment> attachment;

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public BigDecimal getOrnPrice() {
		return ornPrice;
	}

	public void setOrnPrice(BigDecimal ornPrice) {
		this.ornPrice = ornPrice;
	}

	public BigDecimal getPstPrice() {
		return pstPrice;
	}

	public void setPstPrice(BigDecimal pstPrice) {
		this.pstPrice = pstPrice;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getProv() {
		return prov;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public List<MainPicture> getMainPic() {
		return mainPic;
	}

	public void setMainPic(List<MainPicture> mainPic) {
		this.mainPic = mainPic;
	}

	public List<Attachment> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<Attachment> attachment) {
		this.attachment = attachment;
	}
}
