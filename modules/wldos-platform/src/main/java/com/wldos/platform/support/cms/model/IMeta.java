/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.model;

import java.math.BigDecimal;
import java.util.List;

import com.wldos.platform.support.term.dto.Term;
import com.wldos.platform.support.cms.dto.PubTypeExt;
import com.wldos.platform.support.cms.vo.SeoCrumbs;

/**
 * 统一操作接口。
 *
 * @author 元悉宇宙
 * @date 2021/8/20
 * @version 1.0
 */
public interface IMeta {
	void setPubTypeExt(List<PubTypeExt> pubTypeExt);

	void setPubContent(String content);

	String getPubType();

	String getPubContent();

	String getPubTitle();

	List<Term> getTags();

	void setSeoCrumbs(SeoCrumbs seoCrumbs);

	void setTermTypeIds(List<Long> termTypeIds);

	void setTags(List<Term> terms);

	void setCover(String s);

	void setSubTitle(String subTitle);

	void setOrnPrice(BigDecimal bigDecimal);

	void setContact(String hideName);

	void setTelephone(String hidePhone);

	void setRealNo(String telephone);

	void setCity(String name);

	void setProv(String provName);

	void setCounty(String county);

	void setViews(String views);
}
