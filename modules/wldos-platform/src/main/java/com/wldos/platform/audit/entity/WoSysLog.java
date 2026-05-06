/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统事件日志，对应表 {@code wo_sys_log}。
 *
 * <p>设计要点与 {@code WoLoginLog}/{@code WoOpLog} 一致（不继承 BaseEntity，
 * {@link Persistable#isNew()}=true 跳过 select 直接 INSERT）。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Table
@Getter
@Setter
public class WoSysLog implements Persistable<Long> {

	@Id
	@TableId
	private Long id;

	private String sourceModule;

	private String eventType;

	private String severity;

	private String message;

	private String metadata;

	private Long operatorUserId;

	private Long domainId;

	private Long comId;

	private Timestamp occurAt;

	private Timestamp createdAt;

	@Transient
	@Override
	public boolean isNew() {
		return true;
	}
}
