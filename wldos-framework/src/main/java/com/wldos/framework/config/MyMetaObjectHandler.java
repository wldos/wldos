/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import java.sql.Timestamp;
import java.util.Date;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.wldos.framework.common.CommonOperation;
import com.wldos.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 公共审计字段自动填充处理。
 *
 * @author 元悉宇宙
 * @date 2023/10/21
 * @version 1.0
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

	public static final String CREATE_BY = "createBy";

	public static final String UPDATE_BY = "updateBy";

	public static final String CREATE_TIME = "createTime";

	public static final String UPDATE_TIME = "updateTime";

	public static final String CREATE_IP = "createIp";

	public static final String UPDATE_IP = "updateIp";

	@Autowired
	@Lazy
	@SuppressWarnings({ "all" })
	protected CommonOperation commonOperate;

	@Override
	public void insertFill(MetaObject metaObject) {
		Long userId = userId();
		if (this.getFieldValByName(CREATE_BY, metaObject) == null) {
			this.setFieldValByName(CREATE_BY, userId, metaObject);
		}
		if (this.getFieldValByName(UPDATE_BY, metaObject) == null) {
			this.setFieldValByName(UPDATE_BY, userId, metaObject);
		}
		Timestamp date = DateUtils.convSQLDate(new Date());
		if (this.getFieldValByName(CREATE_TIME, metaObject) == null) {
			this.setFieldValByName(CREATE_TIME, date, metaObject);
		}
		if (this.getFieldValByName(UPDATE_TIME, metaObject) == null) {
			this.setFieldValByName(UPDATE_TIME, date, metaObject);
		}

		String curUserIp = curUserIp();
		if (this.getFieldValByName(CREATE_IP, metaObject) == null) {
			this.setFieldValByName(CREATE_IP, curUserIp, metaObject);
		}
		if (this.getFieldValByName(UPDATE_IP, metaObject) == null) {
			this.setFieldValByName(UPDATE_IP, curUserIp, metaObject);
		}
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		Long userId = userId();
		if (this.getFieldValByName(UPDATE_BY, metaObject) == null) {
			this.setFieldValByName(UPDATE_BY, userId, metaObject);
		}
		if (this.getFieldValByName(UPDATE_TIME, metaObject) == null) {
			this.setFieldValByName(UPDATE_TIME, DateUtils.convSQLDate(new Date()), metaObject);
		}

		String curUserIp = curUserIp();
		if (this.getFieldValByName(UPDATE_IP, metaObject) == null) {
			this.setFieldValByName(UPDATE_IP, curUserIp, metaObject);
		}
	}

	private Long userId() {
		log.info("自动获取user id：{}", this.commonOperate.getUserId());
		return this.commonOperate.getUserId();
	}

	private String curUserIp() {
		return this.commonOperate.getUserIp();
	}
}
