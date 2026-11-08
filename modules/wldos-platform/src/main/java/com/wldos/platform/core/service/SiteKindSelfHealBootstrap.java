/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.List;

import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.entity.WoDomain;

import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.RedisKeyEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.framework.support.cache.ICache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 启动时为存量站点补齐缺失的内置会员角色、授权与挂载（默认关闭）。
 *
 * <p>开关：{@code wldos.platform.site-kind.self-heal-enabled=true} 可开启。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 300)
public class SiteKindSelfHealBootstrap implements ApplicationRunner {

	private final DomainDao domainDao;

	private final DomainBootstrap domainBootstrap;

	private final ICache cache;

	@Value("${wldos.platform.site-kind.self-heal-enabled:false}")
	private boolean enabled;

	public SiteKindSelfHealBootstrap(DomainDao domainDao, DomainBootstrap domainBootstrap, ICache cache) {
		this.domainDao = domainDao;
		this.domainBootstrap = domainBootstrap;
		this.cache = cache;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!this.enabled) {
			log.info("[site-kind.self-heal] disabled by config");
			return;
		}
		try {
			List<WoDomain> domains = this.domainDao.findAllByDeleteFlagAndIsValid(
					DeleteFlagEnum.NORMAL.toString(), ValidStatusEnum.VALID.toString());
			if (domains == null || domains.isEmpty()) {
				return;
			}
			int healed = 0;
			for (WoDomain domain : domains) {
				if (domain == null || domain.getId() == null) {
					continue;
				}
				Long comId = domain.getComId();
				this.domainBootstrap.ensureSiteOperationalSeed(comId, domain.getId());
				healed++;
			}
			this.cache.removeByPrefix(RedisKeyEnum.WLDOS_AUTH.toString());
			log.info("[site-kind.self-heal] ensured operational seed for {} domain(s)", healed);
		}
		catch (Exception ex) {
			log.error("[site-kind.self-heal] failed: {}", ex.getMessage(), ex);
		}
	}
}
