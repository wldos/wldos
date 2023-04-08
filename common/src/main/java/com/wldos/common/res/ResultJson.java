/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
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
@SuppressWarnings({ "unused" })
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

	/**
	 * 封装方法返回的数据到统一的数据结构中，以形成统一格式的json。
	 *
	 * @param res 操作返回的数据对象
	 * @return 格式化后的json
	 */
	public Result format(Object res) {
		if (ObjectUtils.isBlank(res)) {
			return new DomainResult().data("");
		}

		return new DomainResult().data(res);
	}

	/**
	 * 返回json字符串
	 *
	 * @param obj 要转换的对象实例
	 * @return 转换的json
	 */
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

	/**
	 * 简单键值对，返回json字符串
	 *
	 * @param key 参数键
	 * @param value 参数值
	 * @return <k,v>json字符串
	 */
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

	/**
	 * 返回json字符串
	 *
	 * @param obj 待处理对象
	 * @param isLong2Str 是否long转字符串，防止js精度丢失
	 * @return 已转换json字符串
	 */
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

	/**
	 * json转对象，接收前端传递的json
	 *
	 * @param json 待读json字符串
	 * @return 已转换对象
	 */
	public <AnyEntity> AnyEntity readEntity(String json, TypeReference<AnyEntity> anyEntity) {
		try {
			return this.objectMapper.readValue(json, anyEntity);
		}
		catch (JsonProcessingException e) {
			log.error("json转对象异常，json=" + json, e);
		}
		return null;
	}

	/**
	 * 返回直译json字符串
	 *
	 * @param obj 待处理对象
	 * @param isLong2String 是否Long转字符串，前端js不支持长度数字需要转，后端交换不需要
	 * @return json字符串
	 */
	public String toJson(Object obj, boolean isLong2String) {
		try {
			if (isLong2String) {
				return this.objectMapper.writeValueAsString(obj);
			}
			return new ObjectMapper().writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			log.error("转换json异常，转换对象obj=" + obj, e);
		}
		return "";
	}

	public ObjectMapper getObjectMapper() {

		return this.objectMapper;
	}

	/**
	 * 复杂对象返回json字符串
	 *
	 * @param obj 待处理对象
	 * @param type 对象类型
	 * @return 已转换json字符串
	 */
	public String ok(Object obj, Type type) {
		Gson g = new Gson();
		String complex = g.toJson(obj, type);
		return this.ok(complex);
	}
}