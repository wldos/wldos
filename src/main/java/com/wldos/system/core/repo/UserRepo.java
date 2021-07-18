/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;


import java.util.List;

import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.core.entity.WoUser;

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
	/**
	 * 按用户登录名查询用户
	 *
	 * @param username
	 * @return
	 */
	@Query("SELECT u.* FROM wo_user u WHERE u.delete_flag='normal' AND u.status='normal' AND u.login_name=:username")
	WoUser findByLoginName(String username);

	@Query("select o.* from wo_org o where o.is_valid='1' and o.delete_flag='normal' and o.org_code=(select t.value from wo_options t where t.key=:optionName )")
	WoOrg queryOrgByDefaultRole(String optionName);

	/**
	 * 批量查询一批用户信息
	 *
	 * @param userIds
	 * @return
	 */
	@Query("SELECT u.id, u.user_name, u.account_name name, u.remark, u.avatar FROM wo_user u WHERE u.delete_flag='normal' AND u.status='normal' AND u.id in (:userIds)")
	List<UserInfo> queryUsersInfo(List<Long> userIds);
}
