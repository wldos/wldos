/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.res;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.gson.Gson;
import com.wldos.common.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * 统一响应json格式。
 *
 * @author 树悉猿
 * @date 2021-04-17
 * @version V1.0
 */
@SuppressWarnings({"unchecked", "unused"})
@Slf4j
@Component
public class ResultJson {


	private final ObjectMapper objectMapper;

	public ResultJson() {
		ObjectMapper objectMapper = new ObjectMapper();
		/*
		 * 序列换成json时,将long转成string
		 * js中得数字范围小于java long取值范围
		 */
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
		objectMapper.registerModule(simpleModule);
		this.objectMapper = objectMapper;
	}

	public Result format(Object res) {
		if (ObjectUtils.isBlank(res)) {
			return new DomainResult().data("");
		}

		return new DomainResult().data(res);
	}

	public String ok(Object obj) {
		Result res = this.format(obj);
		try {
			return this.objectMapper.writeValueAsString(res);
		}
		catch (JsonProcessingException e) {
			log.error("转换json异常，转换对象res={}", res);
		}
		return "";
	}

	public String ok(String key, Object value) {
		Map<String, Object> obj = new HashMap<>();
		obj.put(key, value);
		Result res = this.format(obj);
		try {
			return this.objectMapper.writeValueAsString(res);
		}
		catch (JsonProcessingException e) {
			log.error("转换json异常，转换对象res={}", res);
		}
		return "";
	}

	public String ok(Object obj, boolean isLong2Str) {
		Result res = this.format(obj);
		try {
			if (isLong2Str)
				return this.ok(obj);
			else
				return new ObjectMapper().writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			log.error("转换json异常，转换对象res={}", res);
		}
		return "";
	}

	public <AnyEntity> AnyEntity readEntity(String json, boolean isString2Long) {
		try {
			if (isString2Long) {
				return new ObjectMapper().readValue(json, new TypeReference<AnyEntity>(){});
			}
			return this.objectMapper.readValue(json, new TypeReference<AnyEntity>(){});
		}
		catch (JsonProcessingException e) {
			log.error("json转对象异常，json="+json);
		}
		return null;
	}

	public String toJson(Object obj, boolean isLong2String) {
		try {
			if (isLong2String) {
				return this.objectMapper.writeValueAsString(obj);
			}
			return new ObjectMapper().writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			log.error("转换json异常，转换对象obj="+obj);
		}
		return "";
	}

	public ObjectMapper getObjectMapper() {

		return this.objectMapper;
	}

	public String ok(Object obj, Type type) {
		Gson g = new Gson();
		String complex = g.toJson(obj, type);
		return this.ok(complex);
	}
}
