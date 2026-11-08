/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;


import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoDomain;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 租户域管理仓库操作类。
 *
 * @author Yuanxi Universe
 * @date 2021/4/27
 * @version 1.0
 */
public interface DomainDao extends BaseDao<WoDomain, Long> {

	List<WoDomain> findAllByDeleteFlagAndIsValid(@Param("deleteFlag") String deleteFlag, @Param("isValid") String isValid);

	/**
	 * 根据域名查询域信息
	 *
	 * @param siteDomain 域名
	 * @param isValid 是否有效
	 * @param deleteFlag 是否删除
	 * @return 域实例
	 */
	WoDomain findBySiteDomainAndIsValidAndDeleteFlag(@Param("siteDomain") String siteDomain, @Param("isValid") String isValid, @Param("deleteFlag") String deleteFlag);

	@Query("select d.* from wo_domain d where d.delete_flag='normal' and d.is_valid='1' and d.com_id=0 order by d.display_order asc, d.id asc limit 1")
	WoDomain findPlatformTemplateDomain();

	@Query("select d.* from wo_domain d where d.delete_flag='normal' and d.is_valid='1' and d.com_id=:comId order by d.display_order asc, d.id asc limit 1")
	WoDomain findFirstByComId(@Param("comId") Long comId);

	@Query("select count(1) from wo_domain d where d.delete_flag='normal' and d.is_valid='1' and d.com_id=:comId")
	int countValidByComId(@Param("comId") Long comId);

	@Query("SELECT * FROM wo_domain WHERE second_domain = :secondDomain LIMIT 1")
	WoDomain findBySecondDomain(@Param("secondDomain") String secondDomain);

	/**
	 * 按网站域名精确查重，不加 is_valid/delete_flag 过滤——{@code uni_idx_site_domain} 唯一索引本身不区分
	 * 有效/软删状态，软删行仍占用该域名值，查重必须与唯一索引口径一致，否则会漏判进而在 INSERT 时撞唯一键异常。
	 */
	@Query("SELECT * FROM wo_domain WHERE site_domain = :siteDomain LIMIT 1")
	WoDomain findBySiteDomain(@Param("siteDomain") String siteDomain);

	@Query("select d.* from wo_domain d where d.delete_flag='normal' and d.is_valid='1' and d.com_id > 0 "
			+ "and not exists (select 1 from wo_company c where c.id = d.com_id and c.delete_flag='normal')")
	List<WoDomain> findValidDomainsWithMissingCompany();

	@Query("select d.* from wo_domain d where d.delete_flag='normal' and d.is_valid='1' and d.com_id=0 "
			+ "order by d.display_order asc, d.id asc")
	List<WoDomain> findValidCatalogAxisDomains();
}
