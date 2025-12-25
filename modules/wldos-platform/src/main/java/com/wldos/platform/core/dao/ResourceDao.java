/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.support.resource.entity.WoResource;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
@Transactional(readOnly = true) // 接口层面开启只读事务，防止死锁，写方法直接用@Transactional注解覆盖只读属性
public interface ResourceDao extends BaseDao<WoResource, Long>, ResourceJdbc {
	@Query("select r.* from wo_resource r where r.delete_flag='normal' and r.is_valid='1' and r.resource_type = :resourceType")
	List<WoResource> queryByResourceType(@Param("resourceType") String resourceType);

	@Query("select r.* from wo_resource r where r.delete_flag='normal' and r.is_valid='1' and r.resource_type in (:resTypes)")
	List<WoResource> queryByResTypes(@Param("resTypes") List<String> resTypes);

	@Query("select max(r.display_order) max_order from wo_resource r where r.delete_flag='normal' and r.is_valid='1' and r.parent_id=:pId")
	Long queryMaxOrder(@Param("pId") Long parentId);

	@Modifying
	@Query("delete from wo_resource where app_id=:appId")
	void deleteByAppId(@Param("appId") Long appId);
}
