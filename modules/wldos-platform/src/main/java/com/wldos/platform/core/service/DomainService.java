/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.dto.SQLTable;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.RedisKeyEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.common.utils.TreeUtils;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.DomainAdminDao;
import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.UserDao;
import com.wldos.platform.core.dao.AppDao;
import com.wldos.platform.core.dao.AuthRoleDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.entity.WoAuthRole;
import com.wldos.platform.core.entity.WoApp;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.support.AppCatalogPolicy;
import com.wldos.platform.core.support.SiteNavDirSupport;
import com.wldos.platform.core.vo.SiteNavDirBody;
import com.wldos.platform.core.vo.SiteNavDirResult;
import com.wldos.platform.core.vo.DomainAdminVO;
import com.wldos.platform.core.vo.DomainMountVisibilityBody;
import com.wldos.platform.core.vo.ResTreeOverlayBody;
import com.wldos.platform.core.vo.ResTreeOverlayItem;
import com.wldos.platform.core.dao.DomainResourceDao;
import com.wldos.platform.core.dao.TermDao;
import com.wldos.platform.core.entity.WoDomainAdmin;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoDomainApp;
import com.wldos.platform.core.entity.WoDomainResource;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoUser;
import io.github.wldos.platform.support.domain.vo.DomainResource;
import io.github.wldos.platform.support.resource.entity.WoResource;
import io.github.wldos.platform.support.resource.enums.MenuNodeKindEnum;
import io.github.wldos.platform.support.resource.enums.ResourceEnum;
import io.github.wldos.platform.support.rbac.ResourceScopeLevel;
import io.github.wldos.platform.support.term.dto.Term;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 域管理service。
 *
 * @author Yuanxi Universe
 * @date 2021/4/28
 * @version 1.0
 */
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class DomainService extends EntityService<DomainDao, WoDomain, Long> {
	@Value("${wldos_platform_logo_default:}")
	private String defaultLogo;

	@Value("${wldos_platform_favicon_default:}")
	private String defaultFavicon;

	private final DomainAppService domainAppService;

	private final DomainResourceDao domainResRepo;

	private final TermDao termRepo;

	private final DomainAdminDao domainAdminDao;

	private final OrgService orgService;

	private final OrgDao orgDao;

	private final DomainBootstrap domainBootstrap;

	private final ResourceService resourceService;

	private final AppDao appDao;

	private final AuthRoleDao authRoleDao;

	private final RoleDao roleDao;

	private final ResourceAuthReferenceGuard resourceAuthReferenceGuard;

	private final PlatformOperatorSettingsService platformOperatorSettingsService;

	private final ProvisionScenarioService provisionScenarioService;

	private final UserDao userDao;

	public DomainService(DomainAppService domainAppService, DomainResourceDao domainResRepo, TermDao termRepo,
	                     DomainAdminDao domainAdminDao, OrgService orgService, OrgDao orgDao,
	                     DomainBootstrap domainBootstrap, @Lazy ResourceService resourceService, AppDao appDao,
	                     AuthRoleDao authRoleDao, RoleDao roleDao,
	                     ResourceAuthReferenceGuard resourceAuthReferenceGuard,
	                     @Lazy PlatformOperatorSettingsService platformOperatorSettingsService,
	                     @Lazy ProvisionScenarioService provisionScenarioService,
	                     UserDao userDao) {
		this.domainAppService = domainAppService;
		this.domainResRepo = domainResRepo;
		this.termRepo = termRepo;
		this.domainAdminDao = domainAdminDao;
		this.orgService = orgService;
		this.orgDao = orgDao;
		this.domainBootstrap = domainBootstrap;
		this.resourceService = resourceService;
		this.appDao = appDao;
		this.authRoleDao = authRoleDao;
		this.roleDao = roleDao;
		this.resourceAuthReferenceGuard = resourceAuthReferenceGuard;
		this.platformOperatorSettingsService = platformOperatorSettingsService;
		this.provisionScenarioService = provisionScenarioService;
		this.userDao = userDao;
	}

	/**
	 * 删除前校验：运营主站、入驻模板站、仍有会员的站点不可删。
	 */
	public void assertDeletable(WoDomain domain) {
		if (domain == null || domain.getId() == null) {
			throw new BaseException("站点不存在", ResultCode.OPERATION_FAILED.getCode());
		}
		Long domainId = domain.getId();
		Optional<Long> primaryId = this.platformOperatorSettingsService.findPrimaryDomainId();
		if (primaryId.isPresent() && primaryId.get().equals(domainId)) {
			throw new BaseException("平台运营主站不可删除", ResultCode.OPERATION_FAILED.getCode());
		}
		if (this.provisionScenarioService.isRegisteredTemplateDomain(domainId)) {
			throw new BaseException("该站点为租户入驻克隆模板，不可删除", ResultCode.OPERATION_FAILED.getCode());
		}
		if (this.userDao.countNormalUsersByDomainId(domainId) > 0) {
			throw new BaseException("站点下仍有会员/用户，请先迁移或删除用户后再删站点", ResultCode.OPERATION_FAILED.getCode());
		}
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
		List<WoDomainApp> domainAppList = new ArrayList<>();
		boolean superAdminViewer = curUserId != null && this.isAdmin(curUserId);
		for (String item : appIds) {
			Long appId = Long.parseLong(item);
			WoApp app = this.appDao.findById(appId).orElse(null);
			AppCatalogPolicy.assertDomainMountAllowed(app, superAdminViewer);
			WoDomainApp existing = this.domainAppService.findMount(domainId, appId);
			if (existing != null) {
				if (ValidStatusEnum.VALID.toString().equals(existing.getIsValid())) {
					mes.append("应用：").append(appId).append(" 已存在; ");
					continue;
				}
				existing.setIsValid(ValidStatusEnum.VALID.toString());
				existing.setComId(comId);
				this.domainAppService.saveOrUpdate(existing);
				continue;
			}
			WoDomainApp domainApp = new WoDomainApp();
			domainApp.setDomainId(domainId);
			domainApp.setAppId(appId);
			domainApp.setComId(comId);
			domainAppList.add(domainApp);
		}
		if (!domainAppList.isEmpty()) {
			this.domainAppService.saveAll(domainAppList);
		}
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
			WoResource resource = this.resourceService.findById(resId);
			this.assertSiteNavDirMountAllowed(resource, domainId);
			WoDomainResource domainRes = this.domainResRepo.queryDomResById(resId, domainId);
			if (domainRes == null || domainRes.getResourceId() == null) {
				mes.append("资源：").append(resId).append(" 已存在; ");
				return null;
			}
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
	private Map<String, Long> queryAllDomainID() {
		String key = RedisKeyEnum.WLDOS_DOMAIN + ":ID";
		String value = ObjectUtils.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<WoDomain> domains = this.queryAllDomain();
				if (ObjectUtils.isBlank(domains)) {
					Long operatorComId = this.platformOperatorSettingsService.findOperatorComId().orElse(null);
					if (operatorComId == null) {
						return new HashMap<>();
					}
					WoDomain domain = new WoDomain();
					domain.setComId(operatorComId);
					domain.setSecondDomain(this.getPlatDomain());
					domain.setSiteDomain(this.getPlatDomain());
					domain.setDisplayOrder(1L);
					domain.setSiteDescription("平台主站");
					domain.setSiteUrl("http://" + this.getPlatDomain());
					this.saveOrUpdate(domain);
					domains = new ArrayList<>();
					domains.add(domain);
					this.domainBootstrap.ensureSiteOperationalSeed(domain.getComId(), domain.getId());
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

	/**
	 * 站点创建后生成内置会员分群/角色及初始授权与挂载。
	 */
	public void provisionSystemKinds(WoDomain domain) {
		if (domain == null || domain.getId() == null) {
			return;
		}
		Long comId = resolveDomainComIdForKindSeed(domain);
		if (comId == null) {
			return;
		}
		this.domainBootstrap.ensureSiteOperationalSeed(comId, domain.getId());
	}

	public void provisionSystemKindSeed(WoDomain domain) {
		if (domain == null || domain.getId() == null) {
			return;
		}
		Long comId = resolveDomainComIdForKindSeed(domain);
		if (comId == null) {
			return;
		}
		this.domainBootstrap.ensureSiteKindSeed(comId, domain.getId());
	}

	private Long resolveDomainComIdForKindSeed(WoDomain domain) {
		return domain.getComId();
	}

	public void assertSiteMountsReady(Long domainId) {
		this.domainBootstrap.assertSiteMountsReady(domainId);
	}

	public void assertSiteMountsReady(Long domainId, Long templateDomainId, String templateSiteDomain) {
		this.domainBootstrap.assertSiteMountsReady(domainId, templateDomainId, templateSiteDomain);
	}

	public WoDomain findFirstByComId(Long comId) {
		if (comId == null || comId <= 0) {
			return null;
		}
		return this.entityRepo.findFirstByComId(comId);
	}

	public int countValidByComId(Long comId) {
		if (comId == null || comId <= 0) {
			return 0;
		}
		return this.entityRepo.countValidByComId(comId);
	}

	/**
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
					(d.getSecondDomain() + "." + this.getPlatDomain()).equals(domain) || ("," + d.getCnameDomain() + ",").contains("," + domain + ",")) {
				return d;
			}
		}

		if (!isExcludeUri) {
			return null;
		}

		// 返回默认域名
		return this.queryDomainByName(this.getPlatDomain(), true);
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
		return this.store.getPublicUrl(logoPath, this.store.getPublicUrl(this.defaultLogo, null));
	}

	/**
	 * 获取域配置的favicon url
	 *
	 * @param iconPath 在文件服务器上的真实路径
	 * @return url
	 */
	public String getDomainFavicon(String iconPath) {
		return this.store.getPublicUrl(iconPath, this.store.getPublicUrl(this.defaultFavicon, null));
	}

	/**
	 * 对域名列表分页处理后再返回
	 *
	 * @param pageQuery 分页查询参数
	 * @return 分页列表
	 */
	public PageData<WoDomain> queryDomainList(PageQuery pageQuery) {
		PageData<WoDomain> domainPage = this.execQueryForPage(new WoDomain(), pageQuery);
		List<WoDomain> domains = domainPage.getRows();

		domains = domains.parallelStream().peek(d -> {
			String logo = d.getSiteLogo();
			if (!ObjectUtils.isBlank(logo))
				d.setSiteLogo(this.store.getPublicUrl(logo, null));
			String favicon = d.getFavicon();
			if (!ObjectUtils.isBlank(favicon))
				d.setFavicon(this.store.getPublicUrl(favicon, null));
		}).collect(Collectors.toList());

		domainPage.setRows(domains);

		return domainPage;
	}

	/**
	 * 查询指定域所有动态路由所拥有的分类的id
	 *
	 * @param domain 站点域名，不带www的siteDomain，否则会出现匹配不上数据！！！
	 * @return 分类id列表
	 */
	public List<Long> queryTermTypeIdsByDomain(String domain) { // 加二级缓存

		String key = RedisKeyEnum.WLDOS_TERM.toString() + domain;
		String value = ObjectUtils.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				Long domId = this.queryAllDomainID().get(domain);
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
	public PageData<DomainResource> queryDomainRes(Long domainId, PageQuery pageQuery) {
		pageQuery.appendParam("domainId", domainId);
		List<Object> mountFilter = pageQuery.pollFilter("mountValid");
		pageQuery.appendParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		pageQuery.pushSort(new String[][] { { "s.resource_path" }, { "coalesce(s.parent_id, 0)" }, { "s.display_order" } }); // parent_id 可能为空，用 coalesce 保证排序有效
		StringBuilder sqlNoWhere = new StringBuilder("select r.id, r.domain_id, r.module_name, r.term_type_id, r.url, r.extra_props, r.is_valid as mount_valid, ")
				.append("s.id resource_id, s.resource_code, ")
				.append("s.resource_name, s.resource_path, s.icon, s.resource_type, s.request_method, s.target, s.app_id, s.parent_id, s.remark, s.is_valid from wo_domain_resource r ")
				.append("join wo_resource s on r.app_id=s.app_id and r.resource_id = s.id");
		this.appendDomainResourceMountValidSql(sqlNoWhere, mountFilter);
		List<DomainResource> resList = this.commonOperate.execQueryForList(DomainResource.class, sqlNoWhere.toString(), pageQuery,
				SQLTable.of(WoDomainResource.class, "r"), SQLTable.of(WoResource.class, "s"));

		List<Long> termTypeIdsByDomain = this.queryTermTypeIdsByDomain(this.findById(domainId).getSiteDomain());
		Map<Long, String> termNames = new HashMap<>();
		termNames.put(0L, "-"); // 未关联分类的是0
		if (!ObjectUtils.isBlank(termTypeIdsByDomain)) {
			List<Term> terms = this.termRepo.queryAllTermsByTermTypeIds(termTypeIdsByDomain);
			termNames.putAll(terms.stream().collect(Collectors.toMap(Term::getTermTypeId, Term::getName)));
		}
		resList = resList.parallelStream().peek(r -> r.setTermName(termNames.get(r.getTermTypeId()))).collect(Collectors.toList());

		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		int total = resList.size();
		List<DomainResource> tree = TreeUtils.build(resList, Constants.MENU_ROOT_ID);
		return new PageData<>(total, currentPage, pageSize, tree);
	}

	public void domainResConf(WoDomainResource dRes) {
		if (dRes != null && dRes.getDomainId() != null && dRes.getResourceId() != null) {
			this.validateOverlayFields(dRes.getDomainId(), dRes.getResourceId(), dRes.getOverlayParentId());
		}
		this.commonOperate.dynamicUpdateByEntity(dRes, true);
	}

	/**
	 * 查询本站已挂载 menu 资源的 overlay 编辑树（effective parent/order/label）。
	 */
	public List<DomainResource> querySiteMenuTree(Long domainId) {
		PageQuery pageQuery = new PageQuery();
		pageQuery.appendParam("domainId", domainId);
		pageQuery.appendParam("isValid", ValidStatusEnum.VALID.toString());
		pageQuery.appendParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		pageQuery.setCurrent(1);
		pageQuery.setPageSize(10000);
		pageQuery.pushSort(new String[][] { { "coalesce(r.overlay_parent_id, s.parent_id, 0)" }, { "coalesce(r.overlay_display_order, s.display_order)" } });
		StringBuilder sqlNoWhere = new StringBuilder("select r.id, r.domain_id, r.resource_id, s.resource_code, s.resource_name global_resource_name, ")
				.append("coalesce(r.overlay_label, s.resource_name) resource_name, s.resource_path, s.component_path, s.icon, ")
				.append("s.resource_type, s.request_method, s.target, s.app_id, s.menu_node_kind, s.scope_level, ")
				.append("coalesce(r.overlay_parent_id, s.parent_id) parent_id, ")
				.append("coalesce(r.overlay_display_order, s.display_order) display_order, ")
				.append("r.overlay_parent_id, r.overlay_display_order, r.overlay_label ")
				.append("from wo_domain_resource r join wo_resource s on r.app_id=s.app_id and r.resource_id=s.id ")
				.append("and s.is_valid='1' and s.delete_flag='normal' ")
				.append("and s.resource_type in ('admin_menu','admin_plugin_menu','menu','plugin_menu') ");
		List<DomainResource> nodes = this.commonOperate.execQueryForList(DomainResource.class, sqlNoWhere.toString(), pageQuery,
				SQLTable.of(WoDomainResource.class, "r"), SQLTable.of(WoResource.class, "s"));
		if (ObjectUtils.isBlank(nodes)) {
			return Collections.emptyList();
		}
		for (DomainResource node : nodes) {
			if (node.getGlobalResourceName() == null) {
				node.setGlobalResourceName(node.getResourceName());
			}
		}
		return TreeUtils.build(nodes, Constants.MENU_ROOT_ID);
	}

	/**
	 * 批量保存站点菜单 overlay（事务 + 环检测 + 父必须已挂载）。
	 */
	@Transactional
	public void saveResTreeOverlay(ResTreeOverlayBody body) {
		if (body == null || body.getDomainId() == null || ObjectUtils.isBlank(body.getItems())) {
			return;
		}
		Long domainId = body.getDomainId();
		Map<Long, WoDomainResource> mountByResId = this.domainResRepo.queryDomainRes(domainId).stream()
				.filter(row -> row.getResourceId() != null)
				.collect(Collectors.toMap(WoDomainResource::getResourceId, row -> row, (a, b) -> a));
		Map<Long, Long> pendingParent = new HashMap<>();
		for (ResTreeOverlayItem item : body.getItems()) {
			if (item == null || item.getResourceId() == null) {
				continue;
			}
			WoDomainResource mount = mountByResId.get(item.getResourceId());
			if (mount == null) {
				throw new BaseException("资源未在本站挂载: " + item.getResourceId(), ResultCode.VALIDATION_ERROR.getCode());
			}
			Long parentId = normalizeOverlayParentId(item.getOverlayParentId());
			this.validateOverlayFields(domainId, item.getResourceId(), parentId);
			pendingParent.put(item.getResourceId(), parentId);
		}
		this.assertOverlayNoCycle(mountByResId, pendingParent);
		for (ResTreeOverlayItem item : body.getItems()) {
			if (item == null || item.getResourceId() == null) {
				continue;
			}
			WoDomainResource mount = mountByResId.get(item.getResourceId());
			mount.setOverlayParentId(normalizeOverlayParentId(item.getOverlayParentId()));
			mount.setOverlayDisplayOrder(item.getOverlayDisplayOrder());
			mount.setOverlayLabel(ObjectUtils.isBlank(item.getOverlayLabel()) ? null : item.getOverlayLabel().trim());
			this.commonOperate.dynamicUpdateByEntity(mount, true);
		}
	}

	/**
	 * 站点自建 nav_dir：写 wo_resource + 本站挂载 + 首授 + refreshAuth。
	 */
	@Transactional
	public SiteNavDirResult createSiteNavDir(SiteNavDirBody body, Long operatorId, String operatorIp) {
		if (body == null || body.getDomainId() == null || ObjectUtils.isBlank(body.getLabel())) {
			throw new BaseException("站点与分组名称不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long domainId = body.getDomainId();
		WoDomain domain = this.findById(domainId);
		if (domain == null) {
			throw new BaseException("站点不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoApp adminApp = this.appDao.findByAppCode(AppCatalogPolicy.SITE_ESSENTIAL_PLATFORM_APP_CODE);
		if (adminApp == null || adminApp.getId() == null) {
			throw new BaseException("系统管理应用未就绪", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long overlayParentId = normalizeOverlayParentId(body.getOverlayParentId());
		if (overlayParentId != null) {
			this.validateOverlayFields(domainId, null, overlayParentId);
		}

		Long resourceId = this.nextId();
		String label = body.getLabel().trim();
		WoResource resource = new WoResource();
		resource.setId(resourceId);
		resource.setResourceCode("site_nav_" + domainId + "_" + resourceId);
		resource.setResourceName(label);
		resource.setResourcePath("");
		resource.setComponentPath("");
		resource.setIcon("folder");
		resource.setResourceType(ResourceEnum.ADMIN_MENU.getValue());
		resource.setRequestMethod("GET");
		resource.setTarget("_self");
		resource.setAppId(adminApp.getId());
		resource.setParentId(Constants.MENU_ROOT_ID);
		resource.setDisplayOrder(body.getOverlayDisplayOrder() != null ? body.getOverlayDisplayOrder().longValue() : 0L);
		resource.setMenuNodeKind(MenuNodeKindEnum.NAV_DIR.getValue());
		resource.setScopeLevel(ResourceScopeLevel.DOMAIN.getCode());
		resource.setIsValid(ValidStatusEnum.VALID.toString());
		resource.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		resource.setCreateBy(operatorId);
		resource.setCreateIp(operatorIp);
		resource.setUpdateBy(operatorId);
		resource.setUpdateIp(operatorIp);
		this.resourceService.saveOrUpdate(resource);

		WoDomainResource mount = new WoDomainResource();
		mount.setId(this.nextId());
		mount.setModuleName("static");
		mount.setResourceId(resourceId);
		mount.setAppId(adminApp.getId());
		mount.setTermTypeId(0L);
		mount.setDomainId(domainId);
		mount.setOverlayParentId(overlayParentId);
		mount.setOverlayDisplayOrder(body.getOverlayDisplayOrder());
		mount.setOverlayLabel(label);
		mount.setIsValid(ValidStatusEnum.VALID.toString());
		mount.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		mount.setCreateBy(operatorId);
		mount.setCreateIp(operatorIp);
		mount.setUpdateBy(operatorId);
		mount.setUpdateIp(operatorIp);
		this.domainResRepo.save(mount);

		this.grantSiteNavDirFirstAuth(domain, resource, operatorId, operatorIp);
		this.refreshAuth();

		return new SiteNavDirResult(resourceId, mount.getId());
	}

	/**
	 * 更新站点自建 nav_dir（名称与 overlay 摆位）。
	 */
	@Transactional
	public SiteNavDirResult updateSiteNavDir(SiteNavDirBody body, Long operatorId, String operatorIp) {
		if (body == null || body.getDomainId() == null || body.getResourceId() == null || ObjectUtils.isBlank(body.getLabel())) {
			throw new BaseException("站点、资源与分组名称不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long domainId = body.getDomainId();
		Long resourceId = body.getResourceId();
		WoResource resource = this.requireSiteOwnedNavDir(domainId, resourceId);
		List<WoDomainResource> mountRows = this.domainResRepo.queryDomainRes(domainId, Collections.singletonList(resourceId));
		if (ObjectUtils.isBlank(mountRows)) {
			throw new BaseException("分组未在本站挂载", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoDomainResource mount = mountRows.get(0);
		Long overlayParentId = normalizeOverlayParentId(body.getOverlayParentId());
		this.validateOverlayFields(domainId, resourceId, overlayParentId);
		Map<Long, WoDomainResource> mountByResId = this.domainResRepo.queryDomainRes(domainId).stream()
				.filter(row -> row.getResourceId() != null)
				.collect(Collectors.toMap(WoDomainResource::getResourceId, row -> row, (a, b) -> a));
		Map<Long, Long> pendingParent = new HashMap<>();
		pendingParent.put(resourceId, overlayParentId);
		this.assertOverlayNoCycle(mountByResId, pendingParent);

		String label = body.getLabel().trim();
		resource.setResourceName(label);
		if (body.getOverlayDisplayOrder() != null) {
			resource.setDisplayOrder(body.getOverlayDisplayOrder().longValue());
		}
		resource.setUpdateBy(operatorId);
		resource.setUpdateIp(operatorIp);
		this.resourceService.saveOrUpdate(resource);

		mount.setOverlayParentId(overlayParentId);
		mount.setOverlayDisplayOrder(body.getOverlayDisplayOrder());
		mount.setOverlayLabel(label);
		mount.setUpdateBy(operatorId);
		mount.setUpdateIp(operatorIp);
		this.commonOperate.dynamicUpdateByEntity(mount, true);
		this.refreshAuth();
		return new SiteNavDirResult(resourceId, mount.getId());
	}

	/**
	 * 删除站点自建 nav_dir（无子节点且无角色授权时）。
	 */
	@Transactional
	public void deleteSiteNavDir(Long domainId, Long resourceId, Long operatorId, String operatorIp) {
		if (domainId == null || resourceId == null) {
			throw new BaseException("站点与资源不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoResource resource = this.requireSiteOwnedNavDir(domainId, resourceId);
		this.assertSiteNavDirHasNoMenuChildren(domainId, resourceId);
		this.resourceAuthReferenceGuard.assertDeletable(resourceId, resource.getAppId(), resource.getResourceName());
		this.removeDomainRes(Collections.singletonList(resourceId), domainId);
		resource.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
		resource.setUpdateBy(operatorId);
		resource.setUpdateIp(operatorIp);
		this.resourceService.saveOrUpdate(resource);
		this.refreshAuth();
	}

	private WoResource requireSiteOwnedNavDir(Long domainId, Long resourceId) {
		WoResource resource = this.resourceService.findById(resourceId);
		if (resource == null || !SiteNavDirSupport.isSiteOwnedNavDir(resource)) {
			throw new BaseException("仅可操作本站自建菜单分组", ResultCode.VALIDATION_ERROR.getCode());
		}
		List<Long> mountedDomains = this.domainResRepo.listDomainIdsByResourceId(resourceId);
		if (ObjectUtils.isBlank(mountedDomains) || !mountedDomains.contains(domainId)) {
			throw new BaseException("分组未在本站挂载", ResultCode.VALIDATION_ERROR.getCode());
		}
		return resource;
	}

	private void assertSiteNavDirHasNoMenuChildren(Long domainId, Long resourceId) {
		List<DomainResource> tree = this.querySiteMenuTree(domainId);
		if (ObjectUtils.isBlank(tree)) {
			return;
		}
		List<DomainResource> flat = this.flattenSiteMenuNodes(tree);
		for (DomainResource node : flat) {
			if (node == null || node.getResourceId() == null) {
				continue;
			}
			if (Objects.equals(node.getResourceId(), resourceId)) {
				continue;
			}
			if (Objects.equals(node.getParentId(), resourceId)) {
				throw new BaseException("分组下仍有子菜单，请先移走或调整上级", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
	}

	private List<DomainResource> flattenSiteMenuNodes(List<DomainResource> nodes) {
		List<DomainResource> list = new ArrayList<>();
		if (ObjectUtils.isBlank(nodes)) {
			return list;
		}
		for (DomainResource node : nodes) {
			list.add(node);
			if (!ObjectUtils.isBlank(node.getChildren())) {
				list.addAll(this.flattenSiteMenuNodes(node.getChildren()));
			}
		}
		return list;
	}

	private void assertSiteNavDirMountAllowed(WoResource resource, Long targetDomainId) {
		if (!SiteNavDirSupport.isSiteOwnedNavDir(resource)) {
			return;
		}
		List<Long> mountedDomains = this.domainResRepo.listDomainIdsByResourceId(resource.getId());
		if (ObjectUtils.isBlank(mountedDomains)) {
			return;
		}
		for (Long mountedDomainId : mountedDomains) {
			if (mountedDomainId != null && !mountedDomainId.equals(targetDomainId)) {
				throw new BaseException("站点自建目录不可跨站挂载", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
	}

	private void grantSiteNavDirFirstAuth(WoDomain domain, WoResource resource, Long operatorId, String operatorIp) {
		if (domain == null || resource == null || resource.getId() == null) {
			return;
		}
		WoRole dadminTemplate = this.roleDao.findPlatformTemplateByRoleCode(Constants.DomainAdminRoleCode);
		if (dadminTemplate == null) {
			return;
		}
		Long roleId = dadminTemplate.getId();
		WoAuthRole existing = this.authRoleDao.findByRoleIdAndResourceIdAndAppId(roleId, resource.getId(), resource.getAppId());
		if (existing != null) {
			return;
		}
		WoAuthRole authRole = new WoAuthRole();
		authRole.setId(this.nextId());
		authRole.setRoleId(roleId);
		authRole.setResourceId(resource.getId());
		authRole.setAppId(resource.getAppId());
		authRole.setIsValid(ValidStatusEnum.VALID.toString());
		authRole.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		authRole.setCreateBy(operatorId);
		authRole.setCreateIp(operatorIp);
		authRole.setUpdateBy(operatorId);
		authRole.setUpdateIp(operatorIp);
		this.authRoleDao.save(authRole);
	}

	private static Long normalizeOverlayParentId(Long overlayParentId) {
		if (overlayParentId == null || overlayParentId <= 0L) {
			return null;
		}
		return overlayParentId;
	}

	private void validateOverlayFields(Long domainId, Long resourceId, Long overlayParentId) {
		if (overlayParentId == null) {
			return;
		}
		if (Objects.equals(resourceId, overlayParentId)) {
			throw new BaseException("菜单不能将自身设为上级", ResultCode.VALIDATION_ERROR.getCode());
		}
		List<WoDomainResource> parentRows = this.domainResRepo.queryDomainRes(domainId, Collections.singletonList(overlayParentId));
		if (ObjectUtils.isBlank(parentRows)) {
			throw new BaseException("上级菜单须为本站已挂载资源", ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private void assertOverlayNoCycle(Map<Long, WoDomainResource> mountByResId, Map<Long, Long> pendingParent) {
		if (pendingParent.isEmpty()) {
			return;
		}
		Map<Long, Long> effectiveParent = new HashMap<>();
		for (Map.Entry<Long, WoDomainResource> entry : mountByResId.entrySet()) {
			Long resId = entry.getKey();
			WoDomainResource mount = entry.getValue();
			Long parent = pendingParent.containsKey(resId) ? pendingParent.get(resId) : mount.getOverlayParentId();
			effectiveParent.put(resId, parent);
		}
		for (Long startId : pendingParent.keySet()) {
			Set<Long> visited = new HashSet<>();
			Long cursor = startId;
			while (cursor != null && cursor > 0L) {
				if (!visited.add(cursor)) {
					throw new BaseException("菜单布局存在环，请调整上级关系", ResultCode.VALIDATION_ERROR.getCode());
				}
				cursor = effectiveParent.get(cursor);
			}
		}
	}

	/**
	 * 查询站点绑定的域级管理员，支持分页与按昵称/账号/邮箱/状态动态查询排序。
	 *
	 * @param domainId 站点id
	 * @param pageQuery 分页查询参数
	 * @return 域管理员分页列表
	 */
	public PageData<DomainAdminVO> listDomainAdmins(Long domainId, PageQuery pageQuery) {
		if (pageQuery.getCondition() != null) {
			pageQuery.getCondition().remove("domainId");
			pageQuery.getCondition().remove("deleteFlag");
			pageQuery.getCondition().remove("isValid");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select da.id, da.domain_id, da.user_id, u.nickname, u.login_name, ");
		sql.append("u.email, u.status, u.remark from wo_domain_admin da ");
		sql.append("left join wo_user u on da.user_id = u.id ");
		sql.append("where da.domain_id = ? and da.delete_flag = 'normal' and da.is_valid = '1' ");
		List<Object> params = new ArrayList<>();
		params.add(domainId);
		// 动态条件只映射 wo_user，避免 domainId/deleteFlag 误加到 u.domain_id 导致绑定存在却列表为空
		return this.commonOperate.execQueryForPage(
				DomainAdminVO.class, sql.toString(), params, pageQuery, SQLTable.of(WoUser.class, "u"));
	}

	private void assertDomainAdminSameCom(Long userId, Long targetComId) {
		List<WoDomainAdmin> bindings = this.domainAdminDao.findByUserId(userId);
		if (ObjectUtils.isBlank(bindings)) {
			return;
		}
		for (WoDomainAdmin row : bindings) {
			if (row.getComId() != null && !row.getComId().equals(targetComId)) {
				throw new BaseException("该用户已是其他租户的站点管理员，不可跨租户绑定");
			}
		}
	}

	public void bindDomainAdmin(Long domainId, Long userId, Long comId, Long operatorId, String operatorIp) {
		if (domainId == null || userId == null) {
			return;
		}
		Long targetComId = comId != null ? comId : Constants.TOP_COM_ID;
		this.assertDomainAdminSameCom(userId, targetComId);
		WoDomainAdmin existing = this.domainAdminDao.findByDomainAndUser(domainId, userId);
		if (existing != null) {
			this.syncDomainAdminBindingRow(existing, targetComId, operatorId, operatorIp);
			this.ensureDomainAdminOrgMembership(userId, targetComId, domainId, operatorId, operatorIp);
			return;
		}
		WoDomainAdmin row = new WoDomainAdmin();
		row.setId(this.nextId());
		row.setDomainId(domainId);
		row.setUserId(userId);
		row.setComId(targetComId);
		row.setIsValid(ValidStatusEnum.VALID.toString());
		row.setCreateBy(operatorId);
		row.setCreateIp(operatorIp);
		row.setUpdateBy(operatorId);
		row.setUpdateIp(operatorIp);
		this.domainAdminDao.save(row);
		this.ensureDomainAdminOrgMembership(userId, row.getComId(), domainId, operatorId, operatorIp);
	}

	/**
	 * 批量绑定站点管理员，复用单条绑定逻辑；已绑定的用户跳过并累计提示，不中断其余用户的绑定。
	 *
	 * @return 已跳过（重复绑定）的提示信息，全部绑定成功则为空字符串
	 */
	@Transactional
	public String bindDomainAdmins(Long domainId, List<Long> userIds, Long comId, Long operatorId, String operatorIp) {
		if (domainId == null || ObjectUtils.isBlank(userIds)) {
			return "";
		}
		StringBuilder mes = new StringBuilder(50);
		for (Long userId : userIds) {
			WoDomainAdmin existing = this.domainAdminDao.findByDomainAndUser(domainId, userId);
			if (existing != null) {
				boolean alreadyActive = this.isActiveDomainAdminRow(existing);
				this.syncDomainAdminBindingRow(existing, comId != null ? comId : Constants.TOP_COM_ID, operatorId, operatorIp);
				this.ensureDomainAdminOrgMembership(
						userId, comId != null ? comId : Constants.TOP_COM_ID, domainId, operatorId, operatorIp);
				if (alreadyActive) {
					mes.append("用户：").append(userId).append(" 已是该站点管理员，跳过；");
				}
				continue;
			}
			this.bindDomainAdmin(domainId, userId, comId, operatorId, operatorIp);
		}
		return mes.toString();
	}

	private boolean isActiveDomainAdminRow(WoDomainAdmin row) {
		return row != null && ValidStatusEnum.VALID.toString().equals(row.getIsValid());
	}

	private void syncDomainAdminBindingRow(WoDomainAdmin row, Long targetComId, Long operatorId, String operatorIp) {
		if (row == null) {
			return;
		}
		boolean dirty = false;
		if (!ValidStatusEnum.VALID.toString().equals(row.getIsValid())) {
			row.setIsValid(ValidStatusEnum.VALID.toString());
			dirty = true;
		}
		if (targetComId != null && !Objects.equals(row.getComId(), targetComId)) {
			row.setComId(targetComId);
			dirty = true;
		}
		if (dirty) {
			row.setUpdateBy(operatorId);
			row.setUpdateIp(operatorIp);
			this.domainAdminDao.save(row);
		}
	}

	/**
	 * 站点管理员绑定组织成员关系，复用 {@link OrgService#userOrg} 同一套逻辑，
	 * 同时维护 wo_org_user 与 wo_com_user（含主企业 isMain 判定）——
	 * 站点管理员登录时的租户解析（{@code queryTenantInfoByTAdminId}）依赖 wo_com_user，
	 * 若只写 wo_org_user 会导致其 getTenantId() 恒回退平台租户，体系/机构等按 comId
	 * 过滤的列表全部返回空，新建的分类/标签也会被误打上平台层 comId 而无法自行编辑删除。
	 */
	private void ensureDomainAdminOrgMembership(Long userId, Long comId, Long domainId, Long operatorId, String operatorIp) {
		Long targetComId = comId != null ? comId : Constants.TOP_COM_ID;
		WoOrg dadminOrg = null;
		if (domainId != null && domainId > 0) {
			dadminOrg = this.orgDao.findByComIdAndDomainIdAndOrgCode(targetComId, domainId, Constants.DomainAdminOrgCode);
		}
		if (dadminOrg == null) {
			dadminOrg = this.orgDao.findByOrgCodeAndIsValidAndDeleteFlag(
					Constants.DomainAdminOrgCode, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		}
		if (dadminOrg == null) {
			return;
		}
		this.orgService.userOrgWithUserComId(
				Collections.singletonList(String.valueOf(userId)), dadminOrg.getId(), targetComId, operatorId, operatorIp);
	}

	public void unbindDomainAdmin(Long id, Long operatorId, String operatorIp) {
		if (id == null) {
			return;
		}
		this.domainAdminDao.findById(id).ifPresent(row -> {
			this.commonOperate.assertBoundDomainAccess(operatorId, row.getDomainId());
			// 按站点保底：删除后该站点至少保留一名站点管理员，防止站点失管
			long remaining = this.domainAdminDao.countByDomainId(row.getDomainId()) - 1;
			if (remaining < 1) {
				throw new BaseException("该站点必须保留至少一名站点管理员，不能全部移除");
			}
			row.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
			row.setUpdateBy(operatorId);
			row.setUpdateIp(operatorIp);
			this.domainAdminDao.save(row);
			// 身份双视图对称清理（R28）：无任何剩余有效绑定时同步移除 dadmin 组成员
			this.removeDomainAdminOrgMembershipIfOrphan(row.getUserId(), row.getComId(), row.getDomainId());
		});
	}

	/**
	 * 按用户解绑其全部站点管理员绑定（组织口移除成员的对称入口，与逐站解绑同一逻辑）。
	 */
	@Transactional
	public void unbindDomainAdminByUser(Long userId, Long operatorId, String operatorIp) {
		if (userId == null) {
			return;
		}
		List<WoDomainAdmin> rows = this.domainAdminDao.findByUserId(userId);
		if (ObjectUtils.isBlank(rows)) {
			return;
		}
		rows.forEach(row -> this.unbindDomainAdmin(row.getId(), operatorId, operatorIp));
	}

	/**
	 * 解绑后的组成员对称清理：用户已无任何有效站点绑定时才移除 dadmin 组成员；
	 * 组织解析顺序与绑定时一致（站点级 dadmin 组优先，回退全局组）。
	 */
	private void removeDomainAdminOrgMembershipIfOrphan(Long userId, Long comId, Long domainId) {
		if (userId == null) {
			return;
		}
		List<WoDomainAdmin> remaining = this.domainAdminDao.findByUserId(userId);
		if (!ObjectUtils.isBlank(remaining)) {
			return;
		}
		Long targetComId = comId != null ? comId : Constants.TOP_COM_ID;
		WoOrg dadminOrg = null;
		if (domainId != null && domainId > 0) {
			dadminOrg = this.orgDao.findByComIdAndDomainIdAndOrgCode(targetComId, domainId, Constants.DomainAdminOrgCode);
		}
		if (dadminOrg == null) {
			dadminOrg = this.orgDao.findByOrgCodeAndIsValidAndDeleteFlag(
					Constants.DomainAdminOrgCode, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		}
		if (dadminOrg == null) {
			return;
		}
		this.orgService.removeMemberByOrg(userId, dadminOrg.getId());
	}

	/**
	 * 批量解绑站点管理员，复用单条解绑逻辑（含域归属校验）。
	 *
	 * @param ids 绑定记录id列表
	 */
	@Transactional
	public void unbindDomainAdmins(List<Long> ids, Long operatorId, String operatorIp) {
		if (ObjectUtils.isBlank(ids)) {
			return;
		}
		ids.forEach(id -> this.unbindDomainAdmin(id, operatorId, operatorIp));
	}

	/**
	 * 保存前校验主站域名与个性域名唯一性，避免落库触发唯一索引异常。
	 *
	 * @param entity 待保存站点
	 */
	public void validateDomainUniqueness(WoDomain entity) {
		if (entity == null) {
			return;
		}
		if (!ObjectUtils.isBlank(entity.getSiteDomain())) {
			String siteDomain = entity.getSiteDomain().trim().toLowerCase();
			entity.setSiteDomain(siteDomain);
			WoDomain occupied = this.entityRepo.findBySiteDomain(siteDomain);
			if (occupied != null && !Objects.equals(occupied.getId(), entity.getId())) {
				throw new BaseException("网站域名 " + siteDomain + " 已被占用，请更换", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
		if (!ObjectUtils.isBlank(entity.getSecondDomain())) {
			String secondDomain = entity.getSecondDomain().trim().toLowerCase();
			entity.setSecondDomain(secondDomain);
			WoDomain occupied = this.entityRepo.findBySecondDomain(secondDomain);
			if (occupied != null && !Objects.equals(occupied.getId(), entity.getId())) {
				String platDomain = this.getPlatDomain();
				String fullHost = ObjectUtils.isBlank(platDomain) ? secondDomain : secondDomain + "." + platDomain;
				throw new BaseException("个性域名 " + fullHost + " 已被占用", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
	}

	/** 是否隐藏只约束 wo_domain_resource.is_valid，避免与 wo_resource.is_valid 共用 filter 键。 */
	private void appendDomainResourceMountValidSql(StringBuilder sql, List<Object> mountFilter) {
		if (mountFilter == null || mountFilter.isEmpty()) {
			sql.append(" and r.is_valid='1' ");
			return;
		}
		sql.append(" and r.is_valid in (");
		boolean first = true;
		for (Object item : mountFilter) {
			String v = ObjectUtils.string(item);
			if (!ValidStatusEnum.VALID.toString().equals(v) && !ValidStatusEnum.INVALID.toString().equals(v)) {
				continue;
			}
			if (!first) {
				sql.append(',');
			}
			sql.append('\'').append(v).append('\'');
			first = false;
		}
		if (first) {
			sql.append('\'').append(ValidStatusEnum.VALID.toString()).append('\'');
		}
		sql.append(") ");
	}

	/**
	 * 站点资源挂载显隐：保留 wo_domain_resource 行，仅切换 is_valid；运行时菜单已按 is_valid 过滤。
	 * 不改动 wo_auth_role，故直连 API 仍按角色鉴权（见 {@link #setDomainAppVisibility} 说明）。
	 */
	public void setDomainResourceVisibility(DomainMountVisibilityBody body, Long operatorId, String operatorIp) {
		if (body == null || body.getDomainId() == null || body.getVisible() == null
				|| ObjectUtils.isBlank(body.getResourceIds())) {
			throw new BaseException("domainId、resourceIds、visible 不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		List<Long> resourceIds = body.getResourceIds().stream().map(Long::parseLong).collect(Collectors.toList());
		String isValid = body.getVisible() ? ValidStatusEnum.VALID.toString() : ValidStatusEnum.INVALID.toString();
		this.domainResRepo.updateVisibilityByDomainAndResourceIds(
				body.getDomainId(), resourceIds, isValid, operatorId, operatorIp);
	}

	/**
	 * 站点应用挂载显隐。隐藏时级联同站同应用下全部 wo_domain_resource，避免门户仍露出条目；
	 * 显示应用仅恢复 wo_domain_app，不自动恢复曾被单独隐藏的资源。
	 */
	public void setDomainAppVisibility(DomainMountVisibilityBody body, Long operatorId, String operatorIp) {
		if (body == null || body.getDomainId() == null || body.getVisible() == null
				|| ObjectUtils.isBlank(body.getAppIds())) {
			throw new BaseException("domainId、appIds、visible 不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		List<Long> appIds = body.getAppIds().stream().map(Long::parseLong).collect(Collectors.toList());
		String isValid = body.getVisible() ? ValidStatusEnum.VALID.toString() : ValidStatusEnum.INVALID.toString();
		this.domainAppService.updateMountVisibility(body.getDomainId(), appIds, isValid, operatorId, operatorIp);
		if (!body.getVisible()) {
			this.domainResRepo.updateVisibilityByDomainAndAppIds(
					body.getDomainId(), appIds, ValidStatusEnum.INVALID.toString(), operatorId, operatorIp);
		}
	}
}
