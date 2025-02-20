/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.core.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.framework.service.RepoService;
import com.wldos.common.Constants;
import com.wldos.common.enums.BoolEnum;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.sys.base.entity.WoComUser;
import com.wldos.sys.base.entity.WoCompany;
import com.wldos.sys.base.repo.ComUserRepo;
import com.wldos.sys.base.repo.CompanyRepo;
import com.wldos.sys.core.entity.WoOrg;
import com.wldos.sys.core.entity.WoOrgUser;
import com.wldos.sys.core.repo.OrgRepo;
import com.wldos.sys.core.repo.OrgUserRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司管理service。
 *
 * @author 元悉宇宙
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CompanyService extends RepoService<CompanyRepo, WoCompany, Long> {

	private final OrgRepo orgRepo;

	private final OrgUserRepo orgUserRepo;

	private final ComUserRepo comUserRepo;

	public CompanyService(OrgRepo orgRepo, OrgUserRepo orgUserRepo, ComUserRepo comUserRepo) {
		this.orgRepo = orgRepo;
		this.orgUserRepo = orgUserRepo;
		this.comUserRepo = comUserRepo;
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
		// @todo 人员的添加应该经过本人同意，本人同意后执行关联操作，如果是首次添加设为主企业(不存在主企业)。
		// @todo 同时应该实现人员加入组织的功能，申请后需要租户管理员的同意，如果是首次添加设为主企业(不存在主企业)。
		// @todo 人员可以选择退出组织或退出企业，退出主企业时默认最后一个企业为主企业，租户管理员可以设置是否允许人员自行退出。
		// @todo 本操作应该加入消息队列支持。
		List<Long> ids = userIds.parallelStream().map(Long::parseLong).collect(Collectors.toList());
		// 判断已设置为本租户管理员的用户清单
		WoOrg org = this.orgRepo.findByOrgCodeAndIsValidAndDeleteFlag(Constants.TAdminOrgCode, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		List<WoOrgUser> orgUsers = this.orgUserRepo.queryAllByUserIds(org.getId(),ids, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		Map<Long, Long> userInOrg = orgUsers.parallelStream().collect(Collectors.toMap(WoOrgUser::getUserId, WoOrgUser::getUserComId));
		StringBuilder mes = new StringBuilder(50);
		List<WoOrgUser> orgUserList = ids.parallelStream().map(userId -> {

			if (userInOrg.containsKey(userId)) {
				mes.append("用户：").append(userId).append(" 已是租户管理员，一个用户最多只能管理一个租户");
				return null;
			}

			WoOrgUser orgUser = new WoOrgUser();
			orgUser.setOrgId(org.getId());
			orgUser.setUserId(userId);
			orgUser.setArchId(org.getArchId());
			orgUser.setComId(org.getComId());
			orgUser.setUserComId(userComId); // 超级管理员设置租户管理员或者其他类似身份时以及借调场景，用户归属公司不同于组织公司

			return orgUser;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		// 验证用户是否关联公司，不存在则创建主企业，存在但不存在当前企业则创建关联，存在当前企业则略过
		List<WoComUser> comUsers = this.comUserRepo.queryAllByUserIds(ids, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		Map<Long, List<WoComUser>> userComList = comUsers.parallelStream().collect(Collectors.groupingBy(WoComUser::getUserId));
		List<WoComUser> woComUsers = ids.parallelStream().map(userId -> {
			WoComUser comUser;
			boolean isCurCom = true; // 是否存在当前企业关联 或者 是否创建当前为主企业
			if (userComList.containsKey(userId) && (isCurCom = userComList.get(userId).stream().allMatch(com -> Objects.equals(userComId, com.getComId()))))
				return null;

			comUser = new WoComUser();
			comUser.setComId(userComId);
			comUser.setUserId(userId);
			comUser.setIsMain(isCurCom ? BoolEnum.YES.toString() : BoolEnum.NO.toString());

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
