/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.cms.vo.Post;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.sys.base.dto.ContentExt;

import org.springframework.util.ReflectionUtils;

/**
 * 信息工具类。
 *
 * @author 树悉猿
 * @date 2022/1/10
 * @version 1.0
 */
class InfoUtil {

	static Post extractPostInfo(String json) throws JsonProcessingException {
		Post post = new Post();

		Class<Post> postClass = Post.class;

		Map<String, Object> params = new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {});

		Map<String, Object> entity = new HashMap<>(); // post实体参数
		Map<String, Object> remain = new HashMap<>(); // 辅助参数

		params.forEach((key, value) -> {
			if (value == null)
				return;
			Field f = ReflectionUtils.findField(postClass, key);
			if (f == null) {
				remain.put(key, value);
				return;
			}
			entity.put(key, value);
		});
		ObjectMapper om = new ObjectMapper();
		if (!entity.isEmpty())
			post = om.readValue(om.writeValueAsString(entity), new TypeReference<Post>() {});

		List<ContentExt> contentExtList = remain.entrySet().parallelStream().map(entry -> {
			if (ObjectUtils.isBlank(entry.getValue()))
				return null;

			ContentExt contentExt = new ContentExt();
			contentExt.setMetaKey(entry.getKey());
			contentExt.setMetaValue(entry.getValue().toString());
			return contentExt;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		post.setContentExt(contentExtList);

		return post;
	}
}
