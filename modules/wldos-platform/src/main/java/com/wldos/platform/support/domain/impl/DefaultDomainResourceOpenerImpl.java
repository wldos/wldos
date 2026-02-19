/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.domain.impl;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.support.domain.DomainResourceOpener;
import com.wldos.platform.support.domain.vo.DomainResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认 DomainResourceOpener 实现类（开源版本）
 * 当 Agent 模块中的 DomainResourceOpenerImpl 不存在时使用此实现
 * 使用降级处理：直接从数据库查询，不使用缓存
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2026/01/10
 */
@ConditionalOnMissingClass("com.wldos.support.domain.DomainResourceOpenerImpl")
@Component("domainResourceOpener")
public class DefaultDomainResourceOpenerImpl extends NonEntityService implements DomainResourceOpener {

	private final String sql = "select s.*, r.id as domain_id, r.module_name, r.term_type_id, r.url from wo_domain_resource r "
			+ "join wo_resource s on r.app_id=s.app_id and r.resource_id = s.id where r.domain_id=:domainId "
			+ "and r.is_valid = '1' and r.delete_flag = 'normal' and s.is_valid='1' and s.delete_flag='normal'";

	@Override
	public List<DomainResource> queryDomainDynamicRoutes(Long domainId) {
		// 降级处理：不使用缓存，直接查询数据库
		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);

		List<DomainResource> resources = this.namedParamJdbcTemplate.query(
				sql + " and r.module_name != 'static'", params, new BeanPropertyRowMapper<>(DomainResource.class));
        
		return resources != null ? resources : new ArrayList<>();
	}

	@Override
	public List<DomainResource> queryDomainResources(Long domainId) {
		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);

		return this.namedParamJdbcTemplate.query(
				sql + " order by s.resource_path, coalesce(s.parent_id, 0), s.display_order", params, new BeanPropertyRowMapper<>(DomainResource.class));
	}
}
