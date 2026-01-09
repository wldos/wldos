/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.auth.vo.Group;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.vo.Org;

import org.springframework.data.jdbc.repository.query.Query;

/**
 * 组织结构仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface OrgDao extends BaseDao<WoOrg, Long> {
	@Query("select o.* from wo_org o where o.delete_flag=:deleteFlag and o.is_valid=:isValid and o.org_type=:orgType")
	List<Org> findAllByOrgType(String deleteFlag, String isValid, String orgType);

	WoOrg findByOrgCodeAndIsValidAndDeleteFlag(String orgCode, String isValid, String deleteFlag);

	@Query("select g.* from wo_org g join wo_org_user u on g.id=u.org_id and g.arch_id=u.arch_id and g.com_id=u.com_id where g.org_type='platform' and g.is_valid='1' and g.delete_flag='normal' and u.user_id=:userId and u.is_valid='1' and u.delete_flag='normal'")
	List<Group> findAllByUserId(Long userId);
}
