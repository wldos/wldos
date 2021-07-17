/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.support.controller.EntityAssists;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.NameConvert;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.auth.JWT;
import com.wldos.system.auth.vo.AccountInfo;
import com.wldos.system.auth.vo.Menu;
import com.wldos.system.auth.vo.Register;
import com.wldos.system.auth.vo.Token;
import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.repo.OrgUserRepo;
import com.wldos.system.storage.IStore;
import com.wldos.system.enums.SysOptionEnum;
import com.wldos.system.enums.UserStatusEnum;
import com.wldos.system.auth.dto.Tenant;
import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.core.entity.WoOrgUser;
import com.wldos.system.core.entity.WoUser;
import com.wldos.system.core.repo.CompanyRepo;
import com.wldos.system.core.repo.UserRepo;
import com.wldos.system.vo.User;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户相关service。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService extends BaseService<UserRepo, WoUser, Long> {
	@Value("${wldos.user.avatar.default}")
	private String defaultAvatar;

	private final AuthService authService;

	private final OrgUserRepo orgUserRepo;

	private final CompanyRepo companyRepo;

	private final IStore store;

	public UserService(AuthService authService, OrgUserRepo orgUserRepo, CompanyRepo companyRepo, IStore store) {
		this.authService = authService;
		this.orgUserRepo = orgUserRepo;
		this.companyRepo = companyRepo;
		this.store = store;
	}

	public User queryUser(Long userId, String accessToken) {
		User user = new User();
		UserInfo userInfo = this.queryUserInfo(userId);
		user.setUserInfo(userInfo);
		List<Menu> menu = this.authService.queryMenuByUserId(userId);
		user.setMenu(menu);
		List<String> currentAuthority = this.authService.queryAuthorityButton(userId);
		user.setCurrentAuthority(currentAuthority);
		String refreshToken = this.refreshToken(accessToken);
		if (refreshToken != null) {
			Token token = new Token();
			token.setAccessToken(refreshToken);
			user.setToken(token);
		}
		return user;
	}

	public List<Menu> queryAdminMenuByUser(Long curUserId) {
		return this.authService.queryAdminMenuByUserId(curUserId);
	}

	/**
	 * 查询游客权限
	 *
	 * @return 游客也是用户
	 */
	public User queryGuest() {
		User user = new User();
		UserInfo userInfo = this.queryUserInfo(PubConstants.GUEST_ID);
		user.setUserInfo(userInfo);
		List<Menu> menu = this.authService.queryMenuByUserId(PubConstants.GUEST_ID);
		user.setMenu(menu);
		List<String> currentAuthority = this.authService.queryAuthorityButton(PubConstants.GUEST_ID);
		user.setCurrentAuthority(currentAuthority);

		return user;
	}

	public UserInfo queryUserInfo(Long userId) {
		UserInfo userInfo = new UserInfo();
		if (this.authService.isGuest(userId)) {
			return userInfo;
		}

		WoUser woUser = this.findById(userId);

		userInfo.setId(woUser.getId());
		userInfo.setUsername(woUser.getUserName());
		userInfo.setName(woUser.getAccountName());
		// 获取头像
		userInfo.setAvatar(this.getAvatarUrl(woUser.getAvatar()));
		userInfo.setRemark(woUser.getRemark());

		return userInfo;
	}

	/**
	 * 查询一批用户的信息
	 *
	 * @param userIds 用户ids
	 * @return 用户信息结果集
	 */
	public List<UserInfo> queryUsersInfo(List<Long> userIds) {

		List<UserInfo> userInfos = this.entityRepo.queryUsersInfo(userIds);

		return userInfos.parallelStream().map(item -> {
			item.setAvatar(this.getAvatarUrl(item.getAvatar()));
			return item;
		}).collect(Collectors.toList());
	}

	/**
	 * 账户全面信息
	 *
	 * @param userId 用户id
	 * @return 账户信息
	 */
	public AccountInfo queryAccountInfo(Long userId) {
		AccountInfo accInfo = new AccountInfo();
		if (this.authService.isGuest(userId)) {
			return accInfo;
		}

		WoUser woUser = this.findById(userId);

		accInfo.setId(woUser.getId());
		accInfo.setUsername(woUser.getUserName());
		accInfo.setName(woUser.getAccountName());
		// 获取头像
		accInfo.setAvatar(this.getAvatarUrl(woUser.getAvatar()));
		accInfo.setRemark(woUser.getRemark());
		BeanUtils.copyProperties(woUser, accInfo,
				"id", "userName", "accountName", "avatar", "remark");

		return accInfo;
	}

	/**
	 * 根据人员id查找所属主企业信息（租户）
	 *
	 * @param userId 用户id
	 * @return 租户信息
	 */
	public Tenant queryTenantInfoByTAdminId(Long userId) {
		try {
			return this.companyRepo.queryTenantInfoByTAdminId(userId);
		} catch (Exception e) {
			return null; // 当前用户还没有设置主企业
		}
	}

	/**
	 * 获取用户头像
	 *
	 * @param avatar 头像相对path
	 * @return 头像url
	 */
	public String getAvatarUrl(String avatar) {
		return this.store.getFileUrl(avatar, this.defaultAvatar);
	}

	public WoUser findByLoginName(String username) {
		return this.entityRepo.findByLoginName(username);
	}

	public WoUser createUser(Register register) {
		// 注册一个用户就是创建用户，然后挂到平台注册用户默认归属组(普通会员组)上，平台会员组属于系统基础设施已经默认设置好
		WoUser woUser = new WoUser();
		WoOrgUser woOrgUser = new WoOrgUser();
		BeanUtils.copyProperties(register, woUser);
		woUser.setStatus(UserStatusEnum.NORMAL.toString());
		long userId = register.getId();
		String userIp = register.getRegisterIp();
		woOrgUser.setUserId(userId);
		woOrgUser.setArchId(PubConstants.TOP_ARCH_ID);
		woOrgUser.setComId(PubConstants.TOP_COM_ID);
		woOrgUser.setIsValid("1");
		WoOrg org = this.entityRepo.queryOrgByDefaultRole(SysOptionEnum.DEFAULT_GROUP.toString());
		woOrgUser.setOrgId(org.getId());
		// 初始化实体公共参数
		EntityAssists.beforeInsert(woUser, userId, userId, userIp, true);
		EntityAssists.beforeInsert(woOrgUser, this.nextId(), userId, userIp, true);

		this.entityRepo.save(woUser);
		this.orgUserRepo.save(woOrgUser);

		return woUser;
	}

	public String refreshToken(String refreshToken) {
		boolean isCanRefresh = this.jwtTool.isCanRefresh(refreshToken);
		if (isCanRefresh) {
			JWT jwt = this.jwtTool.popJwt(refreshToken);
			String accessToken = this.jwtTool.genToken(jwt);

			String logJson = this.jwtTool.takTokenRec(jwt);
			// 更新token日志，绑定新的token id
			this.jwtTool.recToken(accessToken, logJson);

			return accessToken;
		}
		return null;
	}

	/**
	 * 查询用户分页，支持查询、过滤、排序
	 *
	 * @param woUser 用户
	 * @param pageQuery 分页参数
	 * @return 用户分页数据
	 */
	public PageableResult<WoUser> queryUserForPage(WoUser woUser, PageQuery pageQuery) {
		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder(50).append("select a.* from wo_user a where a.delete_flag='normal' ");
		if (!condition.isEmpty()) {
			StringBuilder finalSql1 = new StringBuilder();
			String comId = "comId";
			this.comArchOrgSql(condition, sql, comId, params);

			condition.forEach((key, value) -> {
				if (ObjectUtil.isBlank(value))
					return;
				try {
					Field field = ReflectionUtils.findRequiredField(woUser.getClass(), key);
					this.conditionSql(field, finalSql1, key, "a");
				}
				catch (IllegalArgumentException e) {
					Field field = ReflectionUtils.findRequiredField(WoOrgUser.class, key);
					this.conditionSql(field, finalSql1, key, "o");
				}
				params.add(value);
			});
			sql.append(finalSql1);
		}

		if (!ObjectUtil.isBlank(filter)) {
			StringBuffer finalSql = new StringBuffer();

			this.filterSql(filter, finalSql, params);
			sql.append(finalSql).delete(sql.length() - 1, sql.length());

			sql.append(")");
		}

		List<Map<String, Object>> all = this.commonJdbc.getNamedParamJdbcTemplate().getJdbcOperations()
				.queryForList("select count(1) as total from ( " + sql + " ) b", params.toArray());
		int total = all.isEmpty() ? 0 : Integer.parseInt(ObjectUtil.string(all.get(0).get("total")));

		if (!sort.isEmpty()) {
			StringBuffer temp = new StringBuffer(" order by ");
			sort.stream().iterator().forEachRemaining(s ->
					temp.append(NameConvert.humpToUnderLine(s.getProperty())).append(" ").append(s.isAscending() ? "" : " desc "));
			sql.append(temp);
		}

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<WoUser> list = (List<WoUser>) this.commonJdbc.execQueryForPage(woUser.getClass(), sql.toString(), currentPage, pageSize, params.toArray());

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	private void comArchOrgSql(Map<String, Object> condition, StringBuilder sql, String comId, List<Object> params) {
		if (condition.containsKey(comId)) {
			sql.append(" and exists(select 1 from wo_org_user o where o.user_id=a.id and o.delete_flag='normal' and o.is_valid='1' and o.com_id=? ");
			params.add(condition.get(comId));
			condition.remove(comId);

			String archId = "archId";
			if (condition.containsKey(archId)) {
				sql.append("and o.arch_id=? ");
				params.add(condition.get(archId));
				condition.remove(archId);
			}

			String orgId = "orgId";
			if (condition.containsKey(orgId)) {
				sql.append("and o.org_id=? ");
				params.add(condition.get(orgId));
				condition.remove(orgId);
			}
			sql.append(") ");
		}
	}

	private void conditionSql(Field field, StringBuilder sql, String key, String alias) {
		if (field.getType().equals(String.class)) {
			sql.append(" and instr(").append(alias).append(".").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
		}
		else if (field.getType().equals(Timestamp.class)) { // 日期默认匹配当天
			sql.append(" and DATE_FORMAT(").append(alias).append(".").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
		}
		else { // 包含所有的数字类型、日期类型。日期等的有界查询请移步前面自定义sql方法。
			sql.append(" and ").append(alias).append(".").append(NameConvert.humpToUnderLine(key)).append("= ? ");
		}
	}

	private void filterSql(Map<String, List<Object>> filter, StringBuffer sql, List<Object> params) {
		filter.forEach((key, value) -> {
			sql.append(" and a.").append(NameConvert.humpToUnderLine(key)).append(" in (");
			value.forEach(item -> {
				sql.append("?,");
				params.add(item);
			});
		});
	}
}
