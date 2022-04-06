/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.model;

import java.math.BigDecimal;
import java.util.List;

import com.wldos.cms.vo.SeoCrumbs;
import com.wldos.sys.base.dto.ContentExt;
import com.wldos.sys.base.dto.Term;

/**
 * 统一操作接口。
 *
 * @author 树悉猿
 * @date 2021/8/20
 * @version 1.0
 */
public interface IMeta {
	void setContentExt(List<ContentExt> contentExt);

	void setPostContent(String content);

	String getPostContent();

	String getContentType();

	String getPostTitle();

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
