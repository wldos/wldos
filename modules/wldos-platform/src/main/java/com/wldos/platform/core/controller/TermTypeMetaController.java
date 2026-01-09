/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.common.res.Result;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.KTermType;
import com.wldos.platform.core.service.TermTypeMetaService;
import com.wldos.platform.core.vo.TermTypeMeta;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;
import java.util.Map;

/**
 * 分类类型元数据管理controller
 * 只有超级管理员可以使用
 * 基于现有 k_term_type 表，不创建新表
 *
 * @author 元悉宇宙
 * @date 2025/12/07
 * @version 1.0
 */
@Api(tags = "分类类型元数据管理")
@Slf4j
@RestController
@RequestMapping("/admin/system/term-type-meta")
public class TermTypeMetaController extends EntityController<TermTypeMetaService, KTermType> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 分类类型元数据列表
	 * 从 k_term_type 表查询所有不同的 class_type，结合枚举中的元数据
	 *
	 * @return 分类类型元数据列表
	 */
	@ApiOperation(value = "分类类型元数据列表", notes = "从 k_term_type 表查询所有不同的 class_type，结合枚举中的元数据")
	@GetMapping("")
	public Result listTermTypeMeta() {
		List<TermTypeMeta> list = this.service.findAllTermTypes();
		return Result.ok(list);
	}

	/**
	 * 根据编码获取分类类型元数据
	 *
	 * @param code 类型编码
	 * @return 分类类型元数据
	 */
	@ApiOperation(value = "根据编码获取分类类型元数据", notes = "根据编码获取分类类型元数据")
	@GetMapping("/code/{code}")
	public Result getTermTypeMetaByCode(@ApiParam(value = "类型编码", required = true) @PathVariable String code) {
		TermTypeMeta meta = this.service.findByCode(code);
		if (meta == null) {
			return Result.error(404, "分类类型不存在");
		}
		return Result.ok(meta);
	}

	/**
	 * 保存分类类型元数据（新增或更新）
	 * 支持接收 VO 或 Map（兼容前端）
	 *
	 * @param data 分类类型元数据（VO 或 Map）
	 * @return 操作结果
	 */
	@ApiOperation(value = "保存分类类型元数据", notes = "保存分类类型元数据（新增或更新）")
	@PostMapping("")
	public Result saveTermTypeMeta(@ApiParam(value = "分类类型元数据", required = true) @RequestBody Object data) {
		TermTypeMeta meta;
		if (data instanceof TermTypeMeta) {
			meta = (TermTypeMeta) data;
		} else if (data instanceof Map) {
			// 兼容前端传入的 Map，转换为 VO
			try {
				meta = objectMapper.convertValue(data, TermTypeMeta.class);
			} catch (Exception e) {
				this.getLog().error("转换分类类型元数据失败: {}", e.getMessage(), e);
				return Result.error(400, "数据格式错误: " + e.getMessage());
			}
		} else {
			return Result.error(400, "不支持的数据类型");
		}

		String result = this.service.saveTermTypeMeta(meta);
		if ("ok".equals(result)) {
			return Result.ok("ok");
		} else {
			return Result.error(500, result);
		}
	}

	/**
	 * 删除分类类型（仅限自定义类型）
	 *
	 * @param code 类型编码
	 * @return 操作结果
	 */
	@ApiOperation(value = "删除分类类型", notes = "删除分类类型（仅限自定义类型）")
	@DeleteMapping("/code/{code}")
	public Result deleteTermTypeMeta(@ApiParam(value = "类型编码", required = true) @PathVariable String code) {
		String result = this.service.deleteTermTypeMeta(code);
		if ("ok".equals(result)) {
			return Result.ok("ok");
		} else {
			return Result.error(500, result);
		}
	}
}

