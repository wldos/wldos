/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.controller;

import java.sql.Timestamp;
import java.util.Date;

import com.wldos.support.util.DateUtil;
import com.wldos.support.entity.BaseEntity;
import com.wldos.system.sysenum.DeleteFlagEnum;
import com.wldos.system.sysenum.ValidStatusEnum;

import org.springframework.beans.BeanUtils;

/**
 * 实体公共字段统一设置，这里不采用spring data jdbc的事件监听而是主动调用填充，因为自定义的写方法不确定。
 * 另外建议复杂系统模块使用纯jdbc实现，过度封装的spring data repository对聚合根的jdbc操作存在大量冗余操作，
 * 设置聚合根某个属性会引发全聚合根的更新、删除、再插入，这对于一对多的系统权限配置数据等场景的持久化需求十分不友好，没必要更新历史记录。
 *
 * @Title EntityTools
 * @Package com.wldos.support.util
 * @author 树悉猿
 * @date 2021-04-20
 * @version V1.0
 */
public class EntityTools {
	/**
	 * insert时需要统一设置的公共字段：createBy、createTime、createIp、updateBy、updateTime、updateIp、isValid、deleteFlag
	 *
	 * @param entity 实体bean
	 */
	public static <T> void setInsertInfo(T entity, Long newID, long operateUserId, String userIp, boolean ignoreVersion) {

		Timestamp curTime = DateUtil.convSQLDate(new Date()); // 默认是UTC，否则在分布式架构下，要考虑时区的转换
		BaseEntity baseEntity = new BaseEntity();
		if (newID != null) {
			baseEntity.setId(newID);
		}

		baseEntity.setCreateBy(operateUserId);
		baseEntity.setCreateTime(curTime);
		baseEntity.setCreateIp(userIp);
		baseEntity.setUpdateBy(operateUserId);
		baseEntity.setUpdateIp(userIp);
		baseEntity.setUpdateTime(curTime);
		baseEntity.setIsValid(ValidStatusEnum.valid.toString());
		baseEntity.setDeleteFlag(DeleteFlagEnum.normal.toString());
		baseEntity.setVersions(1);

		BeanUtils.copyProperties(baseEntity, entity, ignoreVersion ? new String[]{"versions"} : null);
	}

	/**
	 * update是需要统一设置的公共字段：updateBy、updateTime、updateIp
	 *
	 * @param entity 实体bean
	 */
	public static <T> void setUpdatedInfo(T entity, long operateUserId, String userIp) {
		Timestamp curTime = DateUtil.convSQLDate(new Date()); // 默认是UTC，否则在分布式架构下，要考虑时区的转换
		BaseEntity baseEntity = new BaseEntity();

		baseEntity.setUpdateBy(operateUserId);
		baseEntity.setUpdateIp(userIp);
		baseEntity.setUpdateTime(curTime);

		BeanUtils.copyProperties(baseEntity, entity, new String[]{"id", "createBy", "createTime", "createIp", "isValid", "deleteFlag", "versions"});
	}
}
