/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.base.Base;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.sys.base.repo.DomainResourceJdbc;
import com.wldos.sys.base.vo.DomainResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * 域资源jdbc扩展实现类。
 *
 * @author 树悉猿
 * @date 2021/12/23
 * @version 1.0
 */
@SuppressWarnings("unused")
public class DomainResourceJdbcImpl extends Base implements DomainResourceJdbc {

	@Autowired
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	private final String sql = "select s.*, r.id as domain_id, r.module_name, r.term_type_id from wo_domain_resource r "
			+ "join wo_resource s on r.app_id=s.app_id and r.resource_id = s.id where r.domain_id=:domainId "
			+ "and r.is_valid = '1' and r.delete_flag = 'normal' and s.is_valid='1' and s.delete_flag='normal'";

	@Override
	public List<DomainResource> queryDomainDynamicRoutes(Long domainId) {
		// 缓存加到dao层
		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);

		String key = queryDomainDynamicRoutesCacheKey(domainId);
		String value = ObjectUtils.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<DomainResource> resources = this.namedParamJdbcTemplate.query(sql + " and r.module_name != 'static'", params, new BeanPropertyRowMapper<>(DomainResource.class));

				if (ObjectUtils.isBlank(resources))
					return new ArrayList<>();

				value = om.writeValueAsString(resources);
				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return resources;
			}

			return om.readValue(value, new TypeReference<List<DomainResource>>() {});
		}
		catch (JsonProcessingException e) {
			this.getLog().error("json解析异常={} {}", value, e.getMessage());
			return new ArrayList<>();
		}
	}

	@Override
	public List<DomainResource> queryDomainResources(Long domainId) {
		// 完成高级自动装配，可自动完成驼峰和下划线的自动映射
		Map<String, Object> params = new HashMap<>();
		params.put("domainId", domainId);

		return this.namedParamJdbcTemplate.query(sql + " order by s.resource_path, s.parent_id | s.display_order", params, new BeanPropertyRowMapper<>(DomainResource.class));
	}
}