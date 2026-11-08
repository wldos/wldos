/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoUser;
import io.github.wldos.platform.support.auth.vo.UserInfo;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 用户仓库操作类。
 *
 * @author Yuanxi Universe
 * @date 2021/4/27
 * @version 1.0
 */
public interface UserDao extends BaseDao<WoUser, Long>, UserJdbc {

	/**
	 * 租户范围内登录名是否已存在（含已删除用户）。
	 */
	@Query("SELECT COUNT(1) FROM wo_user u WHERE u.main_com_id IN (:comIds) AND u.login_name = :loginName")
	boolean existsByComsAndLoginName(@Param("comIds") Collection<Long> comIds, @Param("loginName") String loginName);

	/**
	 * 租户范围内邮箱是否已被未删除用户占用。
	 */
	@Query("SELECT COUNT(1) FROM wo_user u WHERE u.main_com_id IN (:comIds) AND u.email = :email AND u.delete_flag = 'normal'")
	boolean existsNormalByComsAndEmail(@Param("comIds") Collection<Long> comIds, @Param("email") String email);

	/**
	 * 租户范围内按邮箱查未删除用户。
	 */
	@Query("SELECT * FROM wo_user u WHERE u.email = :email AND u.delete_flag = 'normal' AND u.main_com_id IN (:comIds) ORDER BY u.id")
	List<WoUser> listNormalByEmailInComs(@Param("email") String email, @Param("comIds") Collection<Long> comIds);

	/**
	 * 租户范围内按登录名查询可登录用户（正常或待激活）。
	 *
	 * @param username 登陆名
	 * @param comIds 租户范围
	 * @return 用户信息
	 */
	@Query("SELECT u.* FROM wo_user u WHERE u.delete_flag='normal' AND (u.status = 'normal' OR u.status = 'notActive') AND u.login_name=:username AND u.main_com_id IN (:comIds) ORDER BY u.id LIMIT 1")
	WoUser findByLoginNameInComs(@Param("username") String username, @Param("comIds") Collection<Long> comIds);

	@Query("select o.* from wo_org o where o.is_valid='1' and o.delete_flag='normal' and o.org_code=(select t.option_value from wo_options t where t.option_key=:optionName )")
	@Deprecated
	WoOrg queryOrgByDefaultRole(String optionName);

	/**
	 * 批量查询一批用户信息
	 *
	 * @param userIds 用户ids
	 * @return 批量用户信息
	 */
	@Query("SELECT u.id, u.username, u.nickname, u.remark, u.avatar FROM wo_user u WHERE u.delete_flag='normal' AND (u.status = 'normal' OR u.status = 'notActive') AND u.id in (:userIds)")
	List<UserInfo> queryUsersInfo(List<Long> userIds);

	@Query("SELECT * FROM wo_user WHERE id IN (:userIds) AND delete_flag = 'normal'")
	List<WoUser> findByIdsActive(@Param("userIds") Collection<Long> userIds);

	@Query("SELECT COUNT(1) FROM wo_user u WHERE u.domain_id = :domainId AND u.delete_flag = 'normal'")
	long countNormalUsersByDomainId(@Param("domainId") Long domainId);

	/**
	 * 按推荐码查询用户（用于推荐码校验，仅正常状态用户）
	 *
	 * @param recommendCode 推荐码
	 * @return 用户信息，不存在或无效则 empty
	 */
	@Query("SELECT * FROM wo_user u WHERE u.delete_flag='normal' AND (u.status = 'normal' OR u.status = 'notActive') AND u.recommend_code = :recommendCode")
	Optional<WoUser> findByRecommendCode(String recommendCode);

	/**
	 * 用户在主体认证链路上解析到的角色编码（wo_role.role_code），用于门户等按角色裁剪能力。
	 */
	@Query("SELECT DISTINCT r.role_code FROM wo_role r "
			+ "INNER JOIN wo_subject_association sa ON sa.role_id = r.id AND sa.delete_flag = 'normal' AND sa.is_valid = '1' "
			+ "INNER JOIN wo_subject_authentication a ON a.subject_type_id = sa.subject_type_id AND a.delete_flag = 'normal' "
			+ "AND a.is_valid = '1' AND a.user_id = :userId "
			+ "WHERE r.delete_flag = 'normal' AND r.is_valid = '1'")
	List<String> findSubjectRoleCodesByUserId(@Param("userId") Long userId);
}
