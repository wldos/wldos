/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.auth.vo.Group;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.vo.Org;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 组织结构仓库操作类。
 *
 * @author Yuanxi Universe
 * @date 2021/4/27
 * @version 1.0
 */
public interface OrgDao extends BaseDao<WoOrg, Long> {
	@Query("select o.* from wo_org o where o.delete_flag=:deleteFlag and o.is_valid=:isValid and o.org_type=:orgType")
	List<Org> findAllByOrgType(String deleteFlag, String isValid, String orgType);

	WoOrg findByOrgCodeAndIsValidAndDeleteFlag(String orgCode, String isValid, String deleteFlag);

	@Query("select g.* from wo_org g join wo_org_user u on g.id=u.org_id and g.arch_id=u.arch_id and g.com_id=u.com_id where g.org_type='platform' and g.is_valid='1' and g.delete_flag='normal' and u.user_id=:userId and u.is_valid='1' and u.delete_flag='normal'")
	List<Group> findAllByUserId(Long userId);

	/** 用户归属的全部组织（不限 org_type），按加入顺序。 */
	@Query("select o.* from wo_org o join wo_org_user u on o.id=u.org_id and o.arch_id=u.arch_id and o.com_id=u.com_id "
			+ "where u.user_id=:userId and u.delete_flag='normal' and u.is_valid='1' and o.delete_flag='normal' and o.is_valid='1' order by u.id")
	List<WoOrg> queryByUserId(@Param("userId") Long userId);

	/**
	 * 系统保留身份组存在性自愈校验用：查出给定 org_code 集合中，当前实际存在（有效、未删除）的 org_code。
	 * 与传入集合求差集即为缺失、需要重建定义行的身份组。
	 */
	@Query("select org_code from wo_org where delete_flag='normal' and is_valid='1' and com_id=:comId and org_code in (:orgCodes)")
	List<String> findExistingSystemOrgCodes(@Param("comId") Long comId, @Param("orgCodes") List<String> orgCodes);

	/** 身份组（admin/tadmin/dadmin/can_trust 等系统保留 org_code）的全部有效行 id，多态绑定迁移用 */
	@Query("select id from wo_org where delete_flag='normal' and is_valid='1' and org_code in (:orgCodes)")
	List<Long> findIdsByOrgCodes(@Param("orgCodes") List<String> orgCodes);

	/** 按 org_code 集合查有效组织行 */
	@Query("select o.* from wo_org o where o.delete_flag='normal' and o.is_valid='1' and o.org_code in (:orgCodes)")
	List<WoOrg> findByOrgCodes(@Param("orgCodes") List<String> orgCodes);

	@Query("select o.* from wo_org o where o.delete_flag='normal' and o.is_valid='1' and o.com_id=:comId and o.domain_id=:domainId and o.org_code=:orgCode limit 1")
	WoOrg findByComIdAndDomainIdAndOrgCode(
			@Param("comId") Long comId,
			@Param("domainId") Long domainId,
			@Param("orgCode") String orgCode);

	/** 平台模板组织（com_id=0、无站点），供会员分群/角色模板回退解析 */
	@Query("select o.* from wo_org o where o.delete_flag='normal' and o.is_valid='1' and o.com_id=0 and o.domain_id is null and o.org_code=:orgCode limit 1")
	WoOrg findPlatformTemplateByOrgCode(@Param("orgCode") String orgCode);

	/** 平台门户站点（com_id=0）上的有效会员分群 */
	@Query("select o.* from wo_org o inner join wo_domain d on o.domain_id = d.id "
			+ "where o.delete_flag='normal' and o.is_valid='1' and d.delete_flag='normal' and d.is_valid='1' "
			+ "and d.com_id=0 and o.org_type=:orgType")
	List<WoOrg> findValidSegmentsOnCatalogDomains(@Param("orgType") String orgType);

	/** 平台门户站点上的内置会员分群（如 freeuser 等） */
	@Query("select o.* from wo_org o inner join wo_domain d on o.domain_id = d.id "
			+ "where o.delete_flag='normal' and o.is_valid='1' and d.delete_flag='normal' and d.is_valid='1' "
			+ "and d.com_id=0 and o.org_code in (:orgCodes)")
	List<WoOrg> findValidMemberKindsOnCatalogDomains(@Param("orgCodes") List<String> orgCodes);

	/**
	 * 租户站上 com_id=0 的会员分群重复行，且同站已有 com_id=站点租户 的正式行（apply 时软删重复行）。
	 */
	@Query("select o.* from wo_org o inner join wo_domain d on o.domain_id = d.id "
			+ "where o.delete_flag='normal' and o.is_valid='1' and d.delete_flag='normal' and d.is_valid='1' "
			+ "and o.com_id = 0 and o.domain_id is not null and d.com_id > 0 and o.org_code in (:orgCodes) "
			+ "and exists (select 1 from wo_org o2 where o2.delete_flag='normal' and o2.is_valid='1' "
			+ "and o2.domain_id = o.domain_id and o2.org_code = o.org_code and o2.com_id = d.com_id)")
	List<WoOrg> findValidWrongAxisMemberKindDuplicates(@Param("orgCodes") List<String> orgCodes);

	/** 租户站上仅有 com_id=0 的会员分群、缺少租户轴正式行（仅报告，apply 时回填 com_id） */
	@Query("select o.* from wo_org o inner join wo_domain d on o.domain_id = d.id "
			+ "where o.delete_flag='normal' and o.is_valid='1' and d.delete_flag='normal' and d.is_valid='1' "
			+ "and o.com_id = 0 and o.domain_id is not null and d.com_id > 0 and o.org_code in (:orgCodes) "
			+ "and not exists (select 1 from wo_org o2 where o2.delete_flag='normal' and o2.is_valid='1' "
			+ "and o2.domain_id = o.domain_id and o2.org_code = o.org_code and o2.com_id = d.com_id)")
	List<WoOrg> findValidWrongAxisMemberKindsWithoutCanonical(@Param("orgCodes") List<String> orgCodes);

	/** 会员分群指向无效站点（仅报告，apply 时软删） */
	@Query("select o.* from wo_org o where o.delete_flag='normal' and o.is_valid='1' "
			+ "and o.com_id = 0 and o.domain_id is not null and o.org_code in (:orgCodes) "
			+ "and not exists (select 1 from wo_domain d where d.id = o.domain_id "
			+ "and d.delete_flag='normal' and d.is_valid='1')")
	List<WoOrg> findValidWrongAxisMemberKindsOrphanDomain(@Param("orgCodes") List<String> orgCodes);

	/** 租户编制组织（非站点分群）缺少 arch_id */
	@Query("select o.* from wo_org o where o.delete_flag='normal' and o.is_valid='1' "
			+ "and o.com_id > 0 and (o.arch_id is null or o.arch_id = 0) "
			+ "and (o.domain_id is null or o.domain_id <= 0) "
			+ "and o.org_code not in (:kindOrgCodes)")
	List<WoOrg> findValidTenantStaffOrgsMissingArch(@Param("kindOrgCodes") List<String> kindOrgCodes);

	@Query("select count(1) from wo_org o where o.delete_flag='normal' and o.is_valid='1' and o.parent_id=:parentId")
	long countNormalChildrenByParentId(@Param("parentId") Long parentId);
}
