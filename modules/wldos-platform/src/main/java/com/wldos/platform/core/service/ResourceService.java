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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.res.ResultCode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.framework.mvc.service.EntityService;
import io.github.wldos.common.Constants;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;
import io.github.wldos.common.enums.RedisKeyEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.common.utils.TreeUtils;
import io.github.wldos.common.vo.TreeSelectOption;
import com.wldos.platform.core.dao.DomainResourceDao;
import com.wldos.platform.core.dao.ResourceDao;
import com.wldos.platform.core.dao.TermDao;
import com.wldos.platform.core.dao.AppDao;
import com.wldos.platform.core.dao.AuthRoleDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.entity.WoAuthRole;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoDomainResource;
import com.wldos.platform.core.entity.WoApp;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.enums.AppOriginEnum;
import com.wldos.platform.core.service.PageApiManifestLoader.PageApiManifestEntry;
import com.wldos.platform.core.service.PageApiManifestLoader.PageApiRef;
import com.wldos.platform.core.support.SiteNavDirSupport;
import com.wldos.platform.core.vo.DomRes;
import com.wldos.platform.core.vo.ResSimple;
import com.wldos.platform.core.vo.Resource;
import com.wldos.platform.core.vo.ResourceApiCoverageItemVO;
import com.wldos.platform.core.vo.RoleIdCode;
import io.github.wldos.platform.support.resource.entity.WoResource;
import io.github.wldos.platform.support.resource.enums.ResourceEnum;
import io.github.wldos.platform.support.resource.vo.AuthInfo;
import io.github.wldos.platform.support.term.dto.Term;
import io.github.wldos.platform.support.web.enums.TemplateTypeEnum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源操作service。
 *
 * @author Yuanxi Universe
 * @date 2021/4/28
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceService extends EntityService<ResourceDao, WoResource, Long> {
	private final DomainService domainService;

	private final DomainAppService domainAppService;

	private final DomainResourceDao domainResourceRepo;

	private final TermDao termRepo;

	private final PageApiManifestLoader pageApiManifestLoader;

	private final ResourceProbeService resourceProbeService;

	private final ResourceAuthReferenceGuard resourceAuthReferenceGuard;

	@Autowired(required = false)
	private AppDao appDao;

	@Value("${wldos.plugins.debug:false}")
	private boolean wldosPluginsDebug;

	@Autowired
	@Lazy
	private RoleDao roleDao;

	@Autowired
	@Lazy
	private AuthRoleDao authRoleDao;

	public ResourceService(DomainService domainService, DomainAppService domainAppService,
	                       DomainResourceDao domainResourceRepo, TermDao termRepo,
	                       PageApiManifestLoader pageApiManifestLoader,
	                       ResourceProbeService resourceProbeService,
	                       ResourceAuthReferenceGuard resourceAuthReferenceGuard) {
		this.domainService = domainService;
		this.domainAppService = domainAppService;
		this.domainResourceRepo = domainResourceRepo;
		this.termRepo = termRepo;
		this.pageApiManifestLoader = pageApiManifestLoader;
		this.resourceProbeService = resourceProbeService;
		this.resourceAuthReferenceGuard = resourceAuthReferenceGuard;
	}

	public List<WoResource> queryResourceByRoleId(Long roleId) {
		return this.entityRepo.queryResourceByRoleId(roleId);
	}

	/**
	 * 资源清单、角色授权候选等与 ResourceDao / 鉴权 SQL 同口径：未软删且有效。
	 */
	public List<WoResource> listGrantCatalogResources() {
		Map<String, Object> condition = new HashMap<>(2);
		condition.put("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		condition.put("isValid", ValidStatusEnum.VALID.toString());
		return this.findAllWithCond(WoResource.class, condition);
	}

	private void ensureCatalogListCondition(PageQuery pageQuery) {
		Map<String, Object> condition = pageQuery.getCondition();
		if (condition == null) {
			condition = new HashMap<>();
			pageQuery.setCondition(condition);
		}
		condition.putIfAbsent("deleteFlag", DeleteFlagEnum.NORMAL.toString());
	}

	/**
	 * 资源归属应用变更时，同步各站点 wo_domain_app / wo_domain_resource / wo_auth_role。
	 */
	public void cascadeAppIdOnChange(WoResource existing, WoResource incoming, Long operatorId, String operatorIp) {
		if (existing == null || existing.getId() == null || incoming == null) {
			return;
		}
		Long oldAppId = existing.getAppId();
		Long newAppId = incoming.getAppId();
		if (Objects.equals(oldAppId, newAppId)) {
			return;
		}
		List<Long> domainIds = this.domainResourceRepo.listDomainIdsByResourceId(existing.getId());
		if (ObjectUtils.isBlank(domainIds)) {
			return;
		}
		if (newAppId == null) {
			throw new BaseException("该资源已被 " + domainIds.size() + " 个站点挂载，归属应用不能为空",
					ResultCode.VALIDATION_ERROR.getCode());
		}
		WoApp targetApp = this.appDao != null ? this.appDao.findById(newAppId).orElse(null) : null;
		if (targetApp == null || !ValidStatusEnum.VALID.toString().equals(targetApp.getIsValid())) {
			throw new BaseException("目标归属应用不存在或已停用", ResultCode.VALIDATION_ERROR.getCode());
		}
		for (Long domainId : domainIds) {
			if (domainId == null || domainId <= 0) {
				continue;
			}
			if (this.domainAppService.existsByAppIdAndDomainId(newAppId, domainId)) {
				continue;
			}
			WoDomain domain = this.domainService.findById(domainId);
			if (domain == null) {
				continue;
			}
			Long comId = domain.getComId() != null ? domain.getComId() : Constants.TOP_COM_ID;
			this.domainService.domainApp(
					Collections.singletonList(String.valueOf(newAppId)),
					domainId,
					comId,
					operatorId,
					operatorIp);
		}
		this.domainResourceRepo.updateAppIdByResourceId(existing.getId(), newAppId);
		if (oldAppId != null) {
			this.authRoleDao.updateAppIdByResourceIdAndOldAppId(existing.getId(), oldAppId, newAppId);
		}
		else {
			this.authRoleDao.updateAppIdByResourceIdWhereAppIdIsNull(existing.getId(), newAppId);
		}
		log.info("[resource.appId.cascade] resourceId={} appId {} -> {}, domains={}",
				existing.getId(), oldAppId, newAppId, domainIds.size());
	}

	/**
	 * 新建/更新管理端 API 按钮时，自动追加到 tadmin、dadmin 身份角色的 wo_auth_role。
	 */
	public void appendScopedAdminIdentityRoleAuth(WoResource resource, Long comId, Long domainId) {
		if (resource == null || resource.getId() == null) {
			return;
		}
		String type = resource.getResourceType();
		if (!ResourceEnum.ADMIN_BUTTON.getValue().equals(type)
				&& !ResourceEnum.ADMIN_PLUGIN_BUTTON.getValue().equals(type)) {
			return;
		}
		Long effectiveComId = comId != null ? comId : Constants.TOP_COM_ID;
		Long resourceId = resource.getId();
		Long appId = resource.getAppId();
		this.appendAuthForIdentityRole(resourceId, appId, effectiveComId, domainId, Constants.TenantAdminRoleCode);
		this.appendAuthForIdentityRole(resourceId, appId, effectiveComId, domainId, Constants.DomainAdminRoleCode);
	}

	private void appendAuthForIdentityRole(Long resourceId, Long appId, Long comId, Long domainId, String roleCode) {
		Long roleId = this.resolveIdentityRoleId(comId, domainId, roleCode);
		if (roleId == null) {
			return;
		}
		WoAuthRole existing = authRoleDao.findByRoleIdAndResourceIdAndAppId(roleId, resourceId, appId);
		if (existing != null) {
			return;
		}
		authRoleDao.save(WoAuthRole.of(roleId, resourceId, appId));
	}

	private Long resolveIdentityRoleId(Long comId, Long domainId, String roleCode) {
		if (!GrantVisibilityPolicy.isIdentityRoleCode(roleCode)) {
			return null;
		}
		WoRole template = this.roleDao.findPlatformTemplateByRoleCode(roleCode);
		return template != null ? template.getId() : null;
	}

	/**
	 * 资源树分页：支持 terminals、menuRegion 多选过滤（内存二次过滤后重建树）。
	 * <p>性能：{@code execQueryForTree} 在无有效分页时会加载全表匹配行并在内存建树，
	 * 资源量级较大时是列表慢的主因，与站点 nav_dir 行数无关。
	 */
	public PageData<Resource> queryResourceTree(PageQuery pageQuery) {
		this.ensureCatalogListCondition(pageQuery);
		Map<String, List<Object>> filter = pageQuery.getFilter();
		List<Object> terminalsFilter = null;
		List<Object> menuRegionFilter = null;
		if (filter != null) {
			Map<String, List<Object>> effectiveFilter = new HashMap<>(filter);
			if (filter.containsKey("terminals")) {
				terminalsFilter = new ArrayList<>(filter.get("terminals"));
				effectiveFilter.remove("terminals");
			}
			if (filter.containsKey("menuRegion")) {
				menuRegionFilter = new ArrayList<>(filter.get("menuRegion"));
				effectiveFilter.remove("menuRegion");
			}
			pageQuery.setFilter(effectiveFilter);
		}

		PageData<Resource> page = this.execQueryForTree(new Resource(), new WoResource(), pageQuery, Constants.MENU_ROOT_ID);
		final List<Object> terminalFilterValues = terminalsFilter;
		final List<Object> menuRegionFilterValues = menuRegionFilter;
		if ((terminalFilterValues == null || terminalFilterValues.isEmpty())
				&& (menuRegionFilterValues == null || menuRegionFilterValues.isEmpty())) {
			return page;
		}

		List<Resource> flat = flattenResourceTree(page.getRows());
		List<Resource> matched = flat.stream()
				.filter(item -> matchesTerminalsFilter(item.getTerminals(), terminalFilterValues))
				.filter(item -> matchesMenuRegionFilter(item.getMenuRegion(), menuRegionFilterValues))
				.collect(Collectors.toList());
		page.setRows(TreeUtils.build(matched, Constants.MENU_ROOT_ID));
		page.setTotal(matched.size());
		return page;
	}

	private static List<Resource> flattenResourceTree(List<Resource> nodes) {
		List<Resource> out = new ArrayList<>();
		if (nodes == null) {
			return out;
		}
		for (Resource node : nodes) {
			out.add(node);
			if (node.getChildren() != null && !node.getChildren().isEmpty()) {
				out.addAll(flattenResourceTree(node.getChildren()));
			}
		}
		return out;
	}

	private static boolean matchesTerminalsFilter(String terminals, List<Object> filterValues) {
		if (filterValues == null || filterValues.isEmpty()) {
			return true;
		}
		for (Object value : filterValues) {
			String code = value == null ? "" : value.toString();
			if (code.isEmpty()) {
				if (ObjectUtils.isBlank(terminals)) {
					return true;
				}
				continue;
			}
			if (ObjectUtils.isBlank(terminals)) {
				return true;
			}
			for (String part : terminals.split(",")) {
				if (code.equalsIgnoreCase(part.trim())) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean matchesMenuRegionFilter(String menuRegion, List<Object> filterValues) {
		if (filterValues == null || filterValues.isEmpty()) {
			return true;
		}
		String normalized = ObjectUtils.isBlank(menuRegion) ? "" : menuRegion.trim();
		for (Object value : filterValues) {
			String code = value == null ? "" : value.toString();
			if (code.isEmpty()) {
				if (normalized.isEmpty()) {
					return true;
				}
				continue;
			}
			if ("nav_main".equals(code)) {
				if (normalized.isEmpty() || "nav_main".equals(normalized)) {
					return true;
				}
				continue;
			}
			if (code.equals(normalized)) {
				return true;
			}
		}
		return false;
	}

	public List<WoResource> queryResourceByInheritRoleId(Long roleId) {
		return this.entityRepo.queryResourceByInheritRoleId(roleId);
	}

	/**
	 * 查询指定用户、类型的授权资源
	 *
	 * @param domain 域名
	 * @param comId 用户主企业id
	 * @param type 资源类型
	 * @param userId 用户id
	 * @return 用户可见资源列表
	 */
	public List<WoResource> queryResource(String domain, Long comId, String type, Long userId) {
		WoDomain woDomain = this.domainService.findByDomain(domain);

		return this.entityRepo.queryResource(woDomain.getId(), comId, type, userId);
	}

	public List<WoResource> queryResource(Long domainId, Long comId, String type, Long userId) {
		return this.entityRepo.queryResource(domainId, comId, type, userId);
	}

	/**
	 * 查询游客在某应用(模块)下的所有资源，并拼装成权限信息。
	 *
	 * @param domain 请求的域
	 * @param appCode 应用编码
	 * @return 授权信息
	 */
	public List<AuthInfo> queryAuthInfoForGuest(String domain, String appCode) {
		// 找到域id
		WoDomain woDomain = this.domainService.findByDomain(domain);

		return this.entityRepo.queryAuthInfoForGuest(woDomain.getId(), appCode);
	}

	/**
	 * 根据资源类型查询游客有操作权限的某类资源。
	 *
	 * @param domainId 域名id
	 * @param type 资源类型
	 * @return 授权信息
	 */
	public List<WoResource> queryResourceForGuest(Long domainId, String type) {

		return this.entityRepo.queryResourceForGuest(domainId, type);
	}

	/**
	 * guest.role 改绑前校验：目标角色须具备门户 menu 授权且（在可解析的站点上）已挂载，避免游客顶栏 menu 为空。
	 */
	public void assertGuestRoleSlotTarget(Long roleId, Long domainId, Long comId) {
		if (roleId == null || roleId <= 0) {
			return;
		}
		List<WoResource> portalMenus = this.collectPortalMenuGrantsForRole(roleId);
		if (portalMenus.isEmpty()) {
			throw new BaseException(
					"目标角色尚未配置门户菜单，绑定游客后顶栏可能为空。请先在角色授权中配置菜单，或改用已有菜单的角色。",
					ResultCode.VALIDATION_ERROR.getCode());
		}
		Long checkDomainId = domainId != null && domainId > 0 ? domainId : null;
		if (checkDomainId == null && comId != null && comId > 0) {
			WoDomain first = this.domainService.findFirstByComId(comId);
			if (first != null) {
				checkDomainId = first.getId();
			}
		}
		if (checkDomainId == null || checkDomainId <= 0) {
			return;
		}
		Set<Long> mountedIds = this.domainResourceRepo.queryDomainRes(checkDomainId).stream()
				.map(WoDomainResource::getResourceId).collect(Collectors.toSet());
		boolean anyMounted = portalMenus.stream().anyMatch(r -> mountedIds.contains(r.getId()));
		if (!anyMounted) {
			throw new BaseException(
					"目标角色的门户菜单未挂载到当前站点（或本租户首个站点），绑定后游客仍可能看不到顶栏。",
					ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private List<WoResource> collectPortalMenuGrantsForRole(Long roleId) {
		Set<Long> seen = new HashSet<>();
		List<WoResource> merged = new ArrayList<>();
		for (WoResource row : this.entityRepo.queryResourceByRoleId(roleId)) {
			if (seen.add(row.getId())) {
				merged.add(row);
			}
		}
		for (WoResource row : this.entityRepo.queryResourceByInheritRoleId(roleId)) {
			if (seen.add(row.getId())) {
				merged.add(row);
			}
		}
		return merged.stream()
				.filter(r -> ResourceEnum.MENU.getValue().equals(r.getResourceType())
						|| ResourceEnum.PLUGIN_MENU.getValue().equals(r.getResourceType()))
				.collect(Collectors.toList());
	}

	/**
	 * 查询域上可以预订的资源
	 *
	 * @param domainId 域id
	 * @param pageQuery 查询参数
	 * @return 资源列表
	 */
	public PageData<DomRes> queryResByDomainId(Long domainId, PageQuery pageQuery) {
		this.ensureCatalogListCondition(pageQuery);
		List<Object> appIds = this.domainAppService.queryAppListByDomainId(domainId);
		if (ObjectUtils.isBlank(appIds)) {
			return new PageData<>();
		}
		pageQuery.pushFilter("appId", appIds);

		Boolean domainMounted = this.pullDomainMountedFilter(pageQuery);
		if (pageQuery.getCondition() != null) {
			pageQuery.getCondition().remove("domainId");
		}

		List<WoDomainResource> resources = this.domainResourceRepo.queryDomainRes(domainId);
		List<Long> bookedIds = resources.parallelStream().map(WoDomainResource::getResourceId).collect(Collectors.toList());
		Set<Long> foreignSiteNavDirIds = this.collectForeignSiteNavDirIds(bookedIds);

		PageData<DomRes> domRes;
		if (domainMounted != null) {
			domRes = this.commonOperate.execQueryForPage(DomRes.class, pageQuery, WoResource.class, false);
			List<DomRes> flat = domRes.getRows();
			if (!ObjectUtils.isBlank(flat)) {
				flat = flat.stream()
						.filter(row -> !foreignSiteNavDirIds.contains(row.getId()))
						.filter(row -> domainMounted.booleanValue() == bookedIds.contains(row.getId()))
						.collect(Collectors.toList());
			}
			else {
				flat = Collections.emptyList();
			}
			domRes.setTotal(flat.size());
			domRes.setRows(TreeUtils.build(flat, Constants.MENU_ROOT_ID));
		}
		else {
			domRes = this.execQueryForTree(new DomRes(), new WoResource(), pageQuery, Constants.MENU_ROOT_ID);
			List<DomRes> flat = flattenDomResTree(domRes.getRows());
			if (!ObjectUtils.isBlank(flat)) {
				flat = flat.stream()
						.filter(row -> !foreignSiteNavDirIds.contains(row.getId()))
						.collect(Collectors.toList());
				domRes.setRows(TreeUtils.build(flat, Constants.MENU_ROOT_ID));
				domRes.setTotal(flat.size());
			}
		}

		this.setSelectedRecursively(domRes.getRows(), bookedIds);

		return domRes;
	}

	private Set<Long> collectForeignSiteNavDirIds(List<Long> bookedIds) {
		Set<Long> bookedSet = bookedIds == null ? Collections.emptySet() : new HashSet<>(bookedIds);
		return this.listGrantCatalogResources().stream()
				.filter(SiteNavDirSupport::isSiteOwnedNavDir)
				.map(WoResource::getId)
				.filter(id -> id != null && !bookedSet.contains(id))
				.collect(Collectors.toSet());
	}

	private static List<DomRes> flattenDomResTree(List<DomRes> nodes) {
		List<DomRes> out = new ArrayList<>();
		if (nodes == null) {
			return out;
		}
		for (DomRes node : nodes) {
			out.add(node);
			if (node.getChildren() != null && !node.getChildren().isEmpty()) {
				out.addAll(flattenDomResTree(node.getChildren()));
			}
		}
		return out;
	}

	/**
	 * 站点资源选择列表：按是否已挂载到当前域筛选（1=已添加，0=未添加）。
	 */
	private Boolean pullDomainMountedFilter(PageQuery pageQuery) {
		if (pageQuery.getCondition() == null) {
			return null;
		}
		Object raw = pageQuery.getCondition().remove("domainMounted");
		if (raw == null) {
			return null;
		}
		String value = ObjectUtils.string(raw).trim();
		if (value.isEmpty()) {
			return null;
		}
		if ("1".equals(value) || "true".equalsIgnoreCase(value)) {
			return Boolean.TRUE;
		}
		if ("0".equals(value) || "false".equalsIgnoreCase(value)) {
			return Boolean.FALSE;
		}
		return null;
	}

	/**
	 * 递归设置所有节点（包括子节点）的选中状态
	 * 只有真正被添加到域的资源才显示为已选中，父节点选中不影响子节点
	 *
	 * @param nodes 节点列表
	 * @param bookedIds 已预订的资源ID列表
	 */
	private void setSelectedRecursively(List<DomRes> nodes, List<Long> bookedIds) {
		if (ObjectUtils.isBlank(nodes)) {
			return;
		}
		for (DomRes node : nodes) {
			// 只设置当前节点是否已选中（根据是否在 bookedIds 中）
			node.setSelected(bookedIds.contains(node.getId()));
			// 递归处理子节点
			if (node.getChildren() != null && !node.getChildren().isEmpty()) {
				this.setSelectedRecursively(node.getChildren(), bookedIds);
			}
		}
	}

	/**
	 * 按模板新建菜单
	 *
	 * @param resSimple 资源模板
	 * @param curUserId 当前用户id
	 * @param userIp 用户ip
	 */
	public void addSimpleMenu(ResSimple resSimple, Long curUserId, String userIp) {

		String tempType = resSimple.getTempType();
		Long termTypeId = resSimple.getTermTypeId();

		String resName = resSimple.getResName();
		String resPath;
		long displayOrder;
		String resCode;
		String icon = resSimple.getIcon();
		String resType = ResourceEnum.MENU.getValue();
		String reqMethod = resSimple.getReqMethod();
		String target = resSimple.getTarget();
		Long appId = resSimple.getAppId();
		Long parentId = resSimple.getParentId();
		String remark = resSimple.getRemark();

		if (termTypeId == null) {
			resPath = "/" + tempType;
			if (ObjectUtils.isBlank(resName)) {
				resName = TemplateTypeEnum.getTemplateTypeEnumByValue(tempType).getLabel();
			}
			resCode = tempType;
		}
		else {
			Term term = this.termRepo.queryTermByTermTypeId(termTypeId);
			resPath = "/" + tempType + "/category/" + term.getSlug();
			if (ObjectUtils.isBlank(resName)) {
				resName = term.getName();
			}
			resCode = tempType + "_" + term.getSlug();
		}

		Long order = this.entityRepo.queryMaxOrder(parentId);
		long nextOrder = ObjectUtils.nvlToZero(order) + 1L;
		displayOrder = Math.min(nextOrder, Constants.DISPLAY_ORDER_MAX);

		WoResource resource = new WoResource(resCode, resName, resPath, icon, resType, reqMethod, target, appId, parentId, displayOrder, remark);

		this.saveOrUpdate(resource);
	}

	/**
	 * 查询分层菜单树
	 *
	 * @return 菜单树
	 */
	public List<TreeSelectOption> queryLayerMenuTree() {
		String key = RedisKeyEnum.WLDOS_AUTH.toString() + ":res:layer";
		String value = ObjectUtils.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				// 上级菜单选择器：菜单类 + 模块目录（other），便于操作类资源挂模块根目录而非根节点
				List<String> resTypes = new ArrayList<>();
				resTypes.add(ResourceEnum.MENU.getValue());
				resTypes.add(ResourceEnum.ADMIN_MENU.getValue());
				resTypes.add(ResourceEnum.PLUGIN_MENU.getValue());
				resTypes.add(ResourceEnum.ADMIN_PLUGIN_MENU.getValue());
				resTypes.add(ResourceEnum.OTHER.getValue());
				resTypes.add(ResourceEnum.WEB_SERV.getValue());
				List<WoResource> allRes = this.entityRepo.queryByResTypes(resTypes);

				List<TreeSelectOption> menus = allRes.parallelStream().map(res -> {
					Long resId = res.getId();
					return TreeSelectOption.of(resId, res.getParentId(), res.getResourceName(), resId.toString(), resId.toString(), res.getDisplayOrder());
				}).collect(Collectors.toList());

				String topMenuId = String.valueOf(Constants.MENU_ROOT_ID);

				TreeSelectOption topMenu = TreeSelectOption.of(Constants.TOP_TERM_ID, Constants.TOP_VIR_ID, "根资源", topMenuId, topMenuId, 0L);

				menus.add(0, topMenu);

				menus = TreeUtils.build(menus, Constants.TOP_VIR_ID);

				if (menus.isEmpty())
					return menus;

				value = om.writeValueAsString(menus);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return menus;
			}

			return om.readValue(value, new TypeReference<List<TreeSelectOption>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取分层菜单树异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}

	public Long queryMaxOrder(Long parentId) {
		return this.entityRepo.queryMaxOrder(parentId);
	}

	/**
	 * 删除资源前的预处理（检查是否是插件资源，如果是则阻止删除）
	 */
	@Override
	protected void preDelete(WoResource entity) {
		if (entity == null || entity.getId() == null) {
			return;
		}

		// 检查资源的 appId 对应的应用是否是插件应用
		if (entity.getAppId() != null && appDao != null) {
			WoApp app = this.appDao.findById(entity.getAppId()).get();
			if (app != null && AppOriginEnum.PLUGIN.getValue().equals(app.getAppOrigin())) {
				if (wldosPluginsDebug) {
					log.info("检测到插件资源删除请求: " + entity.getResourceName() + " (resourceId: " + entity.getId() + ", appId: " + entity.getAppId() + ")");
				}
				throw new RuntimeException("不能直接删除插件资源。插件资源是插件的一部分，必须通过卸载插件来移除。请前往插件管理页面卸载对应的插件（" + app.getAppName() + "）。");
			}
		}
		this.resourceAuthReferenceGuard.assertDeletable(entity.getId(), entity.getAppId(), entity.getResourceName());
	}

	/**
	 * 批量删除资源前的预处理（检查是否包含插件资源，如果是则阻止删除）
	 */
	@Override
	protected void preDeletes(List<Object> ids) {
		if (ids == null || ids.isEmpty()) {
			return;
		}

		// 检查要删除的资源是否包含插件资源
		if (appDao != null) {
			for (Object id : ids) {
				if (id == null) {
					continue;
				}
				WoResource resource = this.entityRepo.findById(Long.parseLong(id.toString())).get();
				if (resource != null && resource.getAppId() != null) {
					WoApp app = this.appDao.findById(resource.getAppId()).get();
					if (app != null && AppOriginEnum.PLUGIN.getValue().equals(app.getAppOrigin())) {
						if (wldosPluginsDebug) {
							log.info("检测到批量删除中包含插件资源: " + resource.getResourceName() + " (resourceId: " + resource.getId() + ", appId: " + resource.getAppId() + ")");
						}
						throw new RuntimeException("不能直接删除插件资源。插件资源是插件的一部分，必须通过卸载插件来移除。资源「" + resource.getResourceName() + "」属于插件「" + app.getAppName() + "」，请前往插件管理页面卸载对应的插件。");
					}
				}
				if (resource != null) {
					this.resourceAuthReferenceGuard.assertDeletable(resource.getId(), resource.getAppId(), resource.getResourceName());
				}
			}
		}
	}

	/**
	 * 检查资源池内 menu 页面所依赖、但未声明为 button 的 API。
	 */
	@Transactional(readOnly = true)
	public List<ResourceApiCoverageItemVO> checkApiCoverage(String appCode) {
		if (this.appDao == null) {
			throw new BaseException("应用服务不可用");
		}
		WoApp app = this.resolveAppForApiCoverage(appCode);
		if (app == null) {
			throw new BaseException("应用不存在: " + appCode);
		}
		Long appId = app.getId();

		Map<String, PageApiManifestEntry> manifest = this.pageApiManifestLoader.loadFresh();
		List<WoResource> menuResources = this.queryMenuResources(appId);
		Map<String, WoResource> menuByPath = new HashMap<>();
		for (WoResource menu : menuResources) {
			if (menu != null && !ObjectUtils.isBlank(menu.getResourcePath())) {
				menuByPath.putIfAbsent(menu.getResourcePath().trim(), menu);
			}
		}
		Map<String, WoResource> buttonByKey = this.indexButtonResources(appId);

		List<ResourceApiCoverageItemVO> issues = new ArrayList<>();
		Set<String> reportedApiKeys = new HashSet<>();
		for (WoResource menu : menuResources) {
			this.appendManifestApiCoverageIssues(menu, manifest, buttonByKey, issues, reportedApiKeys);
		}
		for (Map.Entry<String, PageApiManifestEntry> entry : manifest.entrySet()) {
			if (!this.manifestPageBelongsToApp(entry.getKey(), app.getAppCode())) {
				continue;
			}
			WoResource anchor = menuByPath.get(entry.getKey().trim());
			if (anchor == null) {
				anchor = this.buildManifestAnchorMenu(appId, entry.getKey(), entry.getValue());
			}
			this.appendManifestApiCoverageIssues(anchor, manifest, buttonByKey, issues, reportedApiKeys);
		}
		return issues;
	}

	private WoApp resolveAppForApiCoverage(String appCode) {
		if (ObjectUtils.isBlank(appCode)) {
			return this.appDao.findByAppCode("admin");
		}
		String trimmed = appCode.trim();
		WoApp app = this.appDao.findByAppCode(trimmed);
		if (app != null) {
			return app;
		}
		if ("/".equals(trimmed) || "portal".equalsIgnoreCase(trimmed)) {
			app = this.appDao.findByAppCode("portal");
			if (app == null) {
				app = this.appDao.findByAppCode("/");
			}
		}
		return app;
	}

	private boolean manifestPageBelongsToApp(String pagePath, String appCode) {
		if (ObjectUtils.isBlank(pagePath) || ObjectUtils.isBlank(appCode)) {
			return false;
		}
		boolean adminLikePath = pagePath.startsWith("/admin");
		String code = appCode.trim();
		if ("admin".equalsIgnoreCase(code)) {
			return adminLikePath;
		}
		if ("/".equals(code) || "portal".equalsIgnoreCase(code)) {
			return !adminLikePath;
		}
		return true;
	}

	private WoResource buildManifestAnchorMenu(Long appId, String pagePath, PageApiManifestEntry entry) {
		WoResource menu = new WoResource();
		menu.setAppId(appId);
		menu.setResourcePath(pagePath.trim());
		String name = pagePath.trim();
		if (entry != null && !ObjectUtils.isBlank(entry.getComponent())) {
			name = entry.getComponent().trim();
		}
		menu.setResourceName(name);
		menu.setResourceType(ResourceEnum.MENU.getValue());
		return menu;
	}

	private void appendManifestApiCoverageIssues(WoResource menu, Map<String, PageApiManifestEntry> manifest,
			Map<String, WoResource> buttonByKey, List<ResourceApiCoverageItemVO> issues, Set<String> reportedApiKeys) {
		if (menu == null || ObjectUtils.isBlank(menu.getResourcePath())) {
			return;
		}
		PageApiManifestEntry entry = manifest.get(menu.getResourcePath().trim());
		if (entry == null || ObjectUtils.isBlank(entry.getApis())) {
			return;
		}
		for (PageApiRef apiRef : entry.getApis()) {
			if (apiRef == null || ObjectUtils.isBlank(apiRef.getPath())) {
				continue;
			}
			String apiMethod = ObjectUtils.isBlank(apiRef.getMethod()) ? "GET" : apiRef.getMethod().trim().toUpperCase();
			String apiPath = apiRef.getPath().trim();
			String resolvedMethod = this.resourceProbeService.resolveHttpMethod(apiPath, apiMethod);
			if (resolvedMethod != null) {
				apiMethod = resolvedMethod;
			}
			String apiKey = PageApiManifestLoader.apiKey(apiMethod, apiPath);
			if (buttonByKey.containsKey(apiKey) || reportedApiKeys.contains(apiKey)) {
				continue;
			}
			reportedApiKeys.add(apiKey);
			String message = menu.getId() == null
					? "manifest 已声明该 API，但资源池无对应菜单页与 button，请新建菜单 " + menu.getResourcePath() + " 及 button"
					: "页面引用了该 API，但资源池未声明对应的 button 资源，请在本页新建";
			issues.add(this.buildApiCoverageIssue(menu, apiPath, apiMethod, message));
		}
	}

	private List<WoResource> queryMenuResources(Long appId) {
		List<WoResource> appResources = this.entityRepo.findAllByAppId(appId);
		if (appResources == null) {
			return Collections.emptyList();
		}
		return appResources.stream()
				.filter(r -> r != null && ResourceEnum.isMenuType(r.getResourceType()))
				.collect(Collectors.toList());
	}

	private Map<String, WoResource> indexButtonResources(Long appId) {
		List<WoResource> appResources = this.entityRepo.findAllByAppId(appId);
		Map<String, WoResource> index = new HashMap<>();
		if (appResources == null) {
			return index;
		}
		for (WoResource resource : appResources) {
			if (resource == null || !ResourceEnum.isButtonType(resource.getResourceType())) {
				continue;
			}
			index.put(PageApiManifestLoader.apiKey(resource.getRequestMethod(), resource.getResourcePath()), resource);
		}
		return index;
	}

	private ResourceApiCoverageItemVO buildApiCoverageIssue(WoResource menu, String apiPath, String apiMethod,
			String message) {
		ResourceApiCoverageItemVO item = new ResourceApiCoverageItemVO();
		item.setSourceMenuPath(menu.getResourcePath());
		item.setSourceMenuName(menu.getResourceName());
		item.setSourceMenuId(menu.getId());
		item.setAppId(menu.getAppId());
		item.setSourceMenuResourceType(menu.getResourceType());
		item.setSuggestedResourceType(this.resolveButtonType(menu.getResourceType()));
		item.setSuggestedResourceName(this.buildSuggestedName(apiPath, apiMethod));
		item.setSuggestedResourceCode(this.buildSuggestedCode(apiPath, apiMethod));
		item.setApiPath(apiPath);
		item.setApiMethod(apiMethod);
		item.setApiName(this.resourceProbeService.resolveApiOperationName(apiPath, apiMethod));
		item.setMessage(message);
		return item;
	}

	private String resolveButtonType(String menuType) {
		if (ObjectUtils.isBlank(menuType)) {
			return ResourceEnum.BUTTON.getValue();
		}
		return ResourceEnum.buttonTypeForMenuType(menuType);
	}

	private String buildSuggestedName(String apiPath, String apiMethod) {
		String segment = this.lastPathSegment(apiPath);
		String name = ObjectUtils.isBlank(apiMethod) ? segment : apiMethod + " " + segment;
		if (name.length() > 25) {
			return name.substring(0, 25);
		}
		return name;
	}

	private String buildSuggestedCode(String apiPath, String apiMethod) {
		String slug = apiPath == null ? "" : apiPath.trim().replaceAll("^/+", "").replace('/', '_');
		if (ObjectUtils.isBlank(slug)) {
			slug = "api";
		}
		String method = ObjectUtils.isBlank(apiMethod) ? "get" : apiMethod.trim().toLowerCase();
		String code = slug + "_" + method;
		if (code.length() > 50) {
			return code.substring(0, 50);
		}
		return code;
	}

	private String lastPathSegment(String apiPath) {
		if (ObjectUtils.isBlank(apiPath)) {
			return "api";
		}
		String normalized = apiPath.trim().replaceAll("/+$", "");
		int idx = normalized.lastIndexOf('/');
		String segment = idx >= 0 ? normalized.substring(idx + 1) : normalized;
		return ObjectUtils.isBlank(segment) ? "api" : segment;
	}

	private String normalizeAppCode(String appCode) {
		if (ObjectUtils.isBlank(appCode)) {
			return "admin";
		}
		String normalized = appCode.trim();
		if ("portal".equalsIgnoreCase(normalized)) {
			return "/";
		}
		return normalized;
	}
}