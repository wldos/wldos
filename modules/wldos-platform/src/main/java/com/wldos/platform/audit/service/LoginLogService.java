/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.service;

import java.util.ArrayList;
import java.util.List;

import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.audit.dao.LoginLogDao;
import com.wldos.platform.audit.entity.WoLoginLog;
import com.wldos.platform.audit.vo.LoginLogItem;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录日志 Service。
 *
 * <p>职责：
 * <ul>
 *   <li>写入：单条 / 批量纯 INSERT，由 {@link com.wldos.platform.audit.adapter.LoginLogPersistAdapter}
 *       从 framework 异步队列消费侧调用；</li>
 *   <li>查询：管理端列表分页、详情，复用 {@code EntityService.execQueryForPage} 的通用能力。</li>
 * </ul>
 *
 * <p>选型说明：
 * <ul>
 *   <li>WoLoginLog 实现 {@code Persistable.isNew()=true}，{@code entityRepo.save / saveAll}
 *       会直接 INSERT，不会先 select 比对——日志高频写场景的标准优化；</li>
 *   <li>不调框架 {@code saveOrUpdate}（避免不必要的"自动填充公共字段"反射，因为 WoLoginLog
 *       不继承 BaseEntity，没有 createBy/createTime/versions 等）。</li>
 * </ul>
 *
 * @author Yuanxi Universe
 * @date 2026/05/04
 * @version 1.0
 */
@Slf4j
@Service
public class LoginLogService extends EntityService<LoginLogDao, WoLoginLog, Long> {

	/**
	 * 实体 → VO 同名同类型字段批量拷贝。
	 *
	 * <p>{@link WoLoginLog#getId()} 为 {@code Long} 而 {@link LoginLogItem#getId()} 为 {@code String}，
	 * 类型不一致会被 {@link BeanCopier} 静默跳过，由 {@link #toItem(WoLoginLog)} 手工补。
	 */
	private static final BeanCopier ENTITY_TO_VO = BeanCopier.create(WoLoginLog.class, LoginLogItem.class, false);

	/* ========== 写入（异步消费侧调用） ========== */

	/** 单条 INSERT。 */
	@Transactional
	public WoLoginLog insertOne(WoLoginLog log) {
		if (log == null) {
			return null;
		}
		return this.entityRepo.save(log);
	}

	/**
	 * 批量 INSERT。
	 *
	 * <p>spring-data-jdbc 的 {@code saveAll} 不会自动转 batch，
	 * 但配合 {@code Persistable.isNew()=true} 已避免 N 次 select。
	 * 高吞吐场景可在后续版本通过 {@code JdbcTemplate.batchUpdate} 进一步优化（非 P0 必需）。
	 */
	@Transactional
	public int insertBatch(List<WoLoginLog> logs) {
		if (logs == null || logs.isEmpty()) {
			return 0;
		}
		Iterable<WoLoginLog> saved = this.entityRepo.saveAll(logs);
		int n = 0;
		if (saved != null) {
			for (WoLoginLog ignored : saved) {
				n++;
			}
		}
		return n;
	}

	/* ========== 查询（管理端列表/详情） ========== */

	/** 管理端列表分页查询，按 occur_at 倒序优先（前端无 sorter 时兜底）。 */
	@Transactional(readOnly = true)
	public PageData<LoginLogItem> queryList(PageQuery pageQuery) {
		if (pageQuery == null) {
			return new PageData<>(0, 1, 10, new ArrayList<>(0));
		}
		LoginLogItem voProto = new LoginLogItem();
		WoLoginLog entityProto = new WoLoginLog();
		return this.execQueryForPage(voProto, entityProto, pageQuery);
	}

	/** 按 ID 详情。 */
	@Transactional(readOnly = true)
	public LoginLogItem detail(Long id) {
		if (id == null) {
			return null;
		}
		WoLoginLog e = this.findById(id);
		if (e == null) {
			return null;
		}
		return toItem(e);
	}

	/* ========== utilities ========== */

	private static LoginLogItem toItem(WoLoginLog e) {
		LoginLogItem v = new LoginLogItem();
		// 同名同类型字段：userId / loginAccount / eventType / oauthProvider / result / failReason
		// / ip / userAgent / domainId / comId / occurAt / createdAt
		ENTITY_TO_VO.copy(e, v, null);
		// id：Long → String 类型不一致，BeanCopier 静默跳过，手工补
		v.setId(e.getId() == null ? null : String.valueOf(e.getId()));
		return v;
	}
}
