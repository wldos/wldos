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
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.support.Constants;
import com.wldos.support.controller.EntityAssists;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.enums.DeleteFlagEnum;
import com.wldos.support.enums.ValidStatusEnum;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.system.core.entity.WoDomain;
import com.wldos.system.core.entity.WoDomainApp;
import com.wldos.system.core.entity.WoDomainResource;
import com.wldos.system.core.entity.WoResource;
import com.wldos.system.core.repo.DomainRepo;
import com.wldos.system.enums.RedisKeyEnum;
import com.wldos.system.storage.IStore;
import com.wldos.system.vo.DomainResource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域管理service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Service
@Transactional(rollbackFor = Exception.class)
public class DomainService extends BaseService<DomainRepo, WoDomain, Long> {
	@Value("${wldos.platform.logo.default}")
	private String defaultLogo;

	private final DomainAppService domainAppService;

	private final DomainResourceService domainResService;

	private final IStore store;

	public DomainService(DomainAppService domainAppService, DomainResourceService domainResService, IStore store) {
		this.domainAppService = domainAppService;
		this.domainResService = domainResService;
		this.store = store;
	}
	
	public String domainApp(List<String> appIds, Long domainId, Long comId, Long curUserId, String uip) {
		StringBuilder mes = new StringBuilder(50);
		List<WoDomainApp> domainAppList = appIds.parallelStream().map(item -> {
			Long appId = Long.parseLong(item);

			boolean exists = this.domainAppService.existsByAppIdAndDomainId(appId, domainId);
			if (exists) {
				mes.append("应用：").append(appId).append(" 已存在; ");
				return null;
			}

			WoDomainApp domainApp = new WoDomainApp();
			domainApp.setDomainId(domainId);
			domainApp.setAppId(appId);
			EntityAssists.beforeInsert(domainApp, this.nextId(), curUserId, uip, true);
			domainApp.setComId(comId);

			return domainApp;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (!domainAppList.isEmpty())
			this.domainAppService.saveAll(domainAppList);

		return mes.toString();
	}
	
	public String domainRes(List<String> resIds, Long domainId, Long curUserId, String uip) {
		StringBuilder mes = new StringBuilder(50);
		List<WoDomainResource> domainResList = resIds.parallelStream().map(item -> {
			Long resId = Long.parseLong(item);
			WoDomainResource domainRes = this.domainResService.queryDomResById(resId, domainId);
			if (domainRes == null || domainRes.getResourceId() == null) {
				mes.append("资源：").append(resId).append(" 已存在; ");
				return null;
			}
			EntityAssists.beforeInsert(domainRes, this.nextId(), curUserId, uip, true);
			return domainRes;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (!domainResList.isEmpty())
			this.domainResService.saveAll(domainResList);

		return mes.toString();
	}
	
	public void removeDomainApp(List<Long> ids, Long domainId) {
		this.domainAppService.removeDomainApp(ids, domainId);
	}
	
	public void removeDomainRes(List<Long> ids, Long domainId) {
		this.domainResService.removeDomainRes(ids, domainId);
	}
	
	public Map<String, Long> queryAllDomainID() {
		String key = RedisKeyEnum.WLDOS_DOMAIN.toString() + ":ID";
		String value = ObjectUtil.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				List<WoDomain> domains = this.queryAllDomain();
				if (ObjectUtil.isBlank(domains)) {
					WoDomain domain = new WoDomain();
					domain.setComId(Constants.TOP_COM_ID);
					domain.setSecondDomain(this.getPlatDomain());
					domain.setSiteDomain(this.getPlatDomain());
					domain.setDisplayOrder(1L);
					domain.setSiteDescription("平台主站");
					domain.setSiteUrl("https://" + this.getPlatDomain());
					EntityAssists.beforeInsert(domain, this.nextId(), Constants.SYSTEM_USER_ID, "127.0.0.1", false);
					this.insertSelective(domain);
					domains = new ArrayList<>();
					domains.add(domain);
				}

				Map<String, Long> domInfo = domains.parallelStream().collect(Collectors.toMap(WoDomain::getSiteDomain, WoDomain::getId));

				value = om.writeValueAsString(domInfo);
				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return domInfo;
			}

			return new ObjectMapper().readValue(value, new TypeReference<Map<String, Long>>() {});
		}
		catch (JsonProcessingException e) {
			this.getLog().error("json解析异常={} {}", value, e.getMessage());
			return null;
		}
	}
	
	public List<WoDomain> queryAllDomain() {
		String key = RedisKeyEnum.WLDOS_DOMAIN.toString();
		String value = ObjectUtil.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				List<WoDomain> domains = this.entityRepo.findAllByDeleteFlagAndIsValid(DeleteFlagEnum.NORMAL.toString(), ValidStatusEnum.VALID.toString());

				value = om.writeValueAsString(domains);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return domains;
			}

			return om.readValue(value, new TypeReference<List<WoDomain>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取所有域名实例异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}
	
	public void refreshDomain(WoDomain domain) {
		this.cache.remove(RedisKeyEnum.WLDOS_DOMAIN.toString());
		this.cache.remove(RedisKeyEnum.WLDOS_DOMAIN.toString() + ":ID");
		this.cache.remove(this.domainResService.queryDomainDynamicRoutesCacheKey(domain.getId()));
		this.cache.remove(RedisKeyEnum.WLDOS_TERM.toString() + domain.getSiteDomain());
		this.cache.remove(RedisKeyEnum.WLDOS_TERM_TREE.toString() + domain.getSiteDomain());
	}
	
	public WoDomain findByDomain(String domain) {
		List<WoDomain> domains = this.queryAllDomain();
		for (WoDomain d : domains) {
			if (d.getSiteDomain().equals(domain)) {
				d.setSiteLogo(ObjectUtil.string(this.getDomainLogo(d.getSiteLogo())));
				return d;
			}
		}

		return null;
	}
	
	public WoDomain queryDomainByName(String domain) {
		List<WoDomain> domains = this.queryAllDomain();
		for (WoDomain d : domains) {
			if (d.getSiteDomain().equals(domain)) {
				return d;
			}
		}

		return null;
	}
	
	public String getDomainLogo(String logoPath) {
		return this.store.getFileUrl(logoPath, this.store.getFileUrl(this.defaultLogo, null));
	}
	
	public PageableResult<WoDomain> queryDomainList(PageQuery pageQuery) {
		PageableResult<WoDomain> domainPage = this.execQueryForPage(new WoDomain(), pageQuery);
		List<WoDomain> domains = domainPage.getData().getRows();

		domains = domains.parallelStream().map(d -> {
			String logo = d.getSiteLogo();
			if (!ObjectUtil.isBlank(logo))
				d.setSiteLogo(this.store.getFileUrl(logo, null));
			return d;
		}).collect(Collectors.toList());

		domainPage.setDataRows(domains);

		return domainPage;
	}

	public Long queryIdByDomain(String domain) {
		WoDomain d = this.queryDomainByName(domain);
		if (d != null)
			return d.getId();

		return null;
	}

	public List<WoDomainResource> queryDynRoutesByDomain(String domain) {
		return this.domainResService.queryDomainDynamicRoutes(this.queryIdByDomain(domain));
	}
	
	public List<Long> queryTermTypeIdsByDomain(String domain) {

		String key = RedisKeyEnum.WLDOS_TERM.toString() + domain;
		String value = ObjectUtil.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				WoDomain dom = this.queryDomainByName(domain);
				List<WoDomainResource> dRes = this.domainResService.queryDomainResWithTerm(dom.getId());

				List<Long> dResIds = dRes.parallelStream().map(WoDomainResource::getTermTypeId).collect(Collectors.toList());

				value = om.writeValueAsString(dResIds);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return dResIds;
			}

			return om.readValue(value, new TypeReference<List<Long>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取域的业务分类id异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}

	public PageableResult<DomainResource> queryDomainRes(Long domainId, PageQuery pageQuery) {
		pageQuery.appendParam("isValid", ValidStatusEnum.VALID.toString());
		pageQuery.appendParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		PageableResult<DomainResource> res = this.domainResService.execQueryForPage(DomainResource.class, WoResource.class,
				WoDomainResource.class, pageQuery, false, "wo_resource", "wo_domain_resource", "resource_id");
		List<DomainResource> resList = res.getData().getRows();

		List<WoDomainResource> domainRes = this.domainResService.queryDomainRes(domainId);
		Map<Long, WoDomainResource> dResMap = domainRes.parallelStream().collect(Collectors.toMap(WoDomainResource::getResourceId, w -> w));

		resList = resList.parallelStream().map(r -> {
			Long id = r.getId();
			WoDomainResource dRes = dResMap.get(id);
			r.setDomResId(dRes.getId());
			r.setDomainId(domainId);
			r.setModuleName(dRes.getModuleName());
			r.setTermTypeId(dRes.getTermTypeId());
			return r;
		}).collect(Collectors.toList());

		res.setDataRows(resList);
		return res;
	}

	public List<WoDomainResource> queryDomainRes(Long domainId) {
		return this.domainResService.queryDomainRes(domainId);
	}

	public void domainResConf(WoDomainResource dRes) {
		this.domainResService.update(dRes);
	}
}
