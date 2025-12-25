/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.OptionsDao;
import com.wldos.platform.core.dao.TermTypeDao;
import com.wldos.platform.core.entity.KTermType;
import com.wldos.platform.core.vo.TermTypeMeta;
import com.wldos.platform.support.system.entity.WoOptions;
import com.wldos.platform.support.term.enums.TermTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类类型元数据服务
 * 基于现有表结构，不创建新表
 *
 * @author 元悉宇宙
 * @date 2025/12/07
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TermTypeMetaService extends EntityService<TermTypeDao, KTermType, Long> {

	private final OptionsDao optionsRepo;
	private static final String TERM_TYPE_META_PREFIX = "term_type_meta_";
	private static final ObjectMapper objectMapper = new ObjectMapper();

	// 高性能 BeanCopier，用于从枚举到 VO 的复制
	private final BeanCopier enumToVoCopier = BeanCopier.create(TermTypeEnum.class, TermTypeMeta.class, false);

	public TermTypeMetaService(OptionsDao optionsRepo) {
		this.optionsRepo = optionsRepo;
	}

	/**
	 * 查询所有分类类型（包含元数据）
	 * 从数据库查询实际使用的类型，结合枚举中的元数据
	 *
	 * @return 分类类型列表
	 */
	public List<TermTypeMeta> findAllTermTypes() {
		// 从数据库查询所有不同的 class_type
		List<String> dbTypes = this.entityRepo.findAllDistinctClassTypes();
		
		// 获取所有枚举定义的类型
		TermTypeEnum[] enumTypes = TermTypeEnum.values();
		
		// 构建结果列表
		List<TermTypeMeta> result = new ArrayList<>();
		
		// 先添加枚举中定义的类型（系统类型）
		for (TermTypeEnum enumType : enumTypes) {
			TermTypeMeta meta = buildTermTypeMetaFromEnum(enumType, dbTypes.contains(enumType.getCode()));
			result.add(meta);
		}
		
		// 添加数据库中存在但枚举中未定义的类型（自定义类型）
		for (String dbType : dbTypes) {
			TermTypeEnum enumType = TermTypeEnum.getByCode(dbType);
			if (enumType == null) {
				// 自定义类型，从枚举中获取不到，尝试从配置中读取
				TermTypeMeta meta = loadCustomTypeMetaAsVo(dbType);
				if (meta == null) {
					// 如果配置中没有，使用默认值
					meta = buildDefaultTermTypeMeta(dbType);
				}
				result.add(meta);
			}
		}
		
		// 按排序字段排序
		result.sort((a, b) -> {
			Integer orderA = a.getSortOrder() != null ? a.getSortOrder() : 999;
			Integer orderB = b.getSortOrder() != null ? b.getSortOrder() : 999;
			return orderA.compareTo(orderB);
		});
		
		return result;
	}

	/**
	 * 根据编码获取分类类型元数据
	 *
	 * @param code 类型编码
	 * @return 分类类型元数据
	 */
	public TermTypeMeta findByCode(String code) {
		if (ObjectUtils.isBlank(code)) {
			return null;
		}
		
		TermTypeEnum enumType = TermTypeEnum.getByCode(code);
		if (enumType != null) {
			// 系统类型，从枚举获取
			List<String> dbTypes = this.entityRepo.findAllDistinctClassTypes();
			return buildTermTypeMetaFromEnum(enumType, dbTypes.contains(code));
		} else {
			// 自定义类型，检查数据库中是否存在
			List<String> dbTypes = this.entityRepo.findAllDistinctClassTypes();
			if (dbTypes.contains(code)) {
				// 尝试从配置中读取
				TermTypeMeta meta = loadCustomTypeMetaAsVo(code);
				if (meta == null) {
					// 如果配置中没有，使用默认值
					meta = buildDefaultTermTypeMeta(code);
				}
				return meta;
			}
		}
		
		return null;
	}

	/**
	 * 保存分类类型元数据（新增或更新）
	 * 对于自定义类型，元数据存储在 wo_options 表中
	 *
	 * @param meta 分类类型元数据 VO
	 * @return 操作结果
	 */
	public String saveTermTypeMeta(TermTypeMeta meta) {
		if (meta == null || ObjectUtils.isBlank(meta.getCode())) {
			return "编码不能为空";
		}

		String code = meta.getCode();

		// 检查是否是系统类型
		TermTypeEnum enumType = TermTypeEnum.getByCode(code);
		if (enumType != null && enumType.getIsSystem()) {
			return "系统类型不允许修改";
		}

		// 对于自定义类型，将元数据保存到 wo_options 表
		try {
			String optionKey = TERM_TYPE_META_PREFIX + code;
			String optionValue = objectMapper.writeValueAsString(meta);
			
			WoOptions option = this.optionsRepo.findByOptionKey(optionKey);
			if (option == null) {
				// 新增
				option = new WoOptions();
				option.setId(this.nextId());
				option.setOptionKey(optionKey);
				option.setOptionName("分类类型元数据: " + code);
				option.setOptionValue(optionValue);
				option.setOptionType("AUTO_RELOAD");
				option.setAppCode("sys_option");
				this.insertOtherEntitySelective(option, false);
			} else {
				// 更新
				option.setOptionValue(optionValue);
				this.optionsRepo.save(option);
			}
			
			return "ok";
		} catch (JsonProcessingException e) {
			this.getLog().error("保存分类类型元数据失败: {}", e.getMessage(), e);
			return "保存失败: " + e.getMessage();
		}
	}

	/**
	 * 删除分类类型（仅限自定义类型）
	 *
	 * @param code 类型编码
	 * @return 操作结果
	 */
	public String deleteTermTypeMeta(String code) {
		if (ObjectUtils.isBlank(code)) {
			return "编码不能为空";
		}

		// 检查是否是系统类型
		TermTypeEnum enumType = TermTypeEnum.getByCode(code);
		if (enumType != null && "1".equals(enumType.getIsSystem())) {
			return "系统类型不允许删除";
		}

		// 检查是否在数据库中使用
		List<String> dbTypes = this.entityRepo.findAllDistinctClassTypes();
		if (dbTypes.contains(code)) {
			// 如果已经在使用，不允许删除
			return "该分类类型正在使用中，无法删除";
		}

		// 删除相关的元数据配置
		String optionKey = TERM_TYPE_META_PREFIX + code;
		WoOptions option = this.optionsRepo.findByOptionKey(optionKey);
		if (option != null) {
			this.deleteByEntityAndIds(option, false, option.getId());
		}
		
		return "ok";
	}


	/**
	 * 从配置中加载自定义类型的元数据（返回 VO）
	 *
	 * @param code 类型编码
	 * @return 元数据，如果不存在返回null
	 */
	private TermTypeMeta loadCustomTypeMetaAsVo(String code) {
		try {
			String optionKey = TERM_TYPE_META_PREFIX + code;
			WoOptions option = this.optionsRepo.findByOptionKey(optionKey);
			if (option != null && !ObjectUtils.isBlank(option.getOptionValue())) {
				return objectMapper.readValue(option.getOptionValue(), TermTypeMeta.class);
			}
		} catch (JsonProcessingException e) {
			this.getLog().warn("读取分类类型元数据失败: {}", e.getMessage());
		}
		return null;
	}

	/**
	 * 从枚举构建 TermTypeMeta VO
	 * 使用高性能 BeanCopier 进行属性复制
	 *
	 * @param enumType 枚举类型
	 * @param existsInDb 是否在数据库中存在
	 * @return TermTypeMeta VO
	 */
	private TermTypeMeta buildTermTypeMetaFromEnum(TermTypeEnum enumType, Boolean existsInDb) {
		TermTypeMeta meta = new TermTypeMeta();
		// 使用 BeanCopier 高性能复制相同字段
		enumToVoCopier.copy(enumType, meta, null);
		// existsInDb 字段枚举中没有，需要单独设置
		meta.setExistsInDb(existsInDb);
		return meta;
	}

	/**
	 * 构建默认的 TermTypeMeta VO（用于自定义类型）
	 *
	 * @param code 类型编码
	 * @return TermTypeMeta VO
	 */
	private TermTypeMeta buildDefaultTermTypeMeta(String code) {
		TermTypeMeta meta = new TermTypeMeta();
		meta.setCode(code);
		meta.setName(code); // 默认使用编码作为名称
		meta.setDescription("");
		meta.setStructureType("tree"); // 默认树形
		meta.setSupportSort("1");
		meta.setSupportHierarchy("1");
		meta.setIcon("");
		meta.setSortOrder(999); // 自定义类型排在后面
		meta.setIsSystem(false);
		meta.setExistsInDb(true);
		return meta;
	}
}

