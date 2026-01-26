/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.auth.impl;

import com.wldos.common.Constants;
import com.wldos.common.utils.TreeUtils;
import com.wldos.platform.support.auth.AuthOpener;
import com.wldos.platform.support.auth.enums.UserRoleEnum;
import com.wldos.platform.support.auth.vo.AuthVerify;
import com.wldos.platform.support.resource.ResTermNoFoundException;
import com.wldos.platform.support.resource.entity.WoResource;
import com.wldos.platform.support.resource.enums.ResourceEnum;
import com.wldos.platform.support.resource.vo.AuthInfo;
import com.wldos.platform.support.resource.vo.DynSet;
import com.wldos.platform.support.resource.vo.Menu;
import com.wldos.platform.support.term.TermOpener;
import com.wldos.platform.support.term.dto.Term;
import com.wldos.framework.support.cache.ICache;
import com.wldos.platform.support.domain.DomainResourceOpener;
import com.wldos.platform.support.domain.vo.DomainResource;
import com.wldos.platform.support.resource.ResourceOpener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 默认 AuthOpener 实现类（开源版本）
 * 当 Agent 模块中的 AuthOpenerImpl 不存在时使用此实现
 * 使用降级处理：简化权限验证逻辑，允许管理员访问所有资源，普通用户基于资源查询结果判断
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2026/01/10
 */
@RefreshScope
@ConditionalOnMissingClass("com.wldos.support.auth.AuthOpenerImpl")
@Component
public class DefaultAuthOpenerImpl implements AuthOpener {

	@Value("${gateway.ignore.path:}")
	private String pathIgnore;

	@Value("${gateway.token.path:}")
	private String tokenUrl;

	private List<String> excludeUris;
	private List<String> tokenUris;

	@PostConstruct
	private void init() {
		this.excludeUris = pathIgnore != null && !pathIgnore.isEmpty() ? Arrays.asList(pathIgnore.split(",")) : new ArrayList<>();
		this.tokenUris = tokenUrl != null && !tokenUrl.isEmpty() ? Arrays.asList(tokenUrl.split(",")) : new ArrayList<>();
	}

	@Override
	public AuthVerify verifyReqAuth(Long domainId, String appCode, Long userId, Long comId, String reqUri, String reqMethod,
	                                TermOpener termOpener, ResourceOpener resourceRepo, ICache cache) {
		AuthVerify authVerify = new AuthVerify();
		// 降级处理：管理员直接允许访问
		if (termOpener != null && termOpener.isAdmin(userId)) {
			authVerify.setAuth(true);
			return authVerify;
		}
		// 查询所有资源权限
		List<AuthInfo> allMenuAuthInfo = this.queryAllAuth(appCode, resourceRepo, cache);
		if (allMenuAuthInfo == null || allMenuAuthInfo.isEmpty()) {
			// 降级处理：如果没有配置资源权限，允许访问
			authVerify.setAuth(true);
			return authVerify;
		}
		// 匹配请求的资源
		List<AuthInfo> matchAuthInfos = allMenuAuthInfo.stream()
				.filter(authInfo -> {
					String uri = authInfo.getResourcePath();
					if (uri != null && uri.contains("{")) {
						uri = uri.replaceAll("\\{\\*\\}", "[a-zA-Z\\d]+");
					}
					String exp = "^" + (uri != null ? uri : "") + "$";
					return reqUri.matches(exp) && reqMethod.equals(authInfo.getRequestMethod());
				})
				.collect(Collectors.toList());

		if (matchAuthInfos.isEmpty()) {
			// 降级处理：如果没有匹配的资源，允许访问
			authVerify.setAuth(true);
			return authVerify;
		}

		// 查询用户权限
		List<AuthInfo> authInfos = this.isGuest(userId) 
				? resourceRepo.queryAuthInfoForGuest(domainId, appCode)
				: resourceRepo.queryAuthInfo(domainId, comId, appCode, userId);

		// 检查是否有匹配的权限
		AuthInfo matchReq = authInfos.stream()
				.filter(auth -> matchAuthInfos.stream()
						.anyMatch(authInfo -> authInfo.getResourceCode().equals(auth.getResourceCode())))
				.findFirst()
				.orElse(null);

		if (matchReq == null) {
			authVerify.setAuth(false);
		} else {
			authVerify.setAuthInfo(matchReq);
			authVerify.setAuth(true);
		}

		return authVerify;
	}

	@Override
	public AuthVerify verifyReqAuth(Long domainId, String appCode, Long userId, Long comId, String reqUri, String reqMethod) {
		// 降级处理：简化版本，直接允许访问
		AuthVerify authVerify = new AuthVerify();
		authVerify.setAuth(true);
		return authVerify;
	}

	@Override
	public List<AuthInfo> queryAllAuth(String appCode, ResourceOpener resourceRepo, ICache cache) {
		// 降级处理：不使用缓存，直接查询数据库
		return resourceRepo.queryAuthInfo(appCode);
	}

	@Override
	public void refreshAuth(String appCode, ICache cache) {
		// 降级处理：空实现，不使用缓存
	}

	@Override
	public List<Menu> queryMenuByUserId(Long domainId, Long comId, String menuType, Long id, ResourceOpener resourceRep) {
		List<WoResource> resources = this.getResourcesByUserId(domainId, comId, menuType, id, resourceRep);
		if (resources == null || resources.isEmpty()) {
			return new ArrayList<>();
		}
		return this.getMenuByUserId(resources);
	}

	@Override
	public List<Menu> queryMenuByUserId(Long domainId, Long comId, String[] menuTypes, Long id, ResourceOpener resourceRep) {
		List<WoResource> resources = this.getResourcesByUserId(domainId, comId, menuTypes, id, resourceRep);
		if (resources == null || resources.isEmpty()) {
			return new ArrayList<>();
		}
		return this.getMenuByUserId(resources);
	}

	private List<Menu> getMenuByUserId(List<WoResource> resources) {
		List<Menu> menus = resources.stream()
				.map(res -> Menu.of(res.getResourceType(), res.getResourcePath(), res.getComponentPath(), res.getIcon(),
						res.getResourceName(), res.getTarget(), res.getId(), res.getParentId(), res.getDisplayOrder()))
				.collect(Collectors.toList());

		return TreeUtils.build(menus, Constants.MENU_ROOT_ID);
	}

	private List<WoResource> getResourcesByUserId(Long domainId, Long comId, String menuType, Long id, ResourceOpener resourceRepo) {
		return this.isGuest(id) 
				? resourceRepo.queryResourceForGuest(domainId, menuType)
				: resourceRepo.queryResource(domainId, comId, menuType, id);
	}

	private List<WoResource> getResourcesByUserId(Long domainId, Long comId, String[] menuTypes, Long id, ResourceOpener resourceRepo) {
		return this.isGuest(id) 
				? resourceRepo.queryResourceForGuest(domainId, menuTypes)
				: resourceRepo.queryResource(domainId, comId, menuTypes, id);
	}

	@Override
	public List<String> queryAuthorityButton(Long domainId, Long comId, Long id, ResourceOpener resourceRepo) {
		boolean isGuest = this.isGuest(id);
		String[] buttonTypes = new String[] {ResourceEnum.BUTTON.getValue(), ResourceEnum.PLUGIN_BUTTON.getValue()};
		List<WoResource> resources = isGuest 
				? resourceRepo.queryResourceForGuest(domainId, buttonTypes)
				: resourceRepo.queryResource(domainId, comId, buttonTypes, id);

		List<String> authorities = resources.stream()
				.map(WoResource::getResourceCode)
				.collect(Collectors.toList());

		if (isGuest) {
			authorities.add(UserRoleEnum.GUEST.toString());
		} else {
			authorities.add(UserRoleEnum.USER.toString());
		}

		return authorities;
	}

	@Override
	public boolean isGuest(Long userId) {
		return Constants.GUEST_ID.equals(userId);
	}

	@Override
	public Map<String, DynSet> queryDynRoute(TermOpener termOpener, DomainResourceOpener domainResourceRepo, Long domainId) {
		List<DomainResource> domRes = domainResourceRepo.queryDomainDynamicRoutes(domainId);
		if (domRes == null || domRes.isEmpty()) {
			return new java.util.HashMap<>();
		}

		List<Long> termTypeIds = domRes.stream()
				.map(DomainResource::getTermTypeId)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		List<Term> terms = termOpener != null ? termOpener.queryAllByTermTypeIds(termTypeIds) : new ArrayList<>();
		Map<Long, Term> termMap = terms.stream()
				.collect(Collectors.toMap(Term::getTermTypeId, t -> t));

		return domRes.stream()
				.collect(Collectors.toMap(DomainResource::getResourcePath, d -> {
					try {
						if (d.getTermTypeId() == null || d.getTermTypeId().equals(Constants.TOP_TERM_ID)) {
							return DynSet.of(d.getModuleName(), null, d.getUrl());
						}
						Term term = termMap.get(d.getTermTypeId());
						if (term == null) {
							throw new ResTermNoFoundException("资源对应的分类项不存在, 资源id：" + d.getId());
						}
						return DynSet.of(d.getModuleName(), term.getSlug(), d.getUrl());
					} catch (RuntimeException e) {
						throw new ResTermNoFoundException("资源对应的分类项不存在, 资源id：" + d.getId());
					}
				}));
	}

	@Override
	public String authorityRoute(String route, Long userId, Long domainId, Long tenantId, HttpServletRequest request,
	                             AuthOpener authService) {
		// 如果是放开路由，直接返回200
		if (this.isMatchUri(route, this.excludeUris)) {
			return "200";
		}
		// 鉴权路由，先检查token，游客返回401
		if (this.isMatchUri(route, this.tokenUris) && authService.isGuest(userId)) {
			return "401";
		}

		String appCode = this.appCode(route);
		if (appCode == null) {
			return "403";
		}

		AuthVerify authVerify = authService.verifyReqAuth(domainId, appCode, userId, tenantId, route, request.getMethod());

		if (authVerify != null && authVerify.isAuth()) {
			return "200";
		}
		return "403";
	}

	private boolean isMatchUri(String reqUri, List<String> target) {
		if (target == null || target.isEmpty()) {
			return false;
		}
		for (String s : target) {
			if (s.equals("/")) {
				if (Objects.equals(reqUri, s)) {
					return true;
				}
				continue;
			}
			if (reqUri.startsWith(s)) {
				return true;
			}
		}
		return false;
	}

	private String appCode(String reqUri) {
		try {
			String root = "/";
			if (reqUri == null || reqUri.isEmpty() || root.equals(reqUri.trim())) {
				return root;
			}

			String[] temp = reqUri.split(root);
			if (temp.length > 1 && temp[1] != null && !temp[1].isEmpty()) {
				return temp[1].toLowerCase();
			}
		} catch (Exception e) {
			// 忽略异常
		}
		return null;
	}
}
