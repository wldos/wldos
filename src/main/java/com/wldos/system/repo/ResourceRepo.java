/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import java.util.List;

import com.wldos.system.entity.WoResource;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 资源仓库操作类。
 *
 * @Title ResourceRepo
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public interface ResourceRepo extends PagingAndSortingRepository<WoResource, Long>, ResourceJdbc {
	@Query("select r.* from wo_resource r where r.delete_flag='normal' and r.is_valid='1' and r.resource_type = :resourceType")
	List<WoResource> queryByResourceType(String resourceType);
}
