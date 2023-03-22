/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.common.dto.SQLTable;
import com.wldos.common.utils.TreeUtils;
import com.wldos.support.term.dto.Term;
import com.wldos.sys.base.entity.WoDomain;
import com.wldos.sys.base.entity.WoDomainApp;
import com.wldos.sys.base.entity.WoDomainResource;
import com.wldos.support.resource.entity.WoResource;
import com.wldos.sys.base.repo.DomainResourceRepo;
import com.wldos.common.Constants;
import com.wldos.base.entity.EntityAssists;
import com.wldos.common.res.PageableResult;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.base.service.BaseService;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;
import com.wldos.sys.base.repo.DomainRepo;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.sys.base.repo.TermRepo;
import com.wldos.support.domain.vo.DomainResource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域管理service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class DomainService extends BaseService<DomainRepo, WoDomain, Long> {
	@Value("${wldos.platform.logo.default}")
	private String defaultLogo;
	@Value("${wldos.platform.favicon.default}")
	private String defaultFavicon;

	private final DomainAppService domainAppService;

	private final DomainResourceRepo domainResRepo;

	private final TermRepo termRepo;

	public DomainService(DomainAppService domainAppService, DomainResourceRepo domainResRepo, TermRepo termRepo) {
		this.domainAppService = domainAppService;
		this.domainResRepo = domainResRepo;
		this.termRepo = termRepo;
	}

	/**
	 * 批量给域添加应用
	 *
	 * @param appIds 应用id
	 * @param domainId 域id
	 * @param comId 公司id
	 * @param curUserId 当前用户id
	 * @param uip 用户ip
	 * @return 反馈信息
	 */
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

	/**
	 * 批量给域添加资源
	 *
	 * @param resIds 资源id
	 * @param domainId 域id
	 * @return 反馈消息
	 */
	public String domainRes(List<String> resIds, Long domainId, Long curUserId, String uip) {
		StringBuilder mes = new StringBuilder(50);
		List<WoDomainResource> domainResList = resIds.parallelStream().map(item -> {
			Long resId = Long.parseLong(item);
			WoDomainResource domainRes = this.domainResRepo.queryDomResById(resId, domainId);
			if (domainRes == null || domainRes.getResourceId() == null) {
				mes.append("资源：").append(resId).append(" 已存在; ");
				return null;
			}
			EntityAssists.beforeInsert(domainRes, this.nextId(), curUserId, uip, true);
			return domainRes;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (!domainResList.isEmpty())
			this.domainResRepo.saveAll(domainResList);

		return mes.toString();
	}

	/**
	 * 批量删除域下的应用
	 *
	 * @param ids 应用id
	 * @param domainId 域id
	 */
	public void removeDomainApp(List<Long> ids, Long domainId) {
		this.domainAppService.removeDomainApp(ids, domainId);
	}

	/**
	 * 批量删除域下的资源
	 *
	 * @param ids 资源id
	 * @param domainId 域id
	 */
	public void removeDomainRes(List<Long> ids, Long domainId) {
		this.domainResRepo.removeDomainRes(ids, domainId);
	}

	/**
	 * 获取所有域名ID，支持缓存
	 *
	 * @return 域名列表
	 */
	public Map<String, Long> queryAllDomainID() {
		String key = RedisKeyEnum.WLDOS_DOMAIN.toString() + ":ID";
		String value = ObjectUtils.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<WoDomain> domains = this.queryAllDomain();
				if (ObjectUtils.isBlank(domains)) { // 域名为空时，默认平台主域名并初始化
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

				Map<String, Long> domInfo = domains.parallelStream().collect(Collectors.toMap(WoDomain::getSiteDomain, WoDomain::getId)); // 主域名
				if (ObjectUtils.isBlank(domInfo))
					return domInfo;
				// 暂不考虑用个性域名等二级域名的分站处理逻辑，二级域名视同顶级域名，仅按顶级域名管理，获取顶级域名的统一方法见DomainUtil
				value = om.writeValueAsString(domInfo);
				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return domInfo;
			}

			return om.readValue(value, new TypeReference<Map<String, Long>>() {});
		}
		catch (JsonProcessingException e) {
			this.getLog().error("json解析异常={} {}", value, e.getMessage());
			return null;
		}
	}

	/**
	 * 获取所有域名实例，支持缓存
	 *
	 * @return 域名列表
	 */
	public List<WoDomain> queryAllDomain() {
		String key = RedisKeyEnum.WLDOS_DOMAIN.toString();
		String value = ObjectUtils.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<WoDomain> domains = this.entityRepo.findAllByDeleteFlagAndIsValid(DeleteFlagEnum.NORMAL.toString(), ValidStatusEnum.VALID.toString());

				if (ObjectUtils.isBlank(domains))
					return new ArrayList<>();
				// 暂不考虑用个性域名等二级域名的分站处理逻辑，二级域名视同顶级域名，仅按顶级域名管理，获取顶级域名的统一方法见DomainUtil
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

	/**
	 * 刷新域名缓存
	 *
	 */
	public void refreshDomain(WoDomain domain) {
		this.cache.remove(RedisKeyEnum.WLDOS_DOMAIN.toString()); // 实例缓存
		this.cache.remove(RedisKeyEnum.WLDOS_DOMAIN.toString() + ":ID"); // id缓存
		this.cache.remove(this.domainResRepo.queryDomainDynamicRoutesCacheKey(domain.getId())); // 各域资源缓存，见DomainResourceServer
		this.cache.remove(RedisKeyEnum.WLDOS_TERM.toString() + domain.getSiteDomain()); // 删除域的分类缓存
	}

	/**r
	 * 根据域名取回域信息，同时处理logo
	 *
	 * @param domain 域名
	 * @return 域信息
	 */
	public WoDomain findByDomain(String domain) {
		List<WoDomain> domains = this.queryAllDomain();
		for (WoDomain d : domains) {
			if (d.getSiteDomain().equals(domain)) {
				d.setSiteLogo(ObjectUtils.string(this.getDomainLogo(d.getSiteLogo())));
				d.setFavicon(ObjectUtils.string(this.getDomainFavicon(d.getFavicon())));
				return d;
			}
			String cnameDomain = d.getCnameDomain();
			if (ObjectUtils.isBlank(cnameDomain))
				continue;
			String[] cname = cnameDomain.split(",");
			if (Arrays.asList(cname).contains(domain)) { // 根据别名域名返回主域名
				d.setSiteLogo(ObjectUtils.string(this.getDomainLogo(d.getSiteLogo())));
				d.setFavicon(ObjectUtils.string(this.getDomainFavicon(d.getFavicon())));
				return d;
			}
		}

		return null;
	}

	/**
	 * 根据请求域名取回对应的主域信息，不处理logo
	 *
	 * @param domain 请求头域名
	 * @param isExcludeUri 是否token认证排除uri，如果不需要登录，默认取根域名
	 * @return 域信息
	 */
	public WoDomain queryDomainByName(String domain, boolean isExcludeUri) {
		List<WoDomain> domains = this.queryAllDomain();
		for (WoDomain d : domains) { // 主域名、个性域名和别名 都匹配到主域名
			if (d.getSiteDomain().equals(domain) || ("www." + d.getSiteDomain()).equals(domain) ||
					(d.getSecondDomain()+"."+this.getPlatDomain()).equals(domain) || (","+d.getCnameDomain()+",").contains(","+domain+",")) {
				return d;
			}
		}

		if (!isExcludeUri) {
			return null;
		}

		// 返回默认域名
		return this.queryDomainByName(this.wldosDomain, true);
	}

	/**
	 * 是否非法域名，不在域名白名单或者不是白名单域名的别名
	 *
	 * @param domain 请求域名
	 * @return 是否非法
	 */
	public boolean isInValidDomain(String domain) {
		return this.queryDomainByName(domain, false) == null;
	}

	/**
	 * 获取域配置的logo url
	 *
	 * @param logoPath 在文件服务器上的真实路径
	 * @return url
	 */
	public String getDomainLogo(String logoPath) {
		return this.store.getFileUrl(logoPath, this.store.getFileUrl(this.defaultLogo, null));
	}

	/**
	 * 获取域配置的favicon url
	 *
	 * @param iconPath 在文件服务器上的真实路径
	 * @return url
	 */
	public String getDomainFavicon(String iconPath) {
		return this.store.getFileUrl(iconPath, this.store.getFileUrl(this.defaultFavicon, null));
	}

	/**
	 * 对域名列表分页处理后再返回
	 *
	 * @param pageQuery 分页查询参数
	 * @return 分页列表
	 */
	public PageableResult<WoDomain> queryDomainList(PageQuery pageQuery) {
		PageableResult<WoDomain> domainPage = this.execQueryForPage(new WoDomain(), pageQuery);
		List<WoDomain> domains = domainPage.getData().getRows();

		domains = domains.parallelStream().peek(d -> {
			String logo = d.getSiteLogo();
			if (!ObjectUtils.isBlank(logo))
				d.setSiteLogo(this.store.getFileUrl(logo, null));
			String favicon = d.getFavicon();
			if (!ObjectUtils.isBlank(favicon))
				d.setFavicon(this.store.getFileUrl(favicon, null));
		}).collect(Collectors.toList());

		domainPage.setDataRows(domains);

		return domainPage;
	}

	/**
	 * 根据域名返回域id
	 *
	 * @param domain 域名
	 * @return 域id
	 */
	public Long queryIdByDomain(String domain) {
		return this.queryAllDomainID().get(domain);
	}

	/**
	 * 查询指定域所有动态路由所拥有的分类的id
	 *
	 * @param domain 域名
	 * @return 分类id列表
	 */
	public List<Long> queryTermTypeIdsByDomain(String domain) { // 加二级缓存

		String key = RedisKeyEnum.WLDOS_TERM.toString() + domain;
		String value = ObjectUtils.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				Long domId = this.queryIdByDomain(domain);
				List<WoDomainResource> dRes = this.domainResRepo.queryDomainResWithTerm(domId);

				List<Long> dResIds = dRes.parallelStream().map(WoDomainResource::getTermTypeId).collect(Collectors.toList());

				if (ObjectUtils.isBlank(dResIds))
					return new ArrayList<>();
				// 暂不考虑用个性域名等二级域名的分站处理逻辑，二级域名视同顶级域名，仅按顶级域名管理，获取顶级域名的统一方法见DomainUtil
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

	/**
	 * 查询域关联的资源清单
	 *
	 * @param domainId 域id
	 * @param pageQuery 查询参数
	 * @return 域资源分页列表
	 */
	public PageableResult<DomainResource> queryDomainRes(Long domainId, PageQuery pageQuery) {
		pageQuery.appendParam("isValid", ValidStatusEnum.VALID.toString());
		pageQuery.appendParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		pageQuery.pushSort(new String[][]{{"s.resource_path"}, {"s.parent_id | s.display_order"}}); // 特殊排序
		String sqlNoWhere = "select r.id, r.domain_id, r.module_name, r.term_type_id, s.id resource_id, s.resource_code, "
				+ "s.resource_name, s.resource_path, s.icon, s.resource_type, s.request_method, s.target, s.app_id, s.parent_id, s.remark, s.is_valid from wo_domain_resource r "
				+ "join wo_resource s on r.app_id=s.app_id and r.resource_id = s.id";
		List<DomainResource> resList = this.commonOperate.execQueryForList(DomainResource.class, sqlNoWhere, pageQuery,
				new SQLTable[]{new SQLTable("wo_domain_resource", "r", WoDomainResource.class),
						new SQLTable("wo_resource", "s", WoResource.class)});

		List<Long> termTypeIdsByDomain = this.queryTermTypeIdsByDomain(this.findById(domainId).getSiteDomain());
		Map<Long, String> termNames = new HashMap<>();
		termNames.put(0L, "-"); // 未关联分类的是0
		if (!ObjectUtils.isBlank(termTypeIdsByDomain)) {
			List<Term> terms = this.termRepo.queryAllTermsByTermTypeIds(termTypeIdsByDomain);
			termNames.putAll(terms.stream().collect(Collectors.toMap(Term::getTermTypeId, Term::getName)));
		}
		resList = resList.parallelStream().map(r -> {
			r.setTermName(termNames.get(r.getTermTypeId()));
			return r;
		}).collect(Collectors.toList());

		// 生成树, 菜单暂不支持定制树，继承资源池设置的树结构
		TreeUtils.build(resList, Constants.MENU_ROOT_ID);
		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		int total = resList.size();
		return new PageableResult<>(total, currentPage, pageSize, resList);
	}

	public void domainResConf(WoDomainResource dRes) {
		this.commonOperate.dynamicUpdateByEntity(dRes);
	}
}
