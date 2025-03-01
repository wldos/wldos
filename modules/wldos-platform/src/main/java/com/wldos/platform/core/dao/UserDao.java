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
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.support.auth.vo.UserInfo;

import org.springframework.data.jdbc.repository.query.Query;

/**
 * 用户仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface UserDao extends BaseDao<WoUser, Long>, UserJdbc {

	boolean existsByLoginName(String loginName);

	boolean existsByEmail(String email);

	@Query("select count(1) from wo_user u where u.email=:email and u.delete_flag=:deleteFlag")
	boolean existsByEmailAndDeleteFlag(String email, String deleteFlag);

	boolean existsByEmailAndDeleteFlagAndStatus(String email, String deleteFlag, String status);

	boolean existsByEmailAndLoginNameAndDeleteFlag(String email, String loginName, String deleteFlag);

	/**
	 * 按用户登录名查询用户
	 *
	 * @param username 登陆名
	 * @return 用户信息
	 */
	@Query("SELECT u.* FROM wo_user u WHERE u.delete_flag='normal' AND (u.status = 'normal' OR u.status = 'notActive') AND u.login_name=:username")
	WoUser findByLoginName(String username);

	@Query("select o.* from wo_org o where o.is_valid='1' and o.delete_flag='normal' and o.org_code=(select t.option_value from wo_options t where t.option_key=:optionName )")
	WoOrg queryOrgByDefaultRole(String optionName);

	/**
	 * 批量查询一批用户信息
	 *
	 * @param userIds 用户ids
	 * @return 批量用户信息
	 */
	@Query("SELECT u.id, u.username, u.nickname, u.remark, u.avatar FROM wo_user u WHERE u.delete_flag='normal' AND (u.status = 'normal' OR u.status = 'notActive') AND u.id in (:userIds)")
	List<UserInfo> queryUsersInfo(List<Long> userIds);
}
