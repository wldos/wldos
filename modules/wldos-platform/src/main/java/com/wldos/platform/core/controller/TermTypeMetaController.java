/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.KTermType;
import com.wldos.platform.core.service.TermTypeMetaService;
import com.wldos.platform.core.vo.TermTypeMeta;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
	@GetMapping("")
	public String listTermTypeMeta() {
		List<TermTypeMeta> list = this.service.findAllTermTypes();
		return this.resJson.ok(list);
	}

	/**
	 * 根据编码获取分类类型元数据
	 *
	 * @param code 类型编码
	 * @return 分类类型元数据
	 */
	@GetMapping("/code/{code}")
	public String getTermTypeMetaByCode(@PathVariable String code) {
		TermTypeMeta meta = this.service.findByCode(code);
		if (meta == null) {
			return this.resJson.ok("分类类型不存在");
		}
		return this.resJson.ok(meta);
	}

	/**
	 * 保存分类类型元数据（新增或更新）
	 * 支持接收 VO 或 Map（兼容前端）
	 *
	 * @param data 分类类型元数据（VO 或 Map）
	 * @return 操作结果
	 */
	@PostMapping("")
	public String saveTermTypeMeta(@RequestBody Object data) {
		TermTypeMeta meta;
		if (data instanceof TermTypeMeta) {
			meta = (TermTypeMeta) data;
		} else if (data instanceof Map) {
			// 兼容前端传入的 Map，转换为 VO
			try {
				meta = objectMapper.convertValue(data, TermTypeMeta.class);
			} catch (Exception e) {
				this.getLog().error("转换分类类型元数据失败: {}", e.getMessage(), e);
				return this.resJson.ok("数据格式错误: " + e.getMessage());
			}
		} else {
			return this.resJson.ok("不支持的数据类型");
		}

		String result = this.service.saveTermTypeMeta(meta);
		if ("ok".equals(result)) {
			return this.resJson.ok("ok");
		} else {
			return this.resJson.ok(result);
		}
	}

	/**
	 * 删除分类类型（仅限自定义类型）
	 *
	 * @param code 类型编码
	 * @return 操作结果
	 */
	@DeleteMapping("/code/{code}")
	public String deleteTermTypeMeta(@PathVariable String code) {
		String result = this.service.deleteTermTypeMeta(code);
		if ("ok".equals(result)) {
			return this.resJson.ok("ok");
		} else {
			return this.resJson.ok(result);
		}
	}
}

