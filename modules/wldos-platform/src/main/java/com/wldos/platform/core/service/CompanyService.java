/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.BoolEnum;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.*;
import com.wldos.platform.core.dao.ComUserDao;
import com.wldos.platform.core.entity.WoComUser;
import com.wldos.platform.core.entity.WoCompany;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.dao.OrgUserDao;
import com.wldos.platform.core.support.PlatformOperatorConstants;
import com.wldos.platform.core.vo.Company;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司管理service。
 *
 * @author Yuanxi Universe
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CompanyService extends EntityService<CompanyDao, WoCompany, Long> {

	private final OrgDao orgRepo;

	private final OrgUserDao orgUserRepo;

	private final ComUserDao comUserRepo;

	private final TenantAdminDao tenantAdminDao;

	private final DomainDao domainDao;

	@Autowired
	@Lazy
	private UserService userService;

	@Autowired
	@Lazy
	private IdentityRegistryService identityRegistryService;

	public CompanyService(
			OrgDao orgRepo,
			OrgUserDao orgUserRepo,
			ComUserDao comUserRepo,
			TenantAdminDao tenantAdminDao,
			DomainDao domainDao) {
		this.orgRepo = orgRepo;
		this.orgUserRepo = orgUserRepo;
		this.comUserRepo = comUserRepo;
		this.tenantAdminDao = tenantAdminDao;
		this.domainDao = domainDao;
	}

	/**
	 * 删除前校验：虚拟平台树顶、平台运营主体、仍有子公司或站点归属的公司不可删。
	 */
	public void assertDeletable(WoCompany company) {
		if (company == null || company.getId() == null) {
			throw new BaseException("公司不存在", ResultCode.OPERATION_FAILED.getCode());
		}
		Long comId = company.getId();
		if (Objects.equals(comId, Constants.TOP_COM_ID)) {
			throw new BaseException("「平台」为虚拟树顶（id=0），不落库，不可删除", ResultCode.OPERATION_FAILED.getCode());
		}
		if (PlatformOperatorConstants.COM_CODE.equals(company.getComCode())) {
			throw new BaseException("平台官方运营主体不可删除；请在「平台运营」菜单维护站点与会员", ResultCode.OPERATION_FAILED.getCode());
		}
		if (this.entityRepo.countNormalChildrenByParentId(comId) > 0) {
			throw new BaseException("存在子公司，请先删除或迁移子公司后再删本公司", ResultCode.OPERATION_FAILED.getCode());
		}
		if (this.domainDao.countValidByComId(comId) > 0) {
			throw new BaseException("存在关联站点，请先在站点管理中解除或删除站点后再删公司", ResultCode.OPERATION_FAILED.getCode());
		}
	}

	/**
	 * 平台超管视角下，在扁平公司列表中追加虚拟平台租户（id=0，不落库）。
	 */
	public static void appendVirtualPlatformCompanyIfNeeded(List<WoCompany> rows, boolean platformAdminViewer) {
		if (!platformAdminViewer || rows == null) {
			return;
		}
		if (rows.stream().anyMatch(c -> c.getId() != null && c.getId() == Constants.TOP_COM_ID)) {
			return;
		}
		WoCompany plat = new WoCompany(Constants.TOP_COM_ID, Constants.TOP_COM_NAME);
		plat.setParentId(Constants.TOP_COM_ID);
		rows.add(plat);
		rows.sort(Comparator.comparing(WoCompany::getId));
	}

	/**
	 * 架构树/公司树查询结果中，在根节点层追加虚拟平台租户（与租户列表 doFilter 语义一致）。
	 */
	public static List<WoCompany> excludePlatformOperatorCompany(List<WoCompany> rows, Long operatorComId) {
		if (rows == null || operatorComId == null || operatorComId <= 0) {
			return rows;
		}
		return rows.stream()
				.filter(c -> c.getId() == null || !operatorComId.equals(c.getId()))
				.collect(Collectors.toList());
	}

	public static void excludePlatformOperatorFromCompanyTree(List<Company> roots, Long operatorComId) {
		if (roots == null || operatorComId == null || operatorComId <= 0) {
			return;
		}
		roots.removeIf(c -> operatorComId.equals(c.getId()));
		for (Company node : roots) {
			if (node.getChildren() != null && !node.getChildren().isEmpty()) {
				excludePlatformOperatorFromCompanyTree(node.getChildren(), operatorComId);
			}
		}
	}

	public static void appendVirtualPlatformCompanyRootIfNeeded(List<Company> roots, boolean platformAdminViewer) {
		if (!platformAdminViewer || roots == null) {
			return;
		}
		if (roots.stream().anyMatch(c -> c.getId() != null && c.getId() == Constants.TOP_COM_ID)) {
			return;
		}
		Company plat = new Company();
		plat.setId(Constants.TOP_COM_ID);
		plat.setComName(Constants.TOP_COM_NAME);
		plat.setParentId(Constants.TOP_COM_ID);
		roots.add(plat);
		roots.sort(Comparator.comparing(Company::getId));
	}

	/**
	 * 超级管理员给租户添加管理员
	 * 约定：一个用户只能作为一个租户管理员，一个公司可以拥有多个租户管理员，用户作为租户管理员时主企业可以不是受托租户，前端权限用主企业，后端管理用受托租户。
	 *
	 * @param userIds 待添加为租户管理员的用户ids
	 * @param userComId 待添加管理员的租户id
	 * @param curUserId 操作人员id
	 * @param uip 操作人员ip
	 * @return 反馈
	 */
	public String addTenantAdmin(List<String> userIds, Long userComId, Long curUserId, String uip) {
		List<Long> ids = userIds.parallelStream().map(Long::parseLong).collect(Collectors.toList());
		StringBuilder mes = new StringBuilder(50);
		List<Long> toBind = new ArrayList<>();
		for (Long userId : ids) {
			if (this.tenantAdminDao.findByUserId(userId) != null) {
				mes.append("用户：").append(userId).append(" 已是租户管理员，一个用户最多只能管理一个租户");
				continue;
			}
			toBind.add(userId);
		}
		if (toBind.isEmpty()) {
			return mes.toString();
		}

		List<WoComUser> comUsers = this.comUserRepo.queryAllByUserIds(toBind, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		Map<Long, List<WoComUser>> userComList = comUsers.parallelStream().collect(Collectors.groupingBy(WoComUser::getUserId));
		List<WoComUser> woComUsers = new ArrayList<>();
		List<WoComUser> mainComSync = new ArrayList<>();
		for (Long userId : toBind) {
			boolean isCurCom = true;
			if (userComList.containsKey(userId)) {
				if (userComList.get(userId).stream().allMatch(com -> Objects.equals(userComId, com.getComId()))) {
					continue;
				}
				isCurCom = false;
			}
			WoComUser comUser = new WoComUser();
			comUser.setComId(userComId);
			comUser.setUserId(userId);
			comUser.setIsMain(isCurCom ? BoolEnum.YES.toString() : BoolEnum.NO.toString());
			woComUsers.add(comUser);
			if (isCurCom) {
				mainComSync.add(comUser);
			}
		}

		if (!woComUsers.isEmpty()) {
			this.comUserRepo.saveAll(woComUsers);
		}

		for (WoComUser cu : mainComSync) {
			this.userService.syncUserMainComId(cu.getUserId(), cu.getComId(), curUserId, uip);
		}

		for (Long userId : toBind) {
			this.identityRegistryService.bindTenantAdminRegistry(userId, userComId, curUserId, uip);
		}

		this.cache.remove(io.github.wldos.common.enums.RedisKeyEnum.WLDOS_TENANT_ADMIN.toString());

		return mes.toString();
	}

	/**
	 * 移除租户管理员。按目标租户保底：删除后该租户至少保留一名租户管理员，防止租户失管。
	 *
	 * @param ids 待移除的用户id
	 * @param userComId 用户所属公司id
	 */
	public void removeTenantAdmin(List<Long> ids, Long userComId) {
		Map<Long, Long> userComMap = new java.util.HashMap<>();
		for (Long userId : ids) {
			com.wldos.platform.core.entity.WoTenantAdmin row = this.tenantAdminDao.findByUserId(userId);
			if (row != null && row.getComId() != null) {
				userComMap.put(userId, row.getComId());
			}
		}
		userComMap.values().stream().distinct().forEach(comId -> {
			long removeCount = userComMap.values().stream().filter(c -> Objects.equals(c, comId)).count();
			long remaining = this.tenantAdminDao.countByComId(comId) - removeCount;
			if (remaining < 1) {
				throw new io.github.wldos.common.exception.BaseException("该租户必须保留至少一名租户管理员，不能全部移除");
			}
		});
		WoOrg org = this.orgRepo.findByOrgCodeAndIsValidAndDeleteFlag(Constants.TAdminOrgCode, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		if (org != null) {
			this.orgUserRepo.removeOrgStaff(ids, org.getId());
		}
		this.identityRegistryService.unbindTenantAdminRegistry(ids, null, null);
		this.cache.remove(io.github.wldos.common.enums.RedisKeyEnum.WLDOS_TENANT_ADMIN.toString());
	}
}
