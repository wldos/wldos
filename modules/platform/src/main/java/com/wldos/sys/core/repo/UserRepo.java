/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.repo;


import java.util.List;

import com.wldos.sys.core.entity.WoOrg;
import com.wldos.sys.core.entity.WoUser;
import com.wldos.support.auth.vo.UserInfo;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 用户仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface UserRepo extends PagingAndSortingRepository<WoUser, Long>, UserJdbc {

	boolean existsByLoginName(String loginName);

	boolean existsByEmail(String email);

	@Query("SELECT u.* FROM wo_user u WHERE u.delete_flag='normal' AND u.status='normal' AND u.login_name=:username")
	WoUser findByLoginName(String username);

	@Query("select o.* from wo_org o where o.is_valid='1' and o.delete_flag='normal' and o.org_code=(select t.value from wo_options t where t.key=:optionName )")
	WoOrg queryOrgByDefaultRole(String optionName);

	@Query("SELECT u.id, u.username, u.nickname, u.remark, u.avatar FROM wo_user u WHERE u.delete_flag='normal' AND u.status='normal' AND u.id in (:userIds)")
	List<UserInfo> queryUsersInfo(List<Long> userIds);
}
