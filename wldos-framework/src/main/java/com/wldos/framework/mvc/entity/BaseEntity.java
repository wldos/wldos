/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.wldos.common.enums.ValidStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

/**
 * 抽象实体，用于定义公共字段对应的公共属性。
 *
 * @author 元悉宇宙
 * @date 2021-02-11
 * @version V1.0
 */
@Data
@Accessors(chain = true)
public class BaseEntity {
	@Id
	@TableId
	private Long id;

	@TableField(fill = FieldFill.INSERT)
	private Long createBy;

	@TableField(fill = FieldFill.INSERT)
	private Timestamp createTime;

	@TableField(fill = FieldFill.INSERT)
	private String createIp;

	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Long updateBy;

	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Timestamp updateTime;

	@TableField(fill = FieldFill.INSERT_UPDATE)
	private String updateIp;

	@TableLogic
	private String deleteFlag;

	private String isValid = ValidStatusEnum.VALID.toString();

	/** 乐观锁 注意如果不使用spring-data-jdbc或者mybatis-plus实现数据更新时需要自己手动维护，或者使用框架的insertSelective类型的方法设置isAutoFill=true表示自动生成公共字段包括乐观锁 */
	@Version
	@com.baomidou.mybatisplus.annotation.Version
	private Integer versions;

	public BaseEntity() {
	}

	public static BaseEntity of(Long updateBy, Timestamp updateTime, String updateIp) {
		return new BaseEntity(updateBy, updateTime, updateIp);
	}

	public static BaseEntity of(Long createBy, Timestamp createTime, String createIp, Long updateBy, Timestamp updateTime,
			String updateIp, String deleteFlag, String isValid, Integer versions) {
		return new BaseEntity(createBy, createTime, createIp, updateBy, updateTime, updateIp,
				deleteFlag, isValid, versions);
	}

	public static BaseEntity of(Long id, Long createBy, Timestamp createTime, String createIp, Long updateBy, Timestamp updateTime,
			String updateIp, String deleteFlag, String isValid, Integer versions) {
		return new BaseEntity(id, createBy, createTime, createIp, updateBy, updateTime, updateIp,
				deleteFlag, isValid, versions);
	}

	private BaseEntity(Long updateBy, Timestamp updateTime, String updateIp) {
		this.updateBy = updateBy;
		this.updateTime = updateTime;
		this.updateIp = updateIp;
	}

	private BaseEntity(Long createBy, Timestamp createTime, String createIp, Long updateBy, Timestamp updateTime,
			String updateIp, String deleteFlag, String isValid, Integer versions) {
		this.id = null;
		this.createBy = createBy;
		this.createTime = createTime;
		this.createIp = createIp;
		this.updateBy = updateBy;
		this.updateTime = updateTime;
		this.updateIp = updateIp;
		this.deleteFlag = deleteFlag;
		this.isValid = isValid;
		this.versions = versions;
	}

	private BaseEntity(Long id, Long createBy, Timestamp createTime, String createIp, Long updateBy, Timestamp updateTime,
			String updateIp, String deleteFlag, String isValid, Integer versions) {
		this.id = id;
		this.createBy = createBy;
		this.createTime = createTime;
		this.createIp = createIp;
		this.updateBy = updateBy;
		this.updateTime = updateTime;
		this.updateIp = updateIp;
		this.deleteFlag = deleteFlag;
		this.isValid = isValid;
		this.versions = versions;
	}
}
