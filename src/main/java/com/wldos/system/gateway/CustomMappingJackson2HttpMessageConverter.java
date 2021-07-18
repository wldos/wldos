/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.gateway;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 扩展消息转换器。
 *
 * @author 树悉猿
 * @date 2021/5/8
 * @version 1.0
 */
@Slf4j
public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
	public CustomMappingJackson2HttpMessageConverter() {
		List<MediaType> mediaTypes = new ArrayList<>();
		mediaTypes.add(MediaType.TEXT_HTML);  //加入text/html类型的支持
		mediaTypes.add(MediaType.TEXT_PLAIN);
		setSupportedMediaTypes(mediaTypes);
	}
}