/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wldos.sys.base.entity.WoApp;
import org.apache.ibatis.annotations.Mapper;

import org.springframework.data.repository.query.Param;

/**
 * demo一个mapper。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
@Mapper
public interface AppMapper extends BaseMapper<WoApp> {

	List<WoApp> findAllByDeleteFlagEqualsAndIsValidEquals(@Param("deleteFlag") String deleteFlag, @Param("isValid") String isValid);
}
