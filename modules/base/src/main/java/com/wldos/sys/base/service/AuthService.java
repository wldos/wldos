/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.common.Constants;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.TreeUtils;
import com.wldos.support.cache.ICache;
import com.wldos.sys.base.dto.MenuAndRoute;
import com.wldos.sys.base.dto.Term;
import com.wldos.sys.base.entity.WoResource;
import com.wldos.sys.base.enums.ResourceEnum;
import com.wldos.sys.base.enums.UserRoleEnum;
import com.wldos.sys.base.repo.DomainResourceRepo;
import com.wldos.sys.base.repo.ResourceRepo;
import com.wldos.sys.base.vo.AuthInfo;
import com.wldos.sys.base.vo.AuthVerify;
import com.wldos.sys.base.vo.DomainResource;
import com.wldos.sys.base.vo.Menu;
import com.wldos.sys.base.vo.Route;
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
	private final ResourceRepo resourceRepo;

	private final DomainResourceRepo domainResourceRepo;

	private final TermService termService;

	private final ICache cache;

	@Autowired
	public AuthService(ICache cache, ResourceRepo resourceRepo, DomainResourceRepo domainResourceRepo, TermService termService) {
		this.cache = cache;
		this.resourceRepo = resourceRepo;
		this.domainResourceRepo = domainResourceRepo;
		this.termService = termService;
	}

	/**
	 * 验证用户对请求资源的权限。
	 *
	 * @param domainId 请求的域id
	 * @param appCode 应用编码
	 * @param userId 用户ID为应用程序创建分布式唯一，如果是数据库自动生成，请使用loginName
	 * @param comId 用户主企业id
	 * @param reqUri 请求资源URI
	 * @param reqMethod 请求方法：GET,POST,PUT,DELETE
	 * @return AuthVerify 验证结果
	 */
	public AuthVerify verifyReqAuth(Long domainId, String appCode, Long userId, Long comId, String reqUri, String reqMethod) {
		AuthVerify authVerify = new AuthVerify();
		if (this.termService.isAdmin(userId)) {
			authVerify.setAuth(true);
			return authVerify;
		}
		List<AuthInfo> allMenuAuthInfo = this.queryAllAuth(appCode);
		List<AuthInfo> matchAuthInfos =
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

		List<AuthInfo> authInfos = this.isGuest(userId) ? this.resourceRepo.queryAuthInfoForGuest(domainId, appCode)
				: this.resourceRepo.queryAuthInfo(domainId, comId, appCode, userId);

		AuthInfo matchReq = null;

		for (AuthInfo auth : authInfos) {

			boolean isMatch = matchAuthInfos.stream().anyMatch(authInfo -> authInfo.getResourceCode().equals(auth.getResourceCode()));
			if (isMatch) {
				matchReq = auth;
				break;
			}
		}

		if (matchReq == null) {
			authVerify.setAuth(false);
		}
		else {
			authVerify.setAuthInfo(matchReq);
			authVerify.setAuth(true);
		}

		return authVerify;
	}

	/**
	 * 查询所有资源权限
	 *
	 * @param appCode 应用编码
	 * @return 应用授权信息
	 */
	public List<AuthInfo> queryAllAuth(String appCode) {
		// @todo 此处应设置所有资源的缓存取值逻辑（key存在取值，不存在查数据库并存储到缓存）
		String key = this.authKey(appCode);
		String value = ObjectUtils.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<AuthInfo> authInfos = this.resourceRepo.queryAuthInfo(appCode);

				value = om.writeValueAsString(authInfos);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return authInfos;
			}

			return JSON.parseArray(value, AuthInfo.class);
		}
		catch (JsonProcessingException e) {
			log.error("json解析异常={} {}", value, e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * 刷新某应用资源缓存
	 *
	 * @param appCode 应用编码
	 */
	public void refreshAuth(String appCode) {
		this.cache.remove(this.authKey(appCode));
	}

	private String authKey(String appCode) {
		return RedisKeyEnum.WLDOS_AUTH.toString() + ":" + appCode;
	}

	/**
	 * 查询某域内用户的可见菜单以及域上定制的动态路由模板，授权资源来源：1.组织权限，2.平台权限。
	 *
	 * @param domainId 域id
	 * @param comId 用户主企业id，用于判定用户的组织权限
	 * @param menuType 菜单类型：普通菜单、管理菜单
	 * @param id 用户id
	 * @return 菜单列表
	 */
	public MenuAndRoute queryMenuAndRouteByUserId(Long domainId, Long comId, String menuType, Long id) {
		List<WoResource> resources = this.getResourcesByUserId(domainId, comId, menuType, id);

		if (ObjectUtils.isBlank(resources))
			return null;

		List<Menu> menus = this.getMenuByUserId(resources);

		List<DomainResource> domRes = this.domainResourceRepo.queryDomainDynamicRoutes(domainId);
		List<Long> termTypeIds = domRes.parallelStream().map(DomainResource::getTermTypeId).collect(Collectors.toList());
		if (ObjectUtils.isBlank(termTypeIds)) {
			log.error("未配置首页模板，内容分类为空");
			return null;
		}
		List<Term> terms = this.termService.queryAllByTermTypeIds(termTypeIds);
		Map<Long, Term> termMap = terms.parallelStream().collect(Collectors.toMap(Term::getTermTypeId, t -> t));

		Map<String, Route> modules = domRes.parallelStream().collect(Collectors.toMap(DomainResource::getResourcePath,
				d -> {
					try {
						if (d.getTermTypeId() == Constants.TOP_TERM_ID)
							return new Route(d.getModuleName(), null);
						Term term = termMap.get(d.getTermTypeId());
						return new Route(d.getModuleName(), term.getSlug());
					}
					catch (RuntimeException e) {
						throw new ResTermNoFoundException("资源对应的分类项不存在, 资源id：" + d.getId());
					}
				}));

		return new MenuAndRoute(menus, modules);
	}

	/**
	 * 查询某域内用户的可见菜单，授权菜单来源：1.组织权限，2.平台权限。
	 *
	 * @param domainId 域id
	 * @param comId 用户主企业id，用于判定用户的组织权限
	 * @param menuType 菜单类型：普通菜单、管理菜单
	 * @param id 用户id
	 * @return 菜单列表
	 */
	public List<Menu> queryMenuByUserId(Long domainId, Long comId, String menuType, Long id) {
		List<WoResource> resources = this.getResourcesByUserId(domainId, comId, menuType, id);
		return this.getMenuByUserId(resources);
	}

	private List<Menu> getMenuByUserId(List<WoResource> resources) {
		List<Menu> menus = resources.parallelStream().map(res ->
				Menu.of(res.getResourcePath(), res.getIcon(), res.getResourceName(), res.getTarget(), res.getId(), res.getParentId(), res.getDisplayOrder()))
				.collect(Collectors.toList());

		return TreeUtils.build(menus, Constants.MENU_ROOT_ID);
	}

	private List<WoResource> getResourcesByUserId(Long domainId, Long comId, String menuType, Long id) {
		// @todo 二期优化：应该按照用户角色为key做缓存，根据用户id找到归属的用户组，根据组找到绑定的角色集，分别取缓存返回，需要去数据库查询的是根据userid找角色
		return this.isGuest(id) ? this.resourceRepo.queryResourceForGuest(domainId, menuType)
				: this.resourceRepo.queryResource(domainId, comId, menuType, id);
	}

	/**
	 * 获取页面操作权限。
	 *
	 * @param domainId 域id
	 * @param comId 用户主企业id
	 * @param id 用户id
	 * @return 操作权限列表
	 */
	public List<String> queryAuthorityButton(Long domainId, Long comId, Long id) {
		// @todo 二期优化：应该按照用户角色为key做缓存，根据用户id找到归属的用户组，根据组找到绑定的角色集，分别取缓存返回，需要去数据库查询的是根据userid找角色
		boolean isGuest = this.isGuest(id);
		List<WoResource> resources = isGuest ? this.resourceRepo.queryResourceForGuest(domainId, ResourceEnum.BUTTON.getValue())
				: this.resourceRepo.queryResource(domainId, comId, ResourceEnum.BUTTON.getValue(), id);

		List<String> authorities = new ArrayList<>();

		for (WoResource r : resources) {
			authorities.add(r.getResourceCode());
		}

		if (isGuest) {
			authorities.add(UserRoleEnum.GUEST.toString());
		}
		else
			authorities.add(UserRoleEnum.USER.toString());

		return authorities;
	}

	/**
	 * 游客专门处理，用于满足互联网应用需求
	 *
	 * @param userId 用户id
	 * @return 是否游客
	 */
	public boolean isGuest(Long userId) {
		return Constants.GUEST_ID.equals(userId);
	}
}