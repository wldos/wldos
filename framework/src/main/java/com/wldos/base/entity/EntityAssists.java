/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base.entity;

import java.sql.Timestamp;
import java.util.Date;

import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.common.utils.DateUtils;

import org.springframework.beans.BeanUtils;

/**
 * 实体公共字段统一设置，这里不采用spring data jdbc的事件监听而是主动调用填充，因为自定义的写方法不确定。
 * 另外建议复杂系统模块使用纯jdbc实现，过度封装的spring data repository对聚合根的jdbc操作存在大量冗余操作，
 * 设置聚合根某个属性会引发全聚合根的更新、删除、再插入，这对于一对多的系统权限配置数据等场景的持久化需求十分不友好，没必要更新历史记录。
 *
 * @author 树悉猿
 * @date 2021-04-20
 * @version V1.0
 */
public class EntityAssists {

	private EntityAssists() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * insert时需要统一设置的公共字段。
	 *
	 * @param entity 实体
	 * @param newID 主键
	 * @param operateUserId 操作人id
	 * @param userIp 操作ip
	 * @param isRepo 使用框架jdbc模板方式执行insert,设置为false;使用spring-data-jdbc时设置为true
	 * @param <T> 实体bean
	 */
	public static <T> void beforeInsert(T entity, Long newID, long operateUserId, String userIp, boolean isRepo) {

		Timestamp curTime = DateUtils.convSQLDate(new Date()); // 默认是UTC，否则在分布式架构下，要考虑时区的转换
		BaseEntity baseEntity = new BaseEntity(newID, operateUserId, curTime, userIp, operateUserId,
				curTime, userIp, DeleteFlagEnum.NORMAL.toString(), ValidStatusEnum.VALID.toString(), 1);


		BeanUtils.copyProperties(baseEntity, entity, isRepo ? "versions" : null);
	}

	/**
	 * update是需要统一设置的公共字段。
	 *
	 * @param entity 实体
	 * @param operateUserId 操作人id
	 * @param userIp 操作人ip
	 * @param <T> 实体bean
	 */
	public static <T> void beforeUpdated(T entity, long operateUserId, String userIp) {
		Timestamp curTime = DateUtils.convSQLDate(new Date()); // 默认是UTC，否则在分布式架构下，要考虑时区的转换
		BaseEntity baseEntity = new BaseEntity(operateUserId, curTime, userIp);

		BeanUtils.copyProperties(baseEntity, entity, "id", "createBy", "createTime", "createIp", "isValid", "deleteFlag", "versions");
	}
}