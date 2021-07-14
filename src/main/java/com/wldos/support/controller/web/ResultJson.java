/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.controller.web;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.gson.Gson;
import com.wldos.support.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 统一响应json格式。
 *
 * @Title ResultJson
 * @Package com.wldos.support.controller.web
 * @author 树悉猿
 * @date 2021-04-17
 * @version V1.0
 */
@Slf4j
@Component
public class ResultJson<DomainEntity> {
	// DomainEntity: 单表或领域实体(共同点是：都有仓库EntityRepo实现，需要和数据库交互)，仅与前端交互的controller无须在乎泛型

	private ObjectMapper objectMapper;

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
		if (ObjectUtil.isBlank(res)) {
			return new DomainResult().data("");
		}

		if (res instanceof Result) {
			return (Result) res;
		}

		if (res instanceof Page) {
			Page<DomainEntity> pages = (Page<DomainEntity>) res;

			Pageable pageable = pages.getPageable();
			long total = pages.getTotalElements();
			int page = pageable.getPageNumber();
			int size = pageable.getPageSize();
			List<DomainEntity> rows = pages.getContent(); //.toList();
			return new PageableResult<>(total, page, size, rows);
		}

		return new DomainResult().data(res);
	}

	/**
	 * spring-data-jdbc分页对象，封装方法返回的数据到统一的数据结构中，以形成统一格式的json。
	 *
	 * @param res 操作返回的数据对象
	 * @param clazz 分页实体的类型
	 * @return 格式化后的json
	 */
	public <clazz> Result formatPage(Object res, Class clazz) {


		Page<clazz> pages = (Page<clazz>) res;

		Pageable pageable = pages.getPageable();
		long total = pageable.getOffset();
		int page = pageable.getPageNumber();
		int size = pageable.getPageSize();
		List rows = pages.toList();
		return new PageableResult<clazz>(total, page, size, rows);
	}

	/**
	 * 返回json字符串
	 *
	 * @param obj
	 * @return
	 */
	public String ok(Object obj) {
		Result res = this.format(obj);
		//Gson g = new Gson();
		//return g.toJson(res);
		try {
			return this.objectMapper.writeValueAsString(res);
		}
		catch (JsonProcessingException e) {
			log.error("转换json异常，转换对象res="+res);
		}
		return "";
	}

	/**
	 * 简单键值对，返回json字符串
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public String ok(String key, Object value) {
		Map<String, Object> obj = new HashMap<>();
		obj.put(key, value);
		Result res = this.format(obj);
		try {
			return this.objectMapper.writeValueAsString(res);
		}
		catch (JsonProcessingException e) {
			log.error("转换json异常，转换对象res="+res);
		}
		return "";
	}

	/**
	 * 返回json字符串
	 *
	 * @param obj
	 * @param isLong2Str 是否long转字符串，防止js精度丢失
	 * @return
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
			log.error("转换json异常，转换对象res="+res);
		}
		return "";
	}

	/**
	 * json转对象，接收前端传递的json
	 *
	 * @param json
	 * @param isString2Long 是否把前端给的字符串转成实体真实类型long
	 * @return
	 */
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

	/**
	 * 返回直译json字符串
	 *
	 * @param obj
	 * @param isLong2String 是否Long转字符串，前端js不支持长度数字需要转，后端交换不需要
	 * @return
	 */
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

	/**
	 * 复杂对象返回json字符串
	 *
	 * @param obj
	 * @param type
	 * @return
	 */
	public String ok(Object obj, Type type) {
		Gson g = new Gson();
		String complex = g.toJson(obj, type);
		return this.ok(complex);
	}

	/**
	 * 返回json字符串
	 *
	 * @param obj
	 * @return
	 */
	public String okPage(Object obj, Class clazz) {
		Result res = this.format(obj);
		Gson g = new Gson();

		return g.toJson(res);
	}
}
