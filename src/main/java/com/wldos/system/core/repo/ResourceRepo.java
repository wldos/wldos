/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.List;

import com.wldos.system.core.entity.WoResource;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 资源仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface ResourceRepo extends PagingAndSortingRepository<WoResource, Long>, ResourceJdbc {
	@Query("select r.* from wo_resource r where r.delete_flag='normal' and r.is_valid='1' and r.resource_type = :resourceType")
	List<WoResource> queryByResourceType(String resourceType);
}
