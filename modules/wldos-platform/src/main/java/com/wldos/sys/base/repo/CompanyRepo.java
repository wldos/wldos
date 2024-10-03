/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.repo;

import java.sql.Timestamp;
import java.util.Date;

import com.wldos.framework.repo.BaseRepo;
import com.wldos.common.Constants;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.sys.base.dto.Tenant;
import com.wldos.sys.base.entity.WoCompany;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 平台公司仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface CompanyRepo extends BaseRepo<WoCompany, Long> {
	// 平台自身租户实例
	Tenant tenant = new Tenant(Constants.TOP_COM_ID, Constants.TOP_COM_CODE, Constants.TOP_COM_NAME,
			"根平台", -1L, 1L, ValidStatusEnum.VALID.toString(), Constants.SYSTEM_USER_ID, new Timestamp(new Date().getTime()),
			"127.0.0.1", Constants.SYSTEM_USER_ID, new Timestamp(new Date().getTime()),
			"127.0.0.1", DeleteFlagEnum.NORMAL.toString());

	/**
	 * 查询所在主企业信息（租户）
	 *
	 * @param userId 用户id
	 * @return 主企业信息
	 */
	@Query("SELECT c.* from wo_company c LEFT JOIN wo_com_user u on c.id=u.com_id where c.delete_flag='normal' and c.is_valid='1' and u.delete_flag='normal' and u.is_valid='1' and u.is_main='1' and u.user_id=:userId")
	Tenant queryTenantInfoByTAdminId(@Param("userId") Long userId);
}
