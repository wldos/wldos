/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.controller;

import java.sql.Timestamp;
import java.util.Date;

import com.wldos.support.util.DateUtils;
import com.wldos.support.entity.BaseEntity;
import com.wldos.support.enums.DeleteFlagEnum;
import com.wldos.support.enums.ValidStatusEnum;

import org.springframework.beans.BeanUtils;

/**
 * 实体公共字段统一设置。
 *
 * @author 树悉猿
 * @date 2021-04-20
 * @version V1.0
 */
public class EntityAssists {

	private EntityAssists() {
		throw new IllegalStateException("Utility class");
	}

	public static <T> void beforeInsert(T entity, Long newID, long operateUserId, String userIp, boolean isRepo) {

		Timestamp curTime = DateUtils.convSQLDate(new Date());
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
		baseEntity.setIsValid(ValidStatusEnum.VALID.toString());
		baseEntity.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		baseEntity.setVersions(1);

		BeanUtils.copyProperties(baseEntity, entity, isRepo ? "versions" : null);
	}

	public static <T> void beforeUpdated(T entity, long operateUserId, String userIp) {
		Timestamp curTime = DateUtils.convSQLDate(new Date());
		BaseEntity baseEntity = new BaseEntity();

		baseEntity.setUpdateBy(operateUserId);
		baseEntity.setUpdateIp(userIp);
		baseEntity.setUpdateTime(curTime);

		BeanUtils.copyProperties(baseEntity, entity, "id", "createBy", "createTime", "createIp", "isValid", "deleteFlag", "versions");
	}
}
