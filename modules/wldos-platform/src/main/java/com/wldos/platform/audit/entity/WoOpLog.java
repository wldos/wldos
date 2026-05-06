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
 * 业务操作日志，对应表 {@code wo_op_log}。
 *
 * <p>设计要点（与 {@code WoLoginLog} 一致）：
 * <ul>
 *   <li>一次写入不可改，<b>不继承 {@code BaseEntity}</b>，省去 update_xxx/delete_flag/is_valid/versions；</li>
 *   <li>{@link Persistable#isNew()} 恒返回 true，spring-data-jdbc 直接 INSERT 不查 select。</li>
 * </ul>
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Table
@Getter
@Setter
public class WoOpLog implements Persistable<Long> {

	@Id
	@TableId
	private Long id;

	private Long userId;

	/** 虚拟身份 ID；无虚拟身份概念时与 userId 一致。 */
	private Long virtualUserId;

	private String userName;

	private Long domainId;

	private Long comId;

	private String module;

	private String action;

	private String resourceType;

	private String resourceId;

	/**
	 * 资源人性化名称（事件发生时刻的快照）。
	 *
	 * <p>由 {@code OpLogPersistAdapter} 在异步消费侧入库前，通过
	 * {@code IOpLogContextResolver} SPI 批量反查业务表得到；
	 * 落库后即"冻结"，业务命名后续变化不影响历史日志，满足合规级审计要求。
	 *
	 * <p>解析失败 / 资源已被硬删 / 无对应解析器时为 {@code null}，
	 * 查询侧会再调一次 SPI 兜底（仍为 null 则前端展示退化为 {@code resourceId}）。
	 */
	private String resourceName;

	private String requestPath;

	private String httpMethod;

	/** 入参摘要 JSON（脱敏 + 截断）。 */
	private String paramsSummary;

	private String resultCode;

	private String errorMessage;

	private Integer durationMs;

	private String ip;

	private String userAgent;

	private Timestamp occurAt;

	private Timestamp createdAt;

	@Transient
	@Override
	public boolean isNew() {
		return true;
	}
}
