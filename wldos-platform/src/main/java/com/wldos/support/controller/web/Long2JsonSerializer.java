/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.support.controller.web;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * long转json例外处理器。
 *
 * @author 树悉猿
 * @date 2021/9/17
 * @version 1.0
 */
public class Long2JsonSerializer extends JsonSerializer<Long> {

	@Override
	public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		if (value != null) {
			jsonGenerator.writeNumber(value);
		}
	}

}