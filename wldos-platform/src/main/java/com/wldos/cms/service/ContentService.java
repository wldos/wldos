/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.support.service.BaseService;
import com.wldos.cms.entity.KModelContent;
import com.wldos.cms.repo.ContentRepo;
import com.wldos.support.util.ObjectUtil;
import com.wldos.system.core.entity.WoDomain;
import com.wldos.system.enums.RedisKeyEnum;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 内容模型service。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ContentService extends BaseService<ContentRepo, KModelContent, Long> {

	public KModelContent findByContentCode(String contentType) {
		List<KModelContent> contents = this.queryAllContent();
		for (KModelContent c : contents) {
			if (c.getContentCode().equals(contentType))
				return c;
		}
		return null;
	}

	public KModelContent findByContentId(Long contentId) {
		List<KModelContent> contents = this.queryAllContent();
		for (KModelContent c : contents) {
			if (c.getId().equals(contentId))
				return c;
		}
		return null;
	}

	public List<KModelContent> queryAllContent() {
		String key = RedisKeyEnum.WLDOS_CONTENT.toString();
		String value = ObjectUtil.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				List<KModelContent> contents = this.findAll();

				if (ObjectUtil.isBlank(contents)) {
					return new ArrayList<>();
				}

				value = om.writeValueAsString(contents);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return contents;
			}

			return om.readValue(value, new TypeReference<List<KModelContent>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取所有内容类型异常");
			e.printStackTrace();
		}

		return new ArrayList<>();
	}
}
