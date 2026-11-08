/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import io.github.wldos.common.utils.DateUtils;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.support.cache.ICache;
import io.github.wldos.platform.support.rbac.EffectiveScopeCache;
import io.github.wldos.platform.support.rbac.RegionRedirectRegistry;

/**
 * 启动加载 {@code wo_region_redirect} 至 SDK 注册表。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class RegionRedirectLoader {

	private final JdbcOperations jdbcOperations;

	@Autowired
	@Lazy
	private ICache cache;

	public RegionRedirectLoader(JdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}

	@PostConstruct
	public void load() {
		this.refresh();
	}

	public void refresh() {
		try {
			List<Map<String, Object>> rows = this.jdbcOperations.queryForList(
					"select from_code, to_code from wo_region_redirect "
							+ "where delete_flag='normal' and is_valid='1' "
							+ "and (effective_at is null or effective_at <= ?)",
					DateUtils.convSQLDate(new Date()));
			Map<String, String> map = new HashMap<>();
			if (rows != null) {
				for (Map<String, Object> row : rows) {
					String from = ObjectUtils.string(row.get("from_code"));
					String to = ObjectUtils.string(row.get("to_code"));
					if (!from.isEmpty() && !to.isEmpty()) {
						map.put(from, to);
					}
				}
			}
			RegionRedirectRegistry.reload(map);
			EffectiveScopeCache.invalidateAll(this.cache);
		}
		catch (Exception ignored) {
			RegionRedirectRegistry.reload(java.util.Collections.emptyMap());
			EffectiveScopeCache.invalidateAll(this.cache);
		}
	}
}
