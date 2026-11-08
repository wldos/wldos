/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.wldos.platform.core.dao.CompanyDao;
import com.wldos.platform.core.dao.DomainAdminDao;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.PlatformAdminDao;
import com.wldos.platform.core.dao.TenantAdminDao;
import com.wldos.platform.core.dao.TrustManDao;
import com.wldos.platform.core.dao.UserDao;
import com.wldos.platform.core.entity.WoCompany;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoDomainAdmin;
import com.wldos.platform.core.entity.WoPlatformAdmin;
import com.wldos.platform.core.entity.WoTenantAdmin;
import com.wldos.platform.core.entity.WoTrustMan;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.vo.IdentityMemberVO;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.RedisKeyEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.utils.ObjectUtils;
import com.wldos.framework.mvc.service.NonEntityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 身份登记册服务：admin/tadmin/can_trust 身份的权威读写入口（dadmin 由 DomainService 经 wo_domain_admin 管理）。
 * 登记册是身份判据；身份组（wo_org）仅作权限容器，新增/移除只写登记册，不投影 wo_org_user。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class IdentityRegistryService extends NonEntityService {

	/** 本服务管理的登记册类别（dadmin 走 wo_domain_admin，不在此列） */
	private static final List<String> REGISTRY_KINDS = Arrays.asList(
			Constants.AdminOrgCode, Constants.TAdminOrgCode, Constants.CanTrustOrgCode);

	/** 平台超管人数上限（可配 wldos.rbac.platform-admin.max） */
	@org.springframework.beans.factory.annotation.Value("${wldos.rbac.platform-admin.max:10}")
	private int platformAdminMax;

	@Autowired
	private PlatformAdminDao platformAdminDao;

	@Autowired
	private TenantAdminDao tenantAdminDao;

	@Autowired
	private TrustManDao trustManDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CompanyDao companyDao;

	@Autowired
	private DomainAdminDao domainAdminDao;

	@Autowired
	private DomainDao domainDao;

	/**
	 * 登记册成员清单。admin/can_trust/tadmin 查登记册联用户表；dadmin 查 wo_domain_admin 联用户与站点表。
	 *
	 * @param kind 身份类别（org_code：admin/tadmin/dadmin/can_trust）
	 * @param comName 租户名称模糊（tadmin 可选，与 comId 二选一）
	 * @param comId 目标租户 id（tadmin/dadmin 筛选，可空表示不限租户）
	 * @param domainId 目标站点id（仅 dadmin 级联用，可空）
	 * @return 成员视图列表
	 */
	public List<IdentityMemberVO> listMembers(String kind, String comName, Long comId, Long domainId) {
		if (Constants.DomainAdminOrgCode.equals(kind)) {
			return this.listDomainAdminMembers(comId, domainId);
		}
		if (!REGISTRY_KINDS.contains(kind)) {
			throw new BaseException("未知身份登记册类别：" + kind);
		}
		if (Constants.AdminOrgCode.equals(kind)) {
			return this.listPlatformAdminMembers();
		}
		if (Constants.TAdminOrgCode.equals(kind)) {
			return this.listTenantAdminMembers(comName, comId);
		}
		return this.listTrustManMembers();
	}

	private List<IdentityMemberVO> listPlatformAdminMembers() {
		List<WoPlatformAdmin> rows = this.platformAdminDao.findAllValid();
		if (ObjectUtils.isBlank(rows)) {
			return Collections.emptyList();
		}
		Map<Long, WoUser> users = this.loadActiveUsers(
				rows.stream().map(WoPlatformAdmin::getUserId).filter(Objects::nonNull).collect(Collectors.toList()));
		List<IdentityMemberVO> members = new ArrayList<>(rows.size());
		for (WoPlatformAdmin row : rows) {
			WoUser user = users.get(row.getUserId());
			if (user == null) {
				continue;
			}
			IdentityMemberVO vo = new IdentityMemberVO();
			vo.setId(row.getId());
			vo.setUserId(row.getUserId());
			vo.setLoginName(user.getLoginName());
			vo.setNickname(user.getNickname());
			vo.setCreateTime(row.getCreateTime());
			members.add(vo);
		}
		return members;
	}

	private List<IdentityMemberVO> listTrustManMembers() {
		List<WoTrustMan> rows = this.trustManDao.findAllValid();
		if (ObjectUtils.isBlank(rows)) {
			return Collections.emptyList();
		}
		Map<Long, WoUser> users = this.loadActiveUsers(
				rows.stream().map(WoTrustMan::getUserId).filter(Objects::nonNull).collect(Collectors.toList()));
		List<IdentityMemberVO> members = new ArrayList<>(rows.size());
		for (WoTrustMan row : rows) {
			WoUser user = users.get(row.getUserId());
			if (user == null) {
				continue;
			}
			IdentityMemberVO vo = new IdentityMemberVO();
			vo.setId(row.getId());
			vo.setUserId(row.getUserId());
			vo.setLoginName(user.getLoginName());
			vo.setNickname(user.getNickname());
			vo.setCreateTime(row.getCreateTime());
			members.add(vo);
		}
		return members;
	}

	private List<IdentityMemberVO> listTenantAdminMembers(String comName, Long filterComId) {
		List<WoTenantAdmin> rows = this.tenantAdminDao.findAllValid();
		if (ObjectUtils.isBlank(rows)) {
			return Collections.emptyList();
		}
		if (filterComId != null && filterComId > 0) {
			rows = rows.stream().filter(r -> Objects.equals(filterComId, r.getComId())).collect(Collectors.toList());
		}
		Map<Long, WoUser> users = this.loadActiveUsers(
				rows.stream().map(WoTenantAdmin::getUserId).filter(Objects::nonNull).collect(Collectors.toList()));
		Set<Long> comIds = rows.stream().map(WoTenantAdmin::getComId).filter(Objects::nonNull).collect(Collectors.toSet());
		Map<Long, WoCompany> companies = this.loadCompanies(comIds);
		String keyword = ObjectUtils.isBlank(comName) ? null : comName.trim().toLowerCase();
		List<IdentityMemberVO> members = new ArrayList<>(rows.size());
		for (WoTenantAdmin row : rows) {
			WoUser user = users.get(row.getUserId());
			if (user == null) {
				continue;
			}
			WoCompany company = companies.get(row.getComId());
			String name = company != null ? company.getComName() : null;
			if (keyword != null && (name == null || !name.toLowerCase().contains(keyword))) {
				continue;
			}
			IdentityMemberVO vo = new IdentityMemberVO();
			vo.setId(row.getId());
			vo.setUserId(row.getUserId());
			vo.setLoginName(user.getLoginName());
			vo.setNickname(user.getNickname());
			vo.setCreateTime(row.getCreateTime());
			vo.setComId(row.getComId());
			if (company != null) {
				vo.setComName(name);
			}
			members.add(vo);
		}
		return members;
	}

	private List<IdentityMemberVO> listDomainAdminMembers(Long comId, Long domainId) {
		List<WoDomainAdmin> rows = this.domainAdminDao.findAllValid();
		if (ObjectUtils.isBlank(rows)) {
			return Collections.emptyList();
		}
		if (comId != null && comId > 0) {
			rows = rows.stream().filter(r -> Objects.equals(comId, r.getComId())).collect(Collectors.toList());
		}
		if (domainId != null && domainId > 0) {
			rows = rows.stream().filter(r -> Objects.equals(domainId, r.getDomainId())).collect(Collectors.toList());
		}
		if (ObjectUtils.isBlank(rows)) {
			return Collections.emptyList();
		}
		Map<Long, WoUser> users = this.loadActiveUsers(
				rows.stream().map(WoDomainAdmin::getUserId).filter(Objects::nonNull).collect(Collectors.toList()));
		Set<Long> domainIds = rows.stream().map(WoDomainAdmin::getDomainId).filter(Objects::nonNull)
				.collect(Collectors.toSet());
		Map<Long, WoDomain> domains = this.loadDomains(domainIds);
		List<IdentityMemberVO> members = new ArrayList<>(rows.size());
		for (WoDomainAdmin row : rows) {
			WoUser user = users.get(row.getUserId());
			if (user == null) {
				continue;
			}
			IdentityMemberVO vo = new IdentityMemberVO();
			vo.setId(row.getId());
			vo.setUserId(row.getUserId());
			vo.setLoginName(user.getLoginName());
			vo.setNickname(user.getNickname());
			vo.setComId(row.getComId());
			vo.setDomainId(row.getDomainId());
			WoDomain domain = domains.get(row.getDomainId());
			if (domain != null) {
				vo.setDomainName(domain.getSiteName());
			}
			vo.setCreateTime(row.getCreateTime());
			members.add(vo);
		}
		return members;
	}

	private Map<Long, WoUser> loadActiveUsers(List<Long> userIds) {
		if (ObjectUtils.isBlank(userIds)) {
			return Collections.emptyMap();
		}
		List<WoUser> users = this.userDao.findByIdsActive(userIds);
		Map<Long, WoUser> map = new HashMap<>(users.size() * 2);
		for (WoUser user : users) {
			map.put(user.getId(), user);
		}
		return map;
	}

	private Map<Long, WoCompany> loadCompanies(Set<Long> comIds) {
		if (ObjectUtils.isBlank(comIds)) {
			return Collections.emptyMap();
		}
		Map<Long, WoCompany> map = new HashMap<>(comIds.size() * 2);
		this.companyDao.findAllById(comIds).forEach(c -> map.put(c.getId(), c));
		return map;
	}

	private Map<Long, WoDomain> loadDomains(Set<Long> domainIds) {
		if (ObjectUtils.isBlank(domainIds)) {
			return Collections.emptyMap();
		}
		Map<Long, WoDomain> map = new HashMap<>(domainIds.size() * 2);
		this.domainDao.findAllById(domainIds).forEach(d -> map.put(d.getId(), d));
		return map;
	}

	/**
	 * 添加平台超管：写登记册；超管人数受上限约束。
	 */
	@Transactional
	public String addPlatformAdmin(List<String> userIds, Long curUserId, String uip) {
		if (ObjectUtils.isBlank(userIds)) {
			return "";
		}
		long existing = this.platformAdminDao.findAllValid().size();
		if (existing + userIds.size() > this.platformAdminMax) {
			throw new BaseException("超级管理员人数已达上限（" + this.platformAdminMax + "），不能再添加");
		}
		StringBuilder mes = new StringBuilder(50);
		List<String> toAdd = new ArrayList<>();
		for (String sid : userIds) {
			Long userId = Long.parseLong(sid);
			if (this.platformAdminDao.findByUserId(userId) != null) {
				mes.append("用户：").append(userId).append(" 已是超级管理员，跳过；");
				continue;
			}
			WoPlatformAdmin row = new WoPlatformAdmin();
			row.setId(this.nextId());
			row.setUserId(userId);
			row.setIsValid(ValidStatusEnum.VALID.toString());
			row.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			row.setCreateBy(curUserId);
			row.setCreateIp(uip);
			row.setUpdateBy(curUserId);
			row.setUpdateIp(uip);
			this.platformAdminDao.save(row);
			toAdd.add(sid);
		}
		this.cache.remove(RedisKeyEnum.WLDOS_ADMIN.toString());
		return mes.toString();
	}

	/**
	 * 移除平台超管：软删登记册；至少留一人。
	 */
	@Transactional
	public void removePlatformAdmin(List<Long> ids, Long curUserId, String uip) {
		if (ObjectUtils.isBlank(ids)) {
			return;
		}
		List<WoPlatformAdmin> all = this.platformAdminDao.findAllValid();
		long remaining = all.stream().map(WoPlatformAdmin::getUserId).filter(uid -> !ids.contains(uid)).count();
		if (remaining < 1) {
			throw new BaseException("必须保留至少一名超级管理员，不能全部移除");
		}
		for (Long userId : ids) {
			WoPlatformAdmin row = this.platformAdminDao.findByUserId(userId);
			if (row != null) {
				row.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
				row.setUpdateBy(curUserId);
				row.setUpdateIp(uip);
				this.platformAdminDao.save(row);
			}
		}
		this.cache.remove(RedisKeyEnum.WLDOS_ADMIN.toString());
	}

	/**
	 * 添加守信人：写登记册。
	 */
	@Transactional
	public String addTrustMan(List<String> userIds, Long curUserId, String uip) {
		if (ObjectUtils.isBlank(userIds)) {
			return "";
		}
		StringBuilder mes = new StringBuilder(50);
		List<String> toAdd = new ArrayList<>();
		for (String sid : userIds) {
			Long userId = Long.parseLong(sid);
			if (this.trustManDao.findByUserId(userId) != null) {
				mes.append("用户：").append(userId).append(" 已是可信会员，跳过；");
				continue;
			}
			WoTrustMan row = new WoTrustMan();
			row.setId(this.nextId());
			row.setUserId(userId);
			row.setIsValid(ValidStatusEnum.VALID.toString());
			row.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			row.setCreateBy(curUserId);
			row.setCreateIp(uip);
			row.setUpdateBy(curUserId);
			row.setUpdateIp(uip);
			this.trustManDao.save(row);
			toAdd.add(sid);
		}
		this.cache.remove(RedisKeyEnum.WLDOS_TRUST.toString());
		return mes.toString();
	}

	/**
	 * 移除守信人：软删登记册；可信会员可为空。
	 */
	@Transactional
	public void removeTrustMan(List<Long> ids, Long curUserId, String uip) {
		if (ObjectUtils.isBlank(ids)) {
			return;
		}
		for (Long userId : ids) {
			WoTrustMan row = this.trustManDao.findByUserId(userId);
			if (row != null) {
				row.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
				row.setUpdateBy(curUserId);
				row.setUpdateIp(uip);
				this.trustManDao.save(row);
			}
		}
		this.cache.remove(RedisKeyEnum.WLDOS_TRUST.toString());
	}

	/**
	 * 租户管理员登记册写入（由 CompanyService.addTenantAdmin 调用）；一人一租户，已存在则跳过。
	 */
	public void bindTenantAdminRegistry(Long userId, Long comId, Long operatorId, String operatorIp) {
		if (userId == null || comId == null) {
			return;
		}
		if (this.tenantAdminDao.findByUserId(userId) != null) {
			return;
		}
		WoTenantAdmin row = new WoTenantAdmin();
		row.setId(this.nextId());
		row.setUserId(userId);
		row.setComId(comId);
		row.setIsValid(ValidStatusEnum.VALID.toString());
		row.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		row.setCreateBy(operatorId);
		row.setCreateIp(operatorIp);
		row.setUpdateBy(operatorId);
		row.setUpdateIp(operatorIp);
		this.tenantAdminDao.save(row);
	}

	/**
	 * 租户管理员登记册移除（由 CompanyService.removeTenantAdmin 调用）。
	 */
	@Transactional
	public void unbindTenantAdminRegistry(List<Long> userIds, Long operatorId, String operatorIp) {
		if (ObjectUtils.isBlank(userIds)) {
			return;
		}
		for (Long userId : userIds) {
			WoTenantAdmin row = this.tenantAdminDao.findByUserId(userId);
			if (row != null) {
				row.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
				row.setUpdateBy(operatorId);
				row.setUpdateIp(operatorIp);
				this.tenantAdminDao.save(row);
			}
		}
	}
}
