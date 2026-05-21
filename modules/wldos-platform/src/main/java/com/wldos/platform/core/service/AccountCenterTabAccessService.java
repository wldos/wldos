/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 */
package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.wldos.platform.core.dao.UserDao;
import com.wldos.platform.core.vo.AccountCenterTabDef;
import io.github.wldos.common.utils.ObjectUtils;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 门户个人中心页签可见性（仅登录会员，非游客）：以角色、路由/资源鉴权、按钮授权码、主体关联角色及「是否具备管理端菜单」等为准，在服务端裁剪 {@link AccountCenterTabDef} 列表。
 *
 * <p>区分用户端与管理端对页签是否可见并非主逻辑：含双端部署时管理用户通常同时具备两侧资源，公网非管理用户则不具备管理相关权限。
 * 「端」仅在产品上要按不同端页面装配不同 Tab 集合时有意义；本服务不按请求侧分支，统一按权限裁剪。</p>
 *
 * <p>游客仅有统一 token，无账号，不提供个人中心；调用方应对 {@code userId == null} 或游客先返回空列表。</p>
 *
 * <p>配置扩展字段见 {@link AccountCenterTabDef}；未配置时使用按 {@code type} 的默认策略。</p>
 */
@Service
public class AccountCenterTabAccessService {

	/** 与 wo_resource 中管理菜单接口路径一致，用于「我的权限」类页签的默认可访问性校验 */
	public static final String ADMIN_MENU_ROUTE_PATH = "/admin/sys/user/adminMenu";

	private static final Set<String> CMS_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("info", "book", "applications", "projects")));

	private final UserService userService;

	private final AuthService authService;

	private final UserDao userDao;

	public AccountCenterTabAccessService(UserService userService, AuthService authService, UserDao userDao) {
		this.userService = userService;
		this.authService = authService;
		this.userDao = userDao;
	}

	/**
	 * @param userId   登录会员用户 id；未登录或游客应由 {@link com.wldos.platform.core.controller.UserController#accountCenterTabDefs()} 等先行拦截
	 * @param domainId 当前域
	 * @param comId    租户/主企业 id
	 */
	public List<AccountCenterTabDef> filterVisibleForCurrentUser(Long userId, Long domainId, Long comId, HttpServletRequest request,
			List<AccountCenterTabDef> defs) {
		if (defs == null || defs.isEmpty()) {
			return defs;
		}
		if (userId == null || authService.isGuest(userId)) {
			return Collections.emptyList();
		}
		if (userService.isAdmin(userId)) {
			return new ArrayList<>(defs);
		}

		boolean manageCapable = !ObjectUtils.isBlank(userService.queryAdminMenuByUser(domainId, comId, userId));
		List<String> authorities = authService.queryAuthorityButton(domainId, comId, userId);
		Set<String> authSet = new HashSet<>(authorities);
		List<String> subjectRoles = emptyIfNull(userDao.findSubjectRoleCodesByUserId(userId));
		Set<String> roleSet = new HashSet<>(subjectRoles);

		List<AccountCenterTabDef> out = new ArrayList<>(defs.size());
		for (AccountCenterTabDef def : defs) {
			if (isTabVisible(def, manageCapable, authSet, roleSet, userId, domainId, comId, request)) {
				out.add(def);
			}
		}
		if (out.isEmpty() && !defs.isEmpty()) {
			for (AccountCenterTabDef def : defs) {
				if (CMS_TYPES.contains(def.getType())
						&& isTabVisible(def, manageCapable, authSet, roleSet, userId, domainId, comId, request)) {
					out.add(def);
				}
			}
		}
		return out;
	}

	private boolean isTabVisible(AccountCenterTabDef def, boolean manageCapable, Set<String> authSet,
			Set<String> roleSet, Long userId, Long domainId, Long comId, HttpServletRequest request) {
		String type = def.getType();

		Boolean requireManage = def.getRequireManageSide();
		boolean needManageSide = requireManage != null ? requireManage : defaultRequireManageSide(type);
		if (needManageSide && !manageCapable) {
			return false;
		}

		if ("permission".equals(type) && manageCapable) {
			String code = authService.authorityRouteCheck(ADMIN_MENU_ROUTE_PATH, userId, domainId, comId, request);
			if (!"200".equals(code)) {
				return false;
			}
		}

		if (!CollectionUtils.isEmpty(def.getResourcePaths())) {
			boolean any = false;
			for (String p : def.getResourcePaths()) {
				if (p == null || p.trim().isEmpty()) {
					continue;
				}
				if ("200".equals(authService.authorityRouteCheck(p.trim(), userId, domainId, comId, request))) {
					any = true;
					break;
				}
			}
			if (!any) {
				return false;
			}
		}

		if (!CollectionUtils.isEmpty(def.getAuthorityNeedAny())) {
			boolean hit = false;
			for (String a : def.getAuthorityNeedAny()) {
				if (a != null && authSet.contains(a.trim())) {
					hit = true;
					break;
				}
			}
			if (!hit) {
				return false;
			}
		}

		if (!CollectionUtils.isEmpty(def.getRoleNeedAny())) {
			boolean hit = false;
			for (String r : def.getRoleNeedAny()) {
				if (r != null && roleSet.contains(r.trim())) {
					hit = true;
					break;
				}
			}
			if (!hit) {
				return false;
			}
		}

		return true;
	}

	private static boolean defaultRequireManageSide(String type) {
		return "permission".equals(type);
	}

	private static List<String> emptyIfNull(List<String> list) {
		return list == null ? Collections.emptyList() : list;
	}
}
