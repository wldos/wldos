/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoSubjectAuthentication;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 主体认证（用户 ↔ 主体类型）仓库操作类。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface SubjectAuthenticationDao extends BaseDao<WoSubjectAuthentication, Long> {

	@Query("SELECT * FROM wo_subject_authentication WHERE (com_id = :comId OR com_id IS NULL OR com_id = 0) AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoSubjectAuthentication> findValidByCom(@Param("comId") Long comId);

	@Query("SELECT * FROM wo_subject_authentication WHERE user_id = :userId AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoSubjectAuthentication> findValidByUserId(@Param("userId") Long userId);
}
