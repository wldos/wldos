/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.base.service.BaseService;
import com.wldos.sys.base.entity.KModelContent;
import com.wldos.sys.base.repo.ContentRepo;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.enums.RedisKeyEnum;

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

	/**
	 * 所有模型行业门类，属于研发期确定参数，普通使用不能改变，二次开发可以追加或修改
	 *
	 * @return 模型行业门类列表
	 */
	public List<KModelContent> queryAllContent() {
		String key = RedisKeyEnum.WLDOS_CONTENT.toString();
		String value = ObjectUtils.string(this.cache.get(key));
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				List<KModelContent> contents = this.findAll();
				if (ObjectUtils.isBlank(contents)) {
					return new ArrayList<>();
				}

				// 暂不考虑用个性域名等二级域名的分站处理逻辑，二级域名视同顶级域名，仅按顶级域名管理，获取顶级域名的统一方法见DomainUtil
				value = om.writeValueAsString(contents);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return contents;
			}

			return om.readValue(value, new TypeReference<List<KModelContent>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取所有行业门类异常");
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	/** 清除行业门类相关的缓存 */
	public void refreshContent() {
		this.cache.remove(RedisKeyEnum.WLDOS_CONTENT.toString());
	}
}
