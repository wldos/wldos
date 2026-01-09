/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.support.system.entity.WoOptions;

import org.springframework.data.repository.query.Param;

/**
 * 系统配置项仓库操作类。
 *
 * @author 元悉宇宙
 * @date 2021/7/13
 * @version 1.0
 */
public interface OptionsDao extends BaseDao<WoOptions, Long> {

	List<WoOptions> findAllByOptionTypeIn(@Param("optionType") List<String> optionType);

	List<WoOptions> findAllByOptionType(@Param("optionType") String optionType);

	List<WoOptions> findAllByOptionKey(@Param("optionKey") String optionKey);

	WoOptions findByOptionKey(@Param("optionKey") String optionKey);

	List<WoOptions> findAllByAppCode(@Param("appCode") String appCode);
}
