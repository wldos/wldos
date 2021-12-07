/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.support.Constants;
import com.wldos.support.controller.EntityAssists;
import com.wldos.support.enums.BoolEnum;
import com.wldos.support.enums.DeleteFlagEnum;
import com.wldos.support.enums.ValidStatusEnum;
import com.wldos.support.service.BaseService;
import com.wldos.system.core.entity.WoComUser;
import com.wldos.system.core.entity.WoCompany;
import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.core.entity.WoOrgUser;
import com.wldos.system.core.repo.ComUserRepo;
import com.wldos.system.core.repo.CompanyRepo;
import com.wldos.system.core.repo.OrgRepo;
import com.wldos.system.core.repo.OrgUserRepo;
import com.wldos.system.enums.RedisKeyEnum;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司管理service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CompanyService extends BaseService<CompanyRepo, WoCompany, Long> {

	private final OrgRepo orgRepo;

	private final OrgUserRepo orgUserRepo;

	private final ComUserRepo comUserRepo;

	public CompanyService(OrgRepo orgRepo, OrgUserRepo orgUserRepo, ComUserRepo comUserRepo) {
		this.orgRepo = orgRepo;
		this.orgUserRepo = orgUserRepo;
		this.comUserRepo = comUserRepo;
	}

	public String addTenantAdmin(List<String> userIds, Long userComId, Long curUserId, String uip) {
		List<Long> ids = userIds.parallelStream().map(Long::parseLong).collect(Collectors.toList());

		WoOrg org = this.orgRepo.findByOrgCodeAndIsValidAndDeleteFlag(Constants.TAdminOrgCode, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		List<WoOrgUser> orgUsers = this.orgUserRepo.queryAllByUserIds(org.getId(), userComId, ids, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		Map<Long, Long> userInOrg = orgUsers.parallelStream().collect(Collectors.toMap(WoOrgUser::getUserId, WoOrgUser::getUserId));
		StringBuilder mes = new StringBuilder(50);
		List<WoOrgUser> orgUserList = ids.parallelStream().map(userId -> {

			if (userInOrg.containsKey(userId)) {
				mes.append("用户：").append(userId).append(" 已是管理员; ");
				return null;
			}

			WoOrgUser orgUser = new WoOrgUser();
			orgUser.setOrgId(org.getId());
			orgUser.setUserId(userId);
			EntityAssists.beforeInsert(orgUser, this.nextId(), curUserId, uip, true);
			orgUser.setArchId(org.getArchId());
			orgUser.setComId(org.getComId());
			orgUser.setUserComId(userComId);

			return orgUser;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		List<WoComUser> comUsers = this.comUserRepo.queryAllByUserIds(ids, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		Map<Long, List<WoComUser>> userComList = comUsers.parallelStream().collect(Collectors.groupingBy(WoComUser::getUserId));
		List<WoComUser> woComUsers = ids.parallelStream().map(userId -> {
			WoComUser comUser;
			if (userComList.containsKey(userId))
				return null;

			comUser = new WoComUser();
			comUser.setComId(userComId);
			comUser.setUserId(userId);
			comUser.setIsMain(userComList.isEmpty() ? BoolEnum.YES.toString() : BoolEnum.NO.toString());
			EntityAssists.beforeInsert(comUser, this.nextId(), curUserId, uip, true);

			return comUser;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (!orgUserList.isEmpty())
			this.orgUserRepo.saveAll(orgUserList);
		if (!woComUsers.isEmpty())
			this.comUserRepo.saveAll(woComUsers);

		return mes.toString();
	}

	public void removeTenantAdmin(List<Long> ids, Long userComId) {
		WoOrg org = this.orgRepo.findByOrgCodeAndIsValidAndDeleteFlag(Constants.TAdminOrgCode, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		this.orgUserRepo.removeTenantAdmin(ids, userComId, org.getId());
	}
}
