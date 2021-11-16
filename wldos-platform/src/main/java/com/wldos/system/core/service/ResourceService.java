/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.List;
import java.util.stream.Collectors;

import com.wldos.support.Constants;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.PageQuery;
import com.wldos.system.core.entity.WoDomain;
import com.wldos.system.core.entity.WoDomainResource;
import com.wldos.system.core.repo.ResourceRepo;
import com.wldos.system.vo.AuthInfo;
import com.wldos.system.core.entity.WoResource;
import com.wldos.system.vo.DomRes;
import com.wldos.system.vo.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源操作service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceService extends BaseService<ResourceRepo, WoResource, Long> {
	private final DomainService domainService;
	private final DomainAppService domainAppService;

	public ResourceService(DomainService domainService, DomainAppService domainAppService) {
		this.domainService = domainService;
		this.domainAppService = domainAppService;
	}
	
	public List<AuthInfo> queryAuthInfo(String domain, String appCode, Long userId, Long comId) {

		WoDomain woDomain = this.domainService.findByDomain(domain);

		return this.entityRepo.queryAuthInfo(woDomain.getId(), comId, appCode, userId);
	}

	public List<AuthInfo> queryAuthInfo(String appCode) {
		return this.entityRepo.queryAuthInfo(appCode);
	}

	public List<WoResource> queryResource(String type) {
		return null;
	}

	public List<WoResource> queryResourceByRoleId(Long roleId) {
		return this.entityRepo.queryResourceByRoleId(roleId);
	}

	public List<WoResource> queryResourceByInheritRoleId(Long roleId) {
		return this.entityRepo.queryResourceByInheritRoleId(roleId);
	}
	
	public List<WoResource> queryResource(String domain, Long comId, String type, Long userId) {
		WoDomain woDomain = this.domainService.findByDomain(domain);

		return this.entityRepo.queryResource(woDomain.getId(), comId, type, userId);
	}
	
	public List<AuthInfo> queryAuthInfoForGuest(String domain, String appCode) {

		WoDomain woDomain = this.domainService.findByDomain(domain);

		return this.entityRepo.queryAuthInfoForGuest(woDomain.getId(), appCode);
	}
	
	public List<WoResource> queryResourceForGuest(String domain, String type) {
		WoDomain woDomain = this.domainService.findByDomain(domain);

		return this.entityRepo.queryResourceForGuest(woDomain.getId(), type);
	}
	
	public PageableResult<DomRes> queryResByDomainId(Long domainId, Long comId, PageQuery pageQuery) {

		List<Object> appIds = this.domainAppService.queryAppListByDomainId(domainId, comId);
		pageQuery.pushFilter("appId", appIds);

		PageableResult<DomRes> domRes = this.execQueryForTree(new DomRes(), new WoResource(), pageQuery, Constants.MENU_ROOT_ID);

		List<WoDomainResource> resources = this.domainService.queryDomainRes(domainId);
		List<Long> bookedIds = resources.parallelStream().map(WoDomainResource::getResourceId).collect(Collectors.toList());

		List<DomRes> allRes = domRes.getData().getRows();

		allRes = allRes.parallelStream().map(r -> {
			r.setSelected(bookedIds.contains(r.getId()));
			return r;
		}).collect(Collectors.toList());

		domRes.setDataRows(allRes);

		return domRes;
	}
}
