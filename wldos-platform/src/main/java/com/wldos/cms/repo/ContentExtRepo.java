/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.entity.KModelContentExt;
import com.wldos.cms.vo.ContentExt;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 内容自定义扩展属性（不含系统模型公共属性）repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface ContentExtRepo extends PagingAndSortingRepository<KModelContentExt, Long>{
	@Query("select e.* from k_model_content_ext e where e.content_id=:contentId")
	List<ContentExt> queryExtPropsByContentId(Long contentId);
}
