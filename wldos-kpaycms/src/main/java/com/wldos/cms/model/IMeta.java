

package com.wldos.cms.model;

import java.util.List;

import com.wldos.cms.vo.ContentExt;
import com.wldos.cms.vo.SeoCrumbs;
import com.wldos.cms.vo.Term;


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
}
