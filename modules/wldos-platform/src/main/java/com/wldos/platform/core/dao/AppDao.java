/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import com.wldos.framework.mvc.dao.BaseDao;
import org.springframework.data.jdbc.repository.query.Query;
import com.wldos.platform.core.entity.WoApp;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 平台应用仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public interface AppDao extends BaseDao<WoApp, Long> {

	List<WoApp> findAllByDeleteFlagEqualsAndIsValidEquals(@Param("deleteFlag") String deleteFlag, @Param("isValid") String isValid);

    @Query("select * from wo_app where app_code=:appCode and delete_flag='normal' limit 1")
    WoApp findByAppCode(@Param("appCode") String appCode);
}
