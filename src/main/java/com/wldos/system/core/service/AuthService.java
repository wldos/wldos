/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.support.cache.ICache;
import com.wldos.system.enums.UserRoleEnum;
import com.wldos.system.vo.AuthInfo;
import com.wldos.system.vo.AuthVerify;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.auth.vo.Menu;
import com.wldos.system.core.entity.WoResource;
import com.wldos.system.enums.RedisKeyEnum;
import com.wldos.system.enums.ResourceEnum;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 平台权限体系service。
 *
 * @author 树悉猿
 * @date 2021/4/26
 * @version 1.0
 */
@Slf4j
@Service
public class AuthService {
	private final ResourceService resourceService;

	private final ICache cache;

	@Autowired
	public AuthService(ICache cache, ResourceService resourceService) {
		this.cache = cache;
		this.resourceService = resourceService;
	}

	/**
	 * 验证用户对请求资源的权限，作为SaaS平台时，应该追加应用APP_ID作为索引验证请求path归属应用下的资源权限。
	 *
	 * @param userId 用户ID为应用程序创建分布式唯一，如果是数据库自动生成，请使用loginName
	 * @param reqUri 请求资源URI
	 * @param reqMethod 请求方法：GET,POST,PUT,DELETE
	 * @return AuthVerify
	 */
	public AuthVerify verifyReqAuth(String appCode, Long userId, String reqUri, String reqMethod) {
		AuthVerify authVerify = new AuthVerify();
		List<AuthInfo> allMenuAuthInfo = this.queryAllAuth(appCode);
		List<AuthInfo> matchAuthInfos = // 匹配请求URI的相关权限
				allMenuAuthInfo.parallelStream().filter(authInfo -> {
					String uri = authInfo.getResourcePath();
					if (uri.contains("{")) { // Redis cache json
						uri = uri.replaceAll("\\{\\*\\}", "[a-zA-Z\\\\d]+");
					}
					String exp = "^" + uri + "$";

					return (Pattern.compile(exp).matcher(reqUri).find() &&
							reqMethod.equals(authInfo.getRequestMethod()));
				}).collect(Collectors.toList());

		if (matchAuthInfos.isEmpty()) {
			authVerify.setAuth(true);
			return authVerify;
		}
		// 当前用户的资源权限
		List<AuthInfo> authInfos = this.isGuest(userId) ? this.resourceService.queryAuthInfoForGuest(appCode)
				: this.resourceService.queryAuthInfo(appCode, userId);

		AuthInfo matchReq = null;

		for (AuthInfo auth : authInfos) {

			boolean isMatch = matchAuthInfos.stream().anyMatch(authInfo -> authInfo.getResourceCode().equals(auth.getResourceCode()));
			if (isMatch) { // 用户的权限中存在该请求权限
				matchReq = auth;
				break;
			}
		}

		if (matchReq == null) { // 没有该权限
			authVerify.setAuth(false);
		}
		else {
			authVerify.setAuthInfo(matchReq);
			authVerify.setAuth(true);
		}

		return authVerify;
	}

	/**
	 * 游客专门处理，用于满足互联网应用需求
	 *
	 * @param userId
	 * @return
	 */
	public boolean isGuest(Long userId) {
		return PubConstants.GUEST_ID.equals(userId);
	}

	/**
	 * 查询所有资源权限
	 *
	 * @return
	 */
	public List<AuthInfo> queryAllAuth(String appCode) {
		// @todo 此处应设置所有资源的缓存取值逻辑（key存在取值，不存在查数据库并存储到缓存）
		String key = RedisKeyEnum.WLDOS_AUTH.toString() + ":" + appCode;
		String value = ObjectUtil.string(this.cache.get(key));

		if (ObjectUtil.isBlank(value)) {
			// 所有资源（菜单、按钮、服务和静态资源），各资源没必要分表，五十步与百步的关系
			Map<String, Object> params = new HashMap<>();
			params.put("appCode", appCode);
			List<AuthInfo> authInfos = this.resourceService.queryAuthInfo(appCode);
			value = new Gson().toJson(authInfos);

			this.cache.set(key, value, 12, TimeUnit.HOURS);
		}

		return JSON.parseArray(value, AuthInfo.class);
	}

	public List<Menu> queryMenuByUserId(Long id) {
		// @todo 二期优化：应该按照用户角色为key做缓存，根据用户id找到归属的用户组，根据组找到绑定的角色集，分别取缓存返回，需要去数据库查询的是根据userid找角色
		List<WoResource> resources = this.isGuest(id) ? this.resourceService.queryResourceForGuest(ResourceEnum.MENU.toString())
				: this.resourceService.queryResource(ResourceEnum.MENU.toString(), id);

		List<Menu> menus = resources.parallelStream().map(res -> {
			// 取出菜单
			Menu node = new Menu();
			node.setPath(res.getResourcePath());
			node.setIcon(res.getIcon());
			node.setName(res.getResourceName());
			node.setTarget(res.getTarget());
			node.setId(res.getId());
			node.setParentId(res.getParentId());
			return node;
		}).collect(Collectors.toList());

		return TreeUtil.build(menus, PubConstants.MENU_ROOT_ID);
	}

	public List<Menu> queryAdminMenuByUserId(Long curUserId) {
		List<WoResource> resources = this.resourceService.queryResource(ResourceEnum.ADMIN_MENU.toString(), curUserId);
		List<Menu> menus = resources.parallelStream().map(res -> {
			// 取出菜单
			Menu node = new Menu();
			node.setPath(res.getResourcePath());
			node.setIcon(res.getIcon());
			node.setName(res.getResourceName());
			node.setTarget(res.getTarget());
			node.setId(res.getId());
			node.setParentId(res.getParentId());
			return node;
		}).collect(Collectors.toList());
		log.info(menus.toString());
		return TreeUtil.build(menus, PubConstants.MENU_ROOT_ID);
	}

	/**
	 * 获取页面操作权限。
	 *
	 * @param id
	 * @return
	 */
	public List<String> queryAuthorityButton(Long id) {
		// @todo 二期优化：应该按照用户角色为key做缓存，根据用户id找到归属的用户组，根据组找到绑定的角色集，分别取缓存返回，需要去数据库查询的是根据userid找角色
		boolean isGuest = this.isGuest(id);
		List<WoResource> resources = isGuest ? this.resourceService.queryResourceForGuest(ResourceEnum.BUTTON.toString())
				: this.resourceService.queryResource(ResourceEnum.BUTTON.toString(), id);

		List<String> authorities = new ArrayList<>();

		for (WoResource r : resources) {
			authorities.add(r.getResourceCode()); // 取出操作权限编码（资源编码）
		}

		if (isGuest) { // 游客的特性在于所有未登录用户都是游客，这是一个角色，游客如果可以访问的资源，用户一定可以，
			// 实现的方式：游客角色绑定平台根节点上，平台下挂管理员与会员两组，会员下挂免费会员和付费会员，以此类推，组织分层则权限继承、平级组织专权不共享。
			authorities.add(UserRoleEnum.GUEST.toString()); // 未登录用户默认guest角色权限。
		}
		else {
			authorities.add(UserRoleEnum.USER.toString()); // 登录用户默认user角色权限。
		}

		return authorities;
	}
}
