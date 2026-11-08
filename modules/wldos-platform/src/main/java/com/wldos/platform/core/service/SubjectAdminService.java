/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.dao.SubjectAssociationDao;
import com.wldos.platform.core.dao.SubjectAuthenticationDao;
import com.wldos.platform.core.dao.SubjectDao;
import com.wldos.platform.core.dao.UserDao;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.entity.WoSubject;
import com.wldos.platform.core.entity.WoSubjectAssociation;
import com.wldos.platform.core.entity.WoSubjectAuthentication;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.vo.SubjectAuthBody;
import com.wldos.platform.core.vo.SubjectAuthVO;
import com.wldos.platform.core.vo.SubjectGrantBody;
import com.wldos.platform.core.vo.SubjectGrantVO;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.DateUtils;
import io.github.wldos.common.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 主体管理：主体类型字典（平台级只读）、主体认证（用户↔主体类型）、主体授权（主体类型↔角色）。
 * 主体认证/授权：超管管全平台，租户管理员管本租户；变更后刷新鉴权缓存。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class SubjectAdminService extends NonEntityService {

	@Autowired
	private SubjectDao subjectDao;

	@Autowired
	private SubjectAuthenticationDao subjectAuthDao;

	@Autowired
	private SubjectAssociationDao subjectAssocDao;

	@Autowired
	private UserDao userRepo;

	@Autowired
	private RoleDao roleRepo;

	@Autowired
	private DomainDao domainRepo;

	/** 主体类型字典（平台级，全部有效类型） */
	public List<WoSubject> listTypes() {
		List<WoSubject> all = new ArrayList<>();
		this.subjectDao.findAll().forEach(all::add);
		return all.stream()
				.filter(t -> DeleteFlagEnum.NORMAL.toString().equals(t.getDeleteFlag())
						&& ValidStatusEnum.VALID.toString().equals(t.getIsValid()))
				.collect(Collectors.toList());
	}

	public List<SubjectAuthVO> listAuthentications(Long comId, Long domainId) {
		Long scopeComId = this.assertScopeAndResolveComId(comId);
		this.assertReadableDomainId(scopeComId, domainId);
		Map<Long, String> typeNames = this.typeNameIndex();
		List<SubjectAuthVO> rows = new ArrayList<>();
		for (WoSubjectAuthentication row : this.subjectAuthDao.findValidByCom(scopeComId)) {
			if (!this.visibleToOperator(row.getDomainId(), domainId)) {
				continue;
			}
			SubjectAuthVO vo = new SubjectAuthVO();
			vo.setId(row.getId());
			vo.setSubjectTypeId(row.getSubjectTypeId());
			vo.setSubjectTypeName(typeNames.get(row.getSubjectTypeId()));
			vo.setUserId(row.getUserId());
			WoUser user = row.getUserId() != null ? this.userRepo.findById(row.getUserId()).orElse(null) : null;
			if (user != null) {
				vo.setLoginName(user.getLoginName());
				vo.setNickname(user.getNickname());
			}
			vo.setComId(row.getComId());
			Long rowDomainId = row.getDomainId() != null && row.getDomainId() > 0 ? row.getDomainId() : null;
			vo.setDomainId(rowDomainId);
			if (rowDomainId != null) {
				WoDomain domain = this.domainRepo.findById(rowDomainId).orElse(null);
				vo.setDomainName(domain != null ? domain.getSiteName() : null);
			}
			vo.setCreateTime(row.getCreateTime());
			rows.add(vo);
		}
		return rows;
	}

	@Transactional(rollbackFor = Exception.class)
	public void addAuthentication(SubjectAuthBody body) {
		if (body == null || body.getSubjectTypeId() == null || body.getUserId() == null) {
			throw new BaseException("主体类型与用户不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long domainId = body.getDomainId() != null && body.getDomainId() > 0 ? body.getDomainId() : 0L;
		WoDomain domain = null;
		if (domainId > 0) {
			domain = this.domainRepo.findById(domainId).orElse(null);
			if (domain == null) {
				throw new BaseException("目标站点不存在", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
		Long comId = this.assertScopeAndResolveComId(body.getComId() != null && body.getComId() > 0
				? body.getComId() : (domain != null ? domain.getComId() : null));
		this.assertWritableDomainId(comId, domainId);
		if (domain != null && !Objects.equals(domain.getComId() != null ? domain.getComId() : Constants.TOP_COM_ID, comId)) {
			throw new BaseException("目标站点不属于当前租户或站点", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!this.typeNameIndex().containsKey(body.getSubjectTypeId())) {
			throw new BaseException("主体类型不存在或已停用", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoUser user = this.userRepo.findById(body.getUserId()).orElse(null);
		if (user == null || !DeleteFlagEnum.NORMAL.toString().equals(user.getDeleteFlag())) {
			throw new BaseException("目标用户不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		final Long storeDomainId = domainId;
		boolean exists = this.subjectAuthDao.findValidByCom(comId).stream()
				.anyMatch(r -> Objects.equals(r.getSubjectTypeId(), body.getSubjectTypeId())
						&& Objects.equals(r.getUserId(), body.getUserId())
						&& Objects.equals(r.getComId() != null ? r.getComId() : Constants.TOP_COM_ID, comId)
						&& Objects.equals(r.getDomainId() != null ? r.getDomainId() : 0L, storeDomainId));
		if (exists) {
			return;
		}
		WoSubjectAuthentication row = new WoSubjectAuthentication();
		row.setId(this.nextId());
		row.setSubjectTypeId(body.getSubjectTypeId());
		row.setUserId(body.getUserId());
		row.setComId(comId);
		row.setDomainId(storeDomainId);
		row.setIsValid(ValidStatusEnum.VALID.toString());
		row.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		row.setCreateBy(this.commonOperate.getUserId());
		row.setCreateIp(this.commonOperate.getUserIp());
		row.setCreateTime(DateUtils.convSQLDate(new Date()));
		this.subjectAuthDao.save(row);
		this.refreshAuth();
	}

	@Transactional(rollbackFor = Exception.class)
	public void removeAuthentication(Long id) {
		if (id == null) {
			throw new BaseException("认证id不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoSubjectAuthentication row = this.subjectAuthDao.findById(id).orElse(null);
		if (row == null || !DeleteFlagEnum.NORMAL.toString().equals(row.getDeleteFlag())) {
			return;
		}
		this.assertScopeAndResolveComId(row.getComId());
		row.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
		row.setUpdateBy(this.commonOperate.getUserId());
		row.setUpdateIp(this.commonOperate.getUserIp());
		row.setUpdateTime(DateUtils.convSQLDate(new Date()));
		this.subjectAuthDao.save(row);
		this.refreshAuth();
	}

	public List<SubjectGrantVO> listAssociations(Long comId, Long domainId) {
		Long scopeComId = this.assertScopeAndResolveComId(comId);
		this.assertReadableDomainId(scopeComId, domainId);
		Map<Long, String> typeNames = this.typeNameIndex();
		List<SubjectGrantVO> rows = new ArrayList<>();
		for (WoSubjectAssociation row : this.subjectAssocDao.findValidByCom(scopeComId)) {
			if (!this.visibleToOperator(row.getDomainId(), domainId)) {
				continue;
			}
			SubjectGrantVO vo = new SubjectGrantVO();
			vo.setId(row.getId());
			vo.setSubjectTypeId(row.getSubjectTypeId());
			vo.setSubjectTypeName(typeNames.get(row.getSubjectTypeId()));
			vo.setRoleId(row.getRoleId());
			WoRole role = row.getRoleId() != null ? this.roleRepo.findById(row.getRoleId()).orElse(null) : null;
			vo.setRoleName(role != null ? role.getRoleName() : null);
			vo.setComId(row.getComId());
			vo.setDomainId(row.getDomainId() != null && row.getDomainId() > 0 ? row.getDomainId() : null);
			if (vo.getDomainId() != null) {
				WoDomain domain = this.domainRepo.findById(vo.getDomainId()).orElse(null);
				vo.setDomainName(domain != null ? domain.getSiteName() : null);
			}
			vo.setCreateTime(row.getCreateTime());
			rows.add(vo);
		}
		return rows;
	}

	@Transactional(rollbackFor = Exception.class)
	public void addAssociation(SubjectGrantBody body) {
		if (body == null || body.getSubjectTypeId() == null || body.getRoleIds() == null || body.getRoleIds().isEmpty()) {
			throw new BaseException("主体类型与角色不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long domainId = body.getDomainId() != null && body.getDomainId() > 0 ? body.getDomainId() : 0L;
		WoDomain domain = null;
		if (domainId > 0) {
			domain = this.domainRepo.findById(domainId).orElse(null);
			if (domain == null) {
				throw new BaseException("目标站点不存在", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
		// 未选站点时按所选站点推导租户
		Long comId = this.assertScopeAndResolveComId(body.getComId() != null && body.getComId() > 0
				? body.getComId() : (domain != null ? domain.getComId() : null));
		this.assertWritableDomainId(comId, domainId);
		if (!this.typeNameIndex().containsKey(body.getSubjectTypeId())) {
			throw new BaseException("主体类型不存在或已停用", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (domain != null && !Objects.equals(domain.getComId() != null ? domain.getComId() : Constants.TOP_COM_ID, comId)) {
			throw new BaseException("目标站点不属于当前租户或站点", ResultCode.VALIDATION_ERROR.getCode());
		}
		List<WoSubjectAssociation> validRows = this.subjectAssocDao.findValidByCom(comId);
		Timestamp now = DateUtils.convSQLDate(new Date());
		boolean changed = false;
		for (Long roleId : body.getRoleIds()) {
			if (roleId == null) {
				continue;
			}
			WoRole role = this.roleRepo.findById(roleId).orElse(null);
			if (role == null || !DeleteFlagEnum.NORMAL.toString().equals(role.getDeleteFlag())
					|| !Objects.equals(role.getComId(), comId)) {
				throw new BaseException("目标角色不存在或不属于当前租户或站点", ResultCode.VALIDATION_ERROR.getCode());
			}
			boolean exists = validRows.stream()
					.anyMatch(r -> Objects.equals(r.getSubjectTypeId(), body.getSubjectTypeId())
							&& Objects.equals(r.getRoleId(), roleId)
							&& Objects.equals(r.getComId() != null ? r.getComId() : Constants.TOP_COM_ID, comId)
							&& Objects.equals(r.getDomainId() != null ? r.getDomainId() : 0L, domainId));
			if (exists) {
				continue;
			}
			WoSubjectAssociation row = new WoSubjectAssociation();
			row.setId(this.nextId());
			row.setSubjectTypeId(body.getSubjectTypeId());
			row.setRoleId(roleId);
			row.setComId(comId);
			row.setDomainId(domainId);
			row.setIsValid(ValidStatusEnum.VALID.toString());
			row.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			row.setCreateBy(this.commonOperate.getUserId());
			row.setCreateIp(this.commonOperate.getUserIp());
			row.setCreateTime(now);
			this.subjectAssocDao.save(row);
			changed = true;
		}
		if (changed) {
			this.refreshAuth();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void removeAssociation(Long id) {
		if (id == null) {
			throw new BaseException("授权id不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoSubjectAssociation row = this.subjectAssocDao.findById(id).orElse(null);
		if (row == null || !DeleteFlagEnum.NORMAL.toString().equals(row.getDeleteFlag())) {
			return;
		}
		this.assertScopeAndResolveComId(row.getComId());
		row.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
		row.setUpdateBy(this.commonOperate.getUserId());
		row.setUpdateIp(this.commonOperate.getUserIp());
		row.setUpdateTime(DateUtils.convSQLDate(new Date()));
		this.subjectAssocDao.save(row);
		this.refreshAuth();
	}

	private Map<Long, String> typeNameIndex() {
		Map<Long, String> index = new HashMap<>();
		for (WoSubject type : this.listTypes()) {
			index.put(type.getId(), type.getSubjectTypeName());
		}
		return index;
	}

	/** 校验管理范围并解析生效租户。 */
	private Long assertScopeAndResolveComId(Long requestedComId) {
		Long userId = this.commonOperate.getUserId();
		if (this.commonOperate.isAdmin(userId)) {
			return requestedComId != null ? requestedComId : Constants.TOP_COM_ID;
		}
		Long tenantId = this.commonOperate.getOperatorComId(userId);
		if (tenantId == null || tenantId <= 0) {
			throw new BaseException("无权管理主体", ResultCode.FORBIDDEN.getCode());
		}
		if (!this.commonOperate.isTenantAdmin(userId) && !this.commonOperate.isDomainAdmin(userId)) {
			throw new BaseException("无权管理主体", ResultCode.FORBIDDEN.getCode());
		}
		if (requestedComId != null && requestedComId > 0 && !requestedComId.equals(tenantId)) {
			throw new BaseException("无权管理其他租户的主体", ResultCode.FORBIDDEN.getCode());
		}
		return tenantId;
	}

	/** 列表/读操作：校验目标站点在操作者可读范围内 */
	private void assertReadableDomainId(Long scopeComId, Long domainId) {
		if (domainId == null || domainId <= 0) {
			return;
		}
		WoDomain domain = this.domainRepo.findById(domainId).orElse(null);
		if (domain == null) {
			throw new BaseException("目标站点不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long domainComId = domain.getComId();
		if (domainComId == null) {
			throw new BaseException("目标站点未绑定租户", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!Objects.equals(domainComId, scopeComId)) {
			throw new BaseException("目标站点不属于当前租户或站点", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long userId = this.commonOperate.getUserId();
		if (this.commonOperate.isAdmin(userId) || this.commonOperate.isTenantAdmin(userId)) {
			return;
		}
		if (this.commonOperate.isDomainAdmin(userId)) {
			Set<Long> bound = new HashSet<>(this.commonOperate.listBoundDomainIds(userId));
			if (!bound.contains(domainId)) {
				throw new BaseException("无权访问该站点数据", ResultCode.FORBIDDEN.getCode());
			}
		}
	}

	/** 写操作：站点管理员仅能在绑定站点（或非站点级）写入 */
	private void assertWritableDomainId(Long scopeComId, long domainId) {
		if (domainId <= 0) {
			Long userId = this.commonOperate.getUserId();
			if (this.commonOperate.isDomainAdmin(userId) && !this.commonOperate.isTenantAdmin(userId)) {
				throw new BaseException("站点管理员请指定生效站点", ResultCode.VALIDATION_ERROR.getCode());
			}
			return;
		}
		this.assertReadableDomainId(scopeComId, domainId);
	}

	private boolean visibleToOperator(Long rowDomainId, Long filterDomainId) {
		if (!this.matchesDomainFilter(rowDomainId, filterDomainId)) {
			return false;
		}
		Long userId = this.commonOperate.getUserId();
		if (this.commonOperate.isAdmin(userId) || this.commonOperate.isTenantAdmin(userId)) {
			return true;
		}
		if (!this.commonOperate.isDomainAdmin(userId)) {
			return true;
		}
		long rowDom = rowDomainId != null && rowDomainId > 0 ? rowDomainId : 0L;
		if (rowDom == 0L) {
			return true;
		}
		return this.commonOperate.listBoundDomainIds(userId).contains(rowDom);
	}

	/** 租户级（domainId=0）在各站点视图均可见；站点级仅匹配当前目标站 */
	private boolean matchesDomainFilter(Long rowDomainId, Long filterDomainId) {
		if (filterDomainId == null || filterDomainId <= 0) {
			return true;
		}
		long rowDom = rowDomainId != null && rowDomainId > 0 ? rowDomainId : 0L;
		if (rowDom == 0L) {
			return true;
		}
		return rowDom == filterDomainId;
	}
}
