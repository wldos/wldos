/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.cms.entity.KModelContent;
import com.wldos.cms.service.ContentService;
import com.wldos.cms.service.TermService;
import com.wldos.cms.vo.Term;
import com.wldos.support.Constants;
import com.wldos.support.cache.ICache;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.auth.dto.MenuAndRoute;
import com.wldos.system.auth.vo.Menu;
import com.wldos.system.auth.vo.Route;
import com.wldos.system.core.entity.WoResource;
import com.wldos.system.core.exception.ResourceTermTypeNoFoundException;
import com.wldos.system.enums.RedisKeyEnum;
import com.wldos.system.enums.ResourceEnum;
import com.wldos.system.enums.UserRoleEnum;
import com.wldos.system.vo.AuthInfo;
import com.wldos.system.vo.AuthVerify;
import com.wldos.system.vo.DomainResource;
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

	private final DomainService domainService;

	private final TermService termService;

	private final ContentService contentService;

	private final ICache cache;

	@Autowired
	public AuthService(ICache cache, ResourceService resourceService, DomainService domainService, TermService termService, ContentService contentService) {
		this.cache = cache;
		this.resourceService = resourceService;
		this.domainService = domainService;
		this.termService = termService;
		this.contentService = contentService;
	}

	public AuthVerify verifyReqAuth(String domain, String appCode, Long userId, Long comId, String reqUri, String reqMethod) {
		AuthVerify authVerify = new AuthVerify();
		if (this.domainService.isAdmin(userId)) {
			authVerify.setAuth(true);
			return authVerify;
		}
		List<AuthInfo> allMenuAuthInfo = this.queryAllAuth(appCode);
		List<AuthInfo> matchAuthInfos =
				allMenuAuthInfo.parallelStream().filter(authInfo -> {
					String uri = authInfo.getResourcePath();
					if (uri.contains("{")) {
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

		List<AuthInfo> authInfos = this.isGuest(userId) ? this.resourceService.queryAuthInfoForGuest(domain, appCode)
				: this.resourceService.queryAuthInfo(domain, appCode, userId, comId);

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

	public List<AuthInfo> queryAllAuth(String appCode) {

		String key = this.authKey(appCode);
		String value = ObjectUtil.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {

				List<AuthInfo> authInfos = this.resourceService.queryAuthInfo(appCode);

				if (ObjectUtil.isBlank(authInfos))
					return new ArrayList<>();

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

	public void refreshAuth(String appCode) {
		this.cache.remove(this.authKey(appCode));
	}

	private String authKey(String appCode) {
		return RedisKeyEnum.WLDOS_AUTH.toString() + ":" + appCode;
	}

	public MenuAndRoute queryMenuAndRouteByUserId(String domain, Long comId, String menuType, Long id) {
		List<WoResource> resources = this.getResourcesByUserId(domain, comId, menuType, id);

		if (ObjectUtil.isBlank(resources))
			return null;

		List<Menu> menus = this.getMenuByUserId(resources);

		List<DomainResource> domRes = this.domainService.queryDynRoutesByDomain(domain);
		List<Long> termTypeIds = domRes.parallelStream().map(DomainResource::getTermTypeId).collect(Collectors.toList());
		if (ObjectUtil.isBlank(termTypeIds)) {
			log.error("未配置首页模板，内容分类为空");
			return null;
		}
		List<Term> terms = this.termService.queryAllByTermTypeIds(termTypeIds);
		Map<Long, Term> termMap = terms.parallelStream().collect(Collectors.toMap(Term::getTermTypeId, t -> t));

		Map<String, Route> modules = domRes.parallelStream().collect(Collectors.toMap(DomainResource::getResourcePath,
			d -> {
				try {
					Term term = termMap.get(d.getTermTypeId());
					KModelContent content = this.contentService.findByContentId(term.getContentId());
					return new Route(d.getModuleName(), term.getSlug(), content.getContentCode());
				} catch (RuntimeException e) {
					throw new ResourceTermTypeNoFoundException("资源对应的分类项不存在, 资源id：" + d.getId());
				}
			}));

		return new MenuAndRoute(TreeUtil.build(menus, Constants.MENU_ROOT_ID), modules);
	}

	public List<Menu> queryMenuByUserId(String domain, Long comId, String menuType, Long id) {
		List<WoResource> resources = this.getResourcesByUserId(domain, comId, menuType, id);
		return this.getMenuByUserId(resources);
	}

	private List<Menu> getMenuByUserId(List<WoResource> resources) {
		List<Menu> menus = resources.parallelStream().map(res -> {

			Menu node = new Menu();
			node.setPath(res.getResourcePath());
			node.setIcon(res.getIcon());
			node.setName(res.getResourceName());
			node.setTarget(res.getTarget());
			node.setId(res.getId());
			node.setParentId(res.getParentId());
			return node;
		}).collect(Collectors.toList());

		return TreeUtil.build(menus, Constants.MENU_ROOT_ID);
	}

	private List<WoResource> getResourcesByUserId(String domain, Long comId, String menuType, Long id) {

		return this.isGuest(id) ? this.resourceService.queryResourceForGuest(domain, menuType)
				: this.resourceService.queryResource(domain, comId, menuType, id);
	}

	public List<String> queryAuthorityButton(String domain, Long comId, Long id) {

		boolean isGuest = this.isGuest(id);
		List<WoResource> resources = isGuest ? this.resourceService.queryResourceForGuest(domain, ResourceEnum.BUTTON.toString())
				: this.resourceService.queryResource(domain, comId, ResourceEnum.BUTTON.toString(), id);

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

	public boolean isGuest(Long userId) {
		return Constants.GUEST_ID.equals(userId);
	}
}
