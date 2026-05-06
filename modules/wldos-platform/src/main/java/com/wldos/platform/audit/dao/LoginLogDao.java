/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.dao;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.audit.entity.WoLoginLog;

/**
 * 登录日志 Repository。
 *
 * <p>查询统一走 {@code EntityService.execQueryForPage}（基于 entity 字段反射，
 * 已内置租户/域过滤、关键字、排序、分页等通用能力），无自定义 {@code @Query}。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
public interface LoginLogDao extends BaseDao<WoLoginLog, Long> {
}
