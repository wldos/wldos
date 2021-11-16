

package com.wldos.cms.model;

import java.math.BigDecimal;
import java.util.List;

import com.wldos.cms.vo.ContentExt;


public class KModelMeta extends CMeta{

	
	protected String subTitle = "未设置";

	
	protected BigDecimal ornPrice = BigDecimal.valueOf(0);

	
	protected BigDecimal pstPrice = BigDecimal.valueOf(0);

	
	protected String contact = "未设置";

	
	protected String telephone = "未设置";

	
	protected String prov = "未知";

	
	protected String city = "未知";

	
	protected String county = "未知";

	
	protected List<MainPicture> mainPic;

	
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
