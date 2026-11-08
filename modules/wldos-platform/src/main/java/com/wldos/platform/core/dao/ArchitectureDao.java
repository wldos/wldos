/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoArchitecture;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 体系结构仓库操作类。
 *
 * @author Yuanxi Universe
 * @date 2021/4/27
 * @version 1.0
 */
public interface ArchitectureDao extends BaseDao<WoArchitecture, Long> {

	@Query("SELECT * FROM wo_architecture WHERE com_id = :comId AND delete_flag = 'normal' AND is_valid = '1' ORDER BY display_order ASC, id ASC LIMIT 1")
	WoArchitecture findFirstByComIdAndDeleteFlagAndIsValid(@Param("comId") Long comId);
}
