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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.base.RepoService;
import com.wldos.base.entity.EntityAssists;
import com.wldos.common.Constants;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.TreeUtils;
import com.wldos.common.vo.TreeSelectOption;
import com.wldos.support.resource.entity.WoResource;
import com.wldos.support.resource.enums.ResourceEnum;
import com.wldos.support.resource.vo.AuthInfo;
import com.wldos.support.term.dto.Term;
import com.wldos.support.web.enums.TemplateTypeEnum;
import com.wldos.sys.base.entity.WoDomain;
import com.wldos.sys.base.entity.WoDomainResource;
import com.wldos.sys.base.repo.DomainResourceRepo;
import com.wldos.sys.base.repo.ResourceRepo;
import com.wldos.sys.base.repo.TermRepo;
import com.wldos.sys.base.vo.DomRes;
import com.wldos.sys.base.vo.ResSimple;

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
public class ResourceService extends RepoService<ResourceRepo, WoResource, Long> {
	private final DomainService domainService;

	private final DomainAppService domainAppService;

	private final DomainResourceRepo domainResourceRepo;

	private final TermRepo termRepo;

	public ResourceService(DomainService domainService, DomainAppService domainAppService, DomainResourceRepo domainResourceRepo, TermRepo termRepo) {
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

		allRes = allRes.parallelStream().peek(r -> r.setSelected(bookedIds.contains(r.getId()))).collect(Collectors.toList());

		domRes.setDataRows(allRes);

		return domRes;
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

		EntityAssists.beforeInsert(resource, this.nextId(), curUserId, userIp, false);
		this.insertSelective(resource);
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
}