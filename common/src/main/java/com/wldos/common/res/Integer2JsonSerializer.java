/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.res;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Integer转json例外处理器。
 *
 * @author 树悉猿
 * @date 2021/9/17
 * @version 1.0
 */
public class Integer2JsonSerializer extends JsonSerializer<Integer> {

	@Override
	public void serialize(Integer value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		if (value != null) {
			jsonGenerator.writeNumber(value);
		}
	}

}