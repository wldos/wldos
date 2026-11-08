/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.List;

import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.OrgUserDao;
import com.wldos.platform.core.dao.PlatformAdminDao;
import com.wldos.platform.core.dao.TenantAdminDao;
import com.wldos.platform.core.dao.TrustManDao;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoOrgUser;
import com.wldos.platform.core.entity.WoPlatformAdmin;
import com.wldos.platform.core.entity.WoTenantAdmin;
import com.wldos.platform.core.entity.WoTrustMan;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.framework.common.IDGen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 身份登记册存量迁移：启动时把 admin/tadmin/can_trust 身份组成员幂等回填到登记册表。
 * 登记册与组成员双写并行期间，本 Runner 同时承担对账自愈：组有成员而登记册缺行时补登记册。
 * 身份登记册数据迁移：已存在则跳过，每次启动执行以保持与登记册一致。
 *
 * <p>开关：{@code wldos.rbac.identity-registry.migration-enabled=false} 可关闭。</p>
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Component
public class IdentityRegistryMigrationBootstrap implements ApplicationRunner {

	@Autowired
	private OrgDao orgDao;

	@Autowired
	private OrgUserDao orgUserRepo;

	@Autowired
	private PlatformAdminDao platformAdminDao;

	@Autowired
	private TenantAdminDao tenantAdminDao;

	@Autowired
	private TrustManDao trustManDao;

	@Value("${wldos.rbac.identity-registry.migration-enabled:true}")
	private boolean enabled;

	@Override
	public void run(ApplicationArguments args) {
		if (!this.enabled) {
			log.info("[identity.registry] 迁移已关闭（wldos.rbac.identity-registry.migration-enabled=false）");
			return;
		}
		try {
			int admin = this.backfillPlatformAdmin();
			int tenant = this.backfillTenantAdmin();
			int trust = this.backfillTrustMan();
			if (admin + tenant + trust > 0) {
				log.warn("[identity.registry] 登记册回填完成：admin 新增 {} 行，tadmin 新增 {} 行，can_trust 新增 {} 行", admin, tenant, trust);
			}
			else {
				log.info("[identity.registry] 登记册与身份组成员一致，无需回填");
			}
		}
		catch (Exception e) {
			log.error("[identity.registry] 登记册迁移异常（不影响启动，身份判定仍走身份组旧链路）: {}", e.getMessage(), e);
		}
	}

	private int backfillPlatformAdmin() {
		WoOrg org = this.findIdentityOrg(Constants.AdminOrgCode);
		if (org == null) {
			return 0;
		}
		int added = 0;
		for (WoOrgUser member : this.orgUserRepo.existsAdmin(Constants.TOP_COM_ID, Constants.AdminOrgCode, org.getId())) {
			if (member.getUserId() == null || this.platformAdminDao.findByUserId(member.getUserId()) != null) {
				continue;
			}
			WoPlatformAdmin row = new WoPlatformAdmin();
			row.setId(IDGen.nextId());
			row.setUserId(member.getUserId());
			this.fillCommon(row);
			this.platformAdminDao.save(row);
			added++;
		}
		return added;
	}

	private int backfillTenantAdmin() {
		WoOrg org = this.findIdentityOrg(Constants.TAdminOrgCode);
		if (org == null) {
			return 0;
		}
		int added = 0;
		for (WoOrgUser member : this.orgUserRepo.existsAdmin(Constants.TOP_COM_ID, Constants.TAdminOrgCode, org.getId())) {
			if (member.getUserId() == null || this.tenantAdminDao.findByUserId(member.getUserId()) != null) {
				continue;
			}
			Long userComId = member.getUserComId();
			if (userComId == null || userComId <= 0) {
				log.error("[identity.registry] tadmin 组成员缺受托租户（user_com_id），跳过回填待人工核实：userId={}", member.getUserId());
				continue;
			}
			WoTenantAdmin row = new WoTenantAdmin();
			row.setId(IDGen.nextId());
			row.setUserId(member.getUserId());
			row.setComId(userComId);
			this.fillCommon(row);
			this.tenantAdminDao.save(row);
			added++;
		}
		return added;
	}

	private int backfillTrustMan() {
		WoOrg org = this.findIdentityOrg(Constants.CanTrustOrgCode);
		if (org == null) {
			return 0;
		}
		int added = 0;
		for (WoOrgUser member : this.orgUserRepo.existsAdmin(Constants.TOP_COM_ID, Constants.CanTrustOrgCode, org.getId())) {
			if (member.getUserId() == null || this.trustManDao.findByUserId(member.getUserId()) != null) {
				continue;
			}
			WoTrustMan row = new WoTrustMan();
			row.setId(IDGen.nextId());
			row.setUserId(member.getUserId());
			this.fillCommon(row);
			this.trustManDao.save(row);
			added++;
		}
		return added;
	}

	private WoOrg findIdentityOrg(String orgCode) {
		return this.orgDao.findByOrgCodeAndIsValidAndDeleteFlag(orgCode,
				ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
	}

	private void fillCommon(WoPlatformAdmin row) {
		row.setIsValid(ValidStatusEnum.VALID.toString());
		row.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		row.setCreateBy(Constants.SYSTEM_USER_ID);
	}

	private void fillCommon(WoTenantAdmin row) {
		row.setIsValid(ValidStatusEnum.VALID.toString());
		row.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		row.setCreateBy(Constants.SYSTEM_USER_ID);
	}

	private void fillCommon(WoTrustMan row) {
		row.setIsValid(ValidStatusEnum.VALID.toString());
		row.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		row.setCreateBy(Constants.SYSTEM_USER_ID);
	}
}
