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
import com.wldos.platform.audit.dao.SysLogDao;
import com.wldos.platform.audit.entity.WoSysLog;
import com.wldos.platform.audit.vo.SysLogItem;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统日志 Service（写入 + 管理端列表查询，与 LoginLogService/OpLogService 同构）。
 *
 * @author Yuanxi Universe
 * @date 2026/05/04
 * @version 1.0
 */
@Slf4j
@Service
public class SysLogService extends EntityService<SysLogDao, WoSysLog, Long> {

	/**
	 * 实体 → VO 同名同类型字段批量拷贝。
	 *
	 * <p>{@link WoSysLog#getId()} 为 {@code Long} 而 {@link SysLogItem#getId()} 为 {@code String}，
	 * 类型不一致会被 {@link BeanCopier} 静默跳过，由 {@link #toItem(WoSysLog)} 手工补。
	 */
	private static final BeanCopier ENTITY_TO_VO = BeanCopier.create(WoSysLog.class, SysLogItem.class, false);

	@Transactional
	public WoSysLog insertOne(WoSysLog log) {
		if (log == null) return null;
		return this.entityRepo.save(log);
	}

	@Transactional
	public int insertBatch(List<WoSysLog> logs) {
		if (logs == null || logs.isEmpty()) return 0;
		Iterable<WoSysLog> saved = this.entityRepo.saveAll(logs);
		int n = 0;
		if (saved != null) {
			for (WoSysLog ignored : saved) {
				n++;
			}
		}
		return n;
	}

	@Transactional(readOnly = true)
	public PageData<SysLogItem> queryList(PageQuery pageQuery) {
		if (pageQuery == null) {
			return new PageData<>(0, 1, 10, new ArrayList<>(0));
		}
		SysLogItem voProto = new SysLogItem();
		WoSysLog entityProto = new WoSysLog();
		return this.execQueryForPage(voProto, entityProto, pageQuery);
	}

	@Transactional(readOnly = true)
	public SysLogItem detail(Long id) {
		if (id == null) return null;
		WoSysLog e = this.findById(id);
		if (e == null) return null;
		return toItem(e);
	}

	private static SysLogItem toItem(WoSysLog e) {
		SysLogItem v = new SysLogItem();
		// 同名同类型字段：sourceModule / eventType / severity / message / metadata
		// / operatorUserId / domainId / comId / occurAt / createdAt
		ENTITY_TO_VO.copy(e, v, null);
		// id：Long → String 类型不一致，BeanCopier 静默跳过，手工补
		v.setId(e.getId() == null ? null : String.valueOf(e.getId()));
		return v;
	}
}
