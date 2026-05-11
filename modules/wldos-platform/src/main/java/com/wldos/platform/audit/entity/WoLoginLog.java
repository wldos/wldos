/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
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
 * 登录/账号安全事件日志，对应表 {@code wo_login_log}。
 *
 * <p>设计要点：
 * <ul>
 *   <li><b>不继承 {@code BaseEntity}</b>：日志为一次写入不可改的事件流，
 *       {@code update_xxx / delete_flag / is_valid / versions} 全部冗余无意义；</li>
 *   <li>{@code occurAt} 由业务侧在 {@code recordAsync} 入参 {@code LoginEvent#occurAt} 处填，
 *       避免异步队列消费时间漂移；</li>
 *   <li>{@code createdAt} 在适配器侧用 {@code DateUtils.convSQLDate(new Date())} 显式回填（UTC），
 *       与 {@code occurAt} 共同用于运维诊断"事件生成"与"入库"延迟；
 *       不依赖 DB 的 {@code CURRENT_TIMESTAMP} —— 一是 spring-data-jdbc 仍会显式 INSERT 该列，
 *       null 会触发 NOT NULL 异常；二是 DB 会话时区可能不是 UTC；</li>
 *   <li>{@code id} 由业务侧雪花 ID 注入（与项目惯例一致），不依赖 DB 自增；</li>
 *   <li>实现 {@link Persistable#isNew()} 恒返回 {@code true}：日志只插不改，
 *       让 spring-data-jdbc 跳过 "select 再决定 insert/update" 的查询，仅纯 INSERT。</li>
 * </ul>
 *
 * @author Yuanxi Universe
 * @date 2026/05/04
 * @version 1.0
 */
@Table
@Getter
@Setter
public class WoLoginLog implements Persistable<Long> {

	@Id
	@TableId
	private Long id;

	/** 已登录用户ID；失败/未通过验证时为 null。 */
	private Long userId;

	/** 用户输入的账号（手机号/邮箱/用户名/openId）。 */
	private String loginAccount;

	/** 事件类型，对应 {@link io.github.wldos.framework.support.audit.LoginEventType}。 */
	private String eventType;

	/** OAuth provider，如 github/wechat/qq；非 OAuth 为 null。 */
	private String oauthProvider;

	/** 结果，对应 {@link io.github.wldos.framework.support.audit.LoginEventResult}。 */
	private String result;

	/** 失败原因短文案/错误码。 */
	private String failReason;

	/** 客户端 IP（已 X-Forwarded-For 解析）。 */
	private String ip;

	/** 浏览器 UA。 */
	private String userAgent;

	/** 当前域 ID（多租户）。 */
	private Long domainId;

	/** 当前公司 ID（多租户）。 */
	private Long comId;

	/** 事件发生时间（业务侧填，避免异步落库时间漂移）。 */
	private Timestamp occurAt;

	/** 入库时间（DB 默认值填）。 */
	private Timestamp createdAt;

	/**
	 * 始终视为新增；spring-data-jdbc 会直接 INSERT，跳过 "select 后判 insert/update"。
	 * 配合 {@code @Transient} 不映射到表字段。
	 */
	@Transient
	@Override
	public boolean isNew() {
		return true;
	}
}
