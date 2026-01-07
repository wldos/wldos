/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.common.Constants;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.TreeUtils;
import com.wldos.common.vo.TreeSelectOption;
import com.wldos.platform.core.dao.DomainResourceDao;
import com.wldos.platform.core.dao.ResourceDao;
import com.wldos.platform.core.dao.TermDao;
import com.wldos.platform.core.dao.AppDao;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoDomainResource;
import com.wldos.platform.core.entity.WoApp;
import com.wldos.platform.core.enums.AppOriginEnum;
import com.wldos.platform.core.vo.DomRes;
import com.wldos.platform.core.vo.ResSimple;
import com.wldos.platform.support.resource.entity.WoResource;
import com.wldos.platform.support.resource.enums.ResourceEnum;
import com.wldos.platform.support.resource.vo.AuthInfo;
import com.wldos.platform.support.term.dto.Term;
import com.wldos.platform.support.web.enums.TemplateTypeEnum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源操作service。
 *
 * @author 元悉宇宙
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

	@Autowired(required = false)
	private AppDao appDao;

	@Value("${wldos.plugins.debug:false}")
	private boolean wldosPluginsDebug;

	public ResourceService(DomainService domainService, DomainAppService domainAppService, DomainResourceDao domainResourceRepo, TermDao termRepo) {
		this.domainService = domainService;
		this.domainAppService = domainAppService;
		this.domainResourceRepo = domainResourceRepo;
		this.termRepo = termRepo;
	}

	public List<WoResource> queryResourceByRoleId(Long roleId) {
		return this.entityRepo.queryResourceByRoleId(roleId);
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
	 * 查询域上可以预订的资源
	 *
	 * @param domainId 域id
	 * @param pageQuery 查询参数
	 * @return 资源列表
	 */
	public PageableResult<DomRes> queryResByDomainId(Long domainId, PageQuery pageQuery) {
		List<Object> appIds = this.domainAppService.queryAppListByDomainId(domainId);
		if (ObjectUtils.isBlank(appIds))
			return new PageableResult<>();
		pageQuery.pushFilter("appId", appIds);

		PageableResult<DomRes> domRes = this.execQueryForTree(new DomRes(), new WoResource(), pageQuery, Constants.MENU_ROOT_ID);

		List<WoDomainResource> resources = this.domainResourceRepo.queryDomainRes(domainId);
		List<Long> bookedIds = resources.parallelStream().map(WoDomainResource::getResourceId).collect(Collectors.toList());

		List<DomRes> allRes = domRes.getData().getRows();

		// 递归设置所有节点（包括子节点）的选中状态：只有真正被添加到域的资源才显示为已选中
		this.setSelectedRecursively(allRes, bookedIds);

		domRes.setDataRows(allRes);

		return domRes;
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
		displayOrder = ObjectUtils.nvlToZero(order) + 1L;

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
				List<String> resTypes = new ArrayList<>();
				resTypes.add(ResourceEnum.MENU.getValue());
				resTypes.add(ResourceEnum.ADMIN_MENU.getValue());
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
			}
		}
	}
}