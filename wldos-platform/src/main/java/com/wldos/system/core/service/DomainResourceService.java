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
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.support.enums.DeleteFlagEnum;
import com.wldos.support.enums.ValidStatusEnum;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.ObjectUtil;
import com.wldos.system.core.entity.WoDomainResource;
import com.wldos.system.core.repo.DomainResourceRepo;
import com.wldos.system.enums.RedisKeyEnum;
import com.wldos.system.vo.DomainResource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域资源管理service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DomainResourceService extends BaseService<DomainResourceRepo, WoDomainResource, Long> {

	public List<WoDomainResource> queryDomainResources(String domain, List<Long> appIds, List<Long> resIds) {
		if (ObjectUtil.isBlank(appIds) || ObjectUtil.isBlank(resIds))
			return new ArrayList<>();
		return this.entityRepo.queryDomainDynamicRoutes(domain, appIds, resIds);
	}

	public List<DomainResource> queryDomainDynamicRoutes(Long domainId) {
		String key = queryDomainDynamicRoutesCacheKey(domainId);
		String value = ObjectUtil.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				List<DomainResource> resources = this.entityRepo.queryDomainDynamicRoutes(domainId);

				if (ObjectUtil.isBlank(resources))
					return new ArrayList<>();

				value = om.writeValueAsString(resources);
				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return resources;
			}

			return om.readValue(value, new TypeReference<List<DomainResource>>() {});
		}
		catch (JsonProcessingException e) {
			this.getLog().error("json解析异常={} {}", value, e.getMessage());
			return new ArrayList<>();
		}
	}

	public String queryDomainDynamicRoutesCacheKey(Long domainId) {
		return RedisKeyEnum.WLDOS_DOMAIN_RES.toString() + domainId + "dynamic";
	}

	public List<WoDomainResource> queryDomainResWithTerm(Long domainId) {
		return this.entityRepo.queryDomainResWithTerm(domainId);
	}

	public List<WoDomainResource> queryDomainRes(Long domainId, List<Long> resIds) {
		return this.entityRepo.queryDomainRes(domainId, resIds);
	}

	public WoDomainResource queryDomResById(Long resId, Long domId) {
		return this.entityRepo.queryDomResById(resId, domId);
	}

	public void removeDomainRes(List<Long> ids) {
		this.deleteByEntityAndIds(new WoDomainResource(), false, ids.toArray());
	}

	public void removeDomainRes(List<Long> ids, Long domainId) {
		this.entityRepo.removeDomainRes(ids, domainId);
	}

	public List<WoDomainResource> queryDomainRes(Long domainId) {
		return this.entityRepo.queryAllByDomainIdAndIsValidAndDeleteFlag(domainId, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
	}
}
