/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.base.tools;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.wldos.common.utils.ObjectUtils;
import com.wldos.framework.entity.BaseEntity;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.common.utils.DateUtils;
import lombok.SneakyThrows;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 实体公共字段统一设置，这里不采用spring data jdbc的事件监听而是主动调用填充，因为自定义的写方法不确定。
 * 另外建议复杂系统模块使用纯jdbc实现，过度封装的spring data repository对聚合根的jdbc操作存在大量冗余操作，
 * 设置聚合根某个属性会引发全聚合根的更新、删除、再插入，这对于一对多的系统权限配置数据等场景的持久化需求十分不友好，没必要更新历史记录。
 *
 * @author 元悉宇宙
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
	 * @param operateUserId 操作人id
	 * @param userIp 操作ip
	 * @param isRepo 使用框架jdbc模板方式执行insert,设置为false;使用spring-data-jdbc时设置为true
	 * @param <T> 实体bean
	 */
	@SneakyThrows
	public static <T> void beforeInsert(T entity, long operateUserId, String userIp, boolean isRepo) {

		Timestamp curTime = DateUtils.convSQLDate(new Date()); // 默认是UTC，否则在分布式架构下，要考虑时区的转换
		BaseEntity baseEntity = BaseEntity.of(operateUserId, curTime, userIp, operateUserId,
				curTime, userIp, DeleteFlagEnum.NORMAL.toString(), ValidStatusEnum.VALID.toString(), 1);

		BeanUtils.copyProperties(baseEntity, entity, "id", isRepo ? "versions" : null);
	}

	/**
	 * insert时需要统一设置的公共字段。
	 *
	 * @param entity 实体
	 * @param newID 主键，如果已经显式设置了主键，不会被newID覆盖
	 * @param operateUserId 操作人id
	 * @param userIp 操作ip
	 * @param isRepo 使用框架jdbc模板方式执行insert,设置为false;使用spring-data-jdbc时设置为true
	 * @param <T> 实体bean
	 */
	@SneakyThrows
	public static <T> void beforeInsert(T entity, long newID, long operateUserId, String userIp, boolean isRepo) {

		Timestamp curTime = DateUtils.convSQLDate(new Date()); // 默认是UTC，否则在分布式架构下，要考虑时区的转换
		// 如果主键不为空，则不自动生成主键
		boolean hasId = isNoNullProperty(entity, "id");
		BaseEntity baseEntity = BaseEntity.of(newID, operateUserId, curTime, userIp, operateUserId,
				curTime, userIp, DeleteFlagEnum.NORMAL.toString(), ValidStatusEnum.VALID.toString(), 1);

		BeanUtils.copyProperties(baseEntity, entity, hasId ? "id" : null, isRepo ? "versions" : null);
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
		BaseEntity baseEntity = BaseEntity.of(operateUserId, curTime, userIp); // 配合spring data 等框架支持乐观锁

		BeanUtils.copyProperties(baseEntity, entity, "id", "createBy", "createTime", "createIp", "isValid", "deleteFlag", "versions");
	}

	public static boolean isNoNullProperty(Object instance, String name) {
		BeanWrapper srcBean = new BeanWrapperImpl(instance);
		PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
		for (PropertyDescriptor p : pds) {
			if (!p.getName().equals(name))
				continue;
			Object value = srcBean.getPropertyValue(p.getName());
			return value != null;
		}
		return false;
	}
}