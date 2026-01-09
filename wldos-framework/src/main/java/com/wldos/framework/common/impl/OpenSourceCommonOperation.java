/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.common.impl;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.wldos.framework.common.CommonOperation;
import com.wldos.framework.common.EntityAssists;
import com.wldos.framework.common.SaveOptions;
import com.wldos.common.Constants;
import com.wldos.common.dto.SQLTable;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.NameConvert;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.TreeUtils;
import com.wldos.common.utils.UUIDUtils;
import com.wldos.common.utils.http.IpUtils;
import com.wldos.common.vo.TreeNode;
import com.wldos.framework.support.internal.God;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 开源版 CommonOperation 实现类
 * 提供基础的 JDBC 操作功能，不依赖加密 JAR
 * 
 * 注意：
 * 1. 此实现类为开源版本，仅实现 JDBC API 操作（查询、分页等）
 * 2. 其他与框架模块无关的业务 API（如 saveOrUpdate、dynamicInsertByEntity 等）为空实现
 * 3. 当 platform 模块存在时，此实现会自动失效，由平台版实现替代
 * 4. 业务相关的 API（如 isAdmin、listSuperAdmin 等）返回默认值，不执行实际业务逻辑
 * 
 * @author 元悉宇宙
 * @date 2026-01-08
 * @version 2.0
 */
public class OpenSourceCommonOperation extends OpenSourceFreeJdbcTemplate implements CommonOperation {
    
    private static final Logger log = LoggerFactory.getLogger(OpenSourceCommonOperation.class);
    
    public OpenSourceCommonOperation(God freeJdbcTemplate) {
        super(freeJdbcTemplate);
    }

    @Override
    public <V> PageableResult<V> execQueryForPage(Class<V> vo, String sqlNoWhere, PageQuery pageQuery, SQLTable... sqlTables) {
        Sort sort = pageQuery.getSorter();
        Map<String, List<Object>> filter = pageQuery.getFilter();
        Map<String, Object> condition = pageQuery.getCondition();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = this.querySqlByTable(sqlNoWhere, sqlTables, params, condition, filter);
        
        String countSql = buildCountSql(sql.toString());
        List<Map<String, Object>> all = this.getJdbcOperations().queryForList(countSql, params.toArray());
        int total = Integer.parseInt(ObjectUtils.string(all.get(0).get("total")));
        
        this.orderSql(sort, sql);
        
        int currentPage = pageQuery.getCurrent();
        int pageSize = pageQuery.getPageSize();
        int totalPageNum = (total - 1) / pageSize + 1;
        currentPage = Math.min(currentPage, totalPageNum);
        List<V> list = this.execQueryForPage(vo, sql.toString(), currentPage, pageSize, params.toArray());
        
        return new PageableResult<>(total, currentPage, pageSize, list);
    }

    @Override
    public <V> PageableResult<V> execQueryForPage(Class<V> vo, String sqlNoWhere, List<Object> params, PageQuery pageQuery, SQLTable... sqlTables) {
        Sort sort = pageQuery.getSorter();
        Map<String, List<Object>> filter = pageQuery.getFilter();
        Map<String, Object> condition = pageQuery.getCondition();
        StringBuilder sql = this.querySqlByTable(sqlNoWhere, sqlTables, params, condition, filter);
        
        String countSql = buildCountSql(sql.toString());
        List<Map<String, Object>> all = this.getJdbcOperations().queryForList(countSql, params.toArray());
        int total = Integer.parseInt(ObjectUtils.string(all.get(0).get("total")));
        
        this.orderSql(sort, sql);
        
        int currentPage = pageQuery.getCurrent();
        int pageSize = pageQuery.getPageSize();
        int totalPageNum = (total - 1) / pageSize + 1;
        currentPage = Math.min(currentPage, totalPageNum);
        List<V> list = this.execQueryForPage(vo, sql.toString(), currentPage, pageSize, params.toArray());
        
        return new PageableResult<>(total, currentPage, pageSize, list);
    }

    @Override
    public <V> List<V> execQueryForList(Class<V> vo, String sqlNoWhere, PageQuery pageQuery, SQLTable... sqlTables) {
        Sort sort = pageQuery.getSorter();
        Map<String, List<Object>> filter = pageQuery.getFilter();
        Map<String, Object> condition = pageQuery.getCondition();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = this.querySqlByTable(sqlNoWhere, sqlTables, params, condition, filter);
        
        this.orderSql(sort, sql);
        
        return this.getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(vo), params.toArray());
    }

    @Override
    public <E> PageableResult<E> execQueryForPage(Class<E> clazz, PageQuery pageQuery) {
        return this.execQueryForPage(clazz, pageQuery, clazz, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends TreeNode<V>, E> PageableResult<V> execQueryForTree(Class<?> clazz, PageQuery pageQuery, Class<E> entity, long root) {
        PageableResult<V> voPageableResult = this.execQueryForPage((Class<V>) clazz, pageQuery, entity, false);
        List<V> entityVOList = TreeUtils.build(voPageableResult.getData().getRows(), root);
        return voPageableResult.setDataRows(entityVOList);
    }

    @Override
    public <E> List<E> findAllWithCond(Class<E> entity, Map<String, Object> condition) {
        String tableName = this.getTableNameByEntity(entity);
        StringBuilder sql = new StringBuilder(50).append("select a.* from ").append(tableName).append(" a ");
        List<Object> params = new ArrayList<>();
        
        if (!ObjectUtils.isBlank(condition)) {
            StringBuilder finalSql1 = new StringBuilder("where 1=1 ");
            String and = "and ";
            String aliases = "a.";
            condition.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(entity, key);
                if (field == null) return;
                if (field.getType().equals(String.class)) {
                    finalSql1.append(and).append(" instr(").append(aliases).append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
                } else if (field.getType().equals(Timestamp.class)) {
                    finalSql1.append(and).append(" DATE_FORMAT(").append(aliases).append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
                } else {
                    finalSql1.append(and).append(aliases).append(NameConvert.humpToUnderLine(key)).append("= ? ");
                }
                params.add(value);
            });
            sql.append(finalSql1);
        }
        
        return this.getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(entity), params.toArray());
    }

    @Override
    public <V, E> PageableResult<V> execQueryForPage(Class<V> clazz, PageQuery pageQuery, Class<E> entity, boolean isPage) {
        List<V> list;
        Sort sort = pageQuery.getSorter();
        Map<String, List<Object>> filter = pageQuery.getFilter();
        Map<String, Object> condition = pageQuery.getCondition();
        List<Object> params = new ArrayList<>();
        String tableName = this.getTableNameByEntity(entity);
        StringBuilder sql = this.querySqlByTable(tableName, entity, params, condition, filter);
        
        String countSql = buildCountSql(sql.toString());
        List<Map<String, Object>> all = this.getJdbcOperations().queryForList(countSql, params.toArray());
        int total = Integer.parseInt(ObjectUtils.string(all.get(0).get("total")));
        
        this.orderSql(sort, sql);
        
        int currentPage = pageQuery.getCurrent();
        int pageSize = pageQuery.getPageSize();
        if (isPage) {
            int totalPageNum = (total - 1) / pageSize + 1;
            currentPage = Math.min(currentPage, totalPageNum);
            list = this.execQueryForPage(clazz, sql.toString(), currentPage, pageSize, params.toArray());
        } else {
            list = this.getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(clazz), params.toArray());
        }
        
        return new PageableResult<>(total, currentPage, pageSize, list);
    }

    // ==================== 开源版 JDBC 操作方法 ====================
    
    /**
     * 根据父子关系表查询VO列表，支持父子表的查询条件、排序、过滤。
     * 开源版实现：基于 querySqlByTable 和 existsSql 构建父子表关联查询
     */
    @Override
    public <P, C, V> List<V> execQueryForList(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, String... pTableAndCTableAndPIdKey) {
        if (pTableAndCTableAndPIdKey == null || pTableAndCTableAndPIdKey.length < 3) {
            log.warn("父子表查询参数不足，需要 [pTable, cTable, pIdKey]");
            return new ArrayList<>();
        }
        
        Sort sort = pageQuery.getSorter();
        Map<String, List<Object>> filter = pageQuery.getFilter();
        Map<String, Object> condition = pageQuery.getCondition();
        List<Object> params = new ArrayList<>();
        
        // 拼装查询sql：父表查询 + 子表EXISTS条件
        String pTable = pTableAndCTableAndPIdKey[0];
        String cTable = pTableAndCTableAndPIdKey[1];
        String pIdKey = pTableAndCTableAndPIdKey[2];
        
        StringBuilder sql = this.querySqlByTable(pTable, pClass, params, condition, filter);
        String baseExistsSql = this.makeBaseExistsSql(cTable, "b", "a", pIdKey);
        sql.append(this.existsSql("b", cClass, baseExistsSql, params, condition, filter));
        
        // 排序
        this.orderSql(sort, sql);
        
        return this.getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(vo), params.toArray());
    }

    /**
     * 根据父子关系表查询父表的分页，支持父子表的查询条件、排序、过滤。
     * 开源版实现：基于父子表查询 + 分页逻辑
     */
    @Override
    public <P, C, V> PageableResult<V> execQueryForPage(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String... pTableAndCTableAndPIdKey) {
        if (pTableAndCTableAndPIdKey == null || pTableAndCTableAndPIdKey.length < 3) {
            log.warn("父子表查询参数不足，需要 [pTable, cTable, pIdKey]");
            return new PageableResult<>(0, 1, pageQuery.getPageSize(), new ArrayList<>());
        }
        
        List<V> list;
        Sort sort = pageQuery.getSorter();
        Map<String, List<Object>> filter = pageQuery.getFilter();
        Map<String, Object> condition = pageQuery.getCondition();
        List<Object> params = new ArrayList<>();
        
        // 拼装查询sql：父表查询 + 子表EXISTS条件
        String pTable = pTableAndCTableAndPIdKey[0];
        String cTable = pTableAndCTableAndPIdKey[1];
        String pIdKey = pTableAndCTableAndPIdKey[2];
        
        StringBuilder sql = this.querySqlByTable(pTable, pClass, params, condition, filter);
        String baseExistsSql = this.makeBaseExistsSql(cTable, "b", "a", pIdKey);
        sql.append(this.existsSql("b", cClass, baseExistsSql, params, condition, filter));
        
        // 计算总数
        String countSql = buildCountSql(sql.toString());
        List<Map<String, Object>> all = this.getJdbcOperations().queryForList(countSql, params.toArray());
        int total = Integer.parseInt(ObjectUtils.string(all.get(0).get("total")));
        
        // 排序
        this.orderSql(sort, sql);
        
        int currentPage = pageQuery.getCurrent();
        int pageSize = pageQuery.getPageSize();
        if (isPage) {
            int totalPageNum = (total - 1) / pageSize + 1;
            currentPage = Math.min(currentPage, totalPageNum);
            list = this.execQueryForPage(vo, sql.toString(), currentPage, pageSize, params.toArray());
        } else {
            list = this.getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(vo), params.toArray());
        }
        
        return new PageableResult<>(total, currentPage, pageSize, list);
    }

    /**
     * 根据父子关系表查询父表的分页，支持父子表的查询条件、排序、过滤。
     * 开源版实现：调用上面的方法，VO类型等于父表实体类型
     */
    @Override
    public <PA, C> PageableResult<PA> execQueryForPage(Class<PA> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String pTable, String cTable, String pIdKey) {
        return this.execQueryForPage(pClass, pClass, cClass, pageQuery, isPage, pTable, cTable, pIdKey);
    }

    /**
     * 根据父子关系表查询父表的分页，支持父子表的查询条件、排序、过滤。
     * 开源版实现：自动获取表名，然后调用上面的方法
     */
    @Override
    public <P, C> PageableResult<P> execQueryForPage(Class<P> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String pIdKey) {
        String pTable = this.getTableNameByEntity(pClass);
        String cTable = this.getTableNameByEntity(cClass);
        return this.execQueryForPage(pClass, cClass, pageQuery, isPage, pTable, cTable, pIdKey);
    }

    // ==================== 开源版 CRUD 操作方法 ====================
    
    private static final String UPDATE = "update ";
    private static final String WHERE = " where ";
    
    /**
     * 获取实体的 @Version 字段值
     */
    private <E> Integer getVersionValue(E entity) {
        try {
            Class<?> clazz = this.clazz(entity);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(Version.class) != null) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value instanceof Integer) {
                        return (Integer) value;
                    } else if (value instanceof Long) {
                        return ((Long) value).intValue();
                    }
                    return null;
                }
            }
            // 检查父类（BaseEntity 有 versions 字段）
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                Field[] superFields = superClass.getDeclaredFields();
                for (Field field : superFields) {
                    if (field.getAnnotation(Version.class) != null) {
                        field.setAccessible(true);
                        Object value = field.get(entity);
                        if (value instanceof Integer) {
                            return (Integer) value;
                        } else if (value instanceof Long) {
                            return ((Long) value).intValue();
                        }
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("获取 @Version 字段值失败: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 设置实体的 @Version 字段值
     */
    private <E> void setVersionValue(E entity, Integer value) {
        try {
            Class<?> clazz = this.clazz(entity);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(Version.class) != null) {
                    field.setAccessible(true);
                    field.set(entity, value);
                    return;
                }
            }
            // 检查父类
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                Field[] superFields = superClass.getDeclaredFields();
                for (Field field : superFields) {
                    if (field.getAnnotation(Version.class) != null) {
                        field.setAccessible(true);
                        field.set(entity, value);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("设置 @Version 字段值失败: {}", e.getMessage());
        }
    }
    
    /**
     * 获取实体的主键字段名
     */
    private <E> String getIdFieldName(Class<?> clazz) {
        Field idField = getIdField(clazz);
        return idField != null ? idField.getName() : "id";
    }
    
    /**
     * 获取实体的主键字段
     */
    private <E> Field getIdField(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Id.class) != null) {
                return field;
            }
        }
        // 检查父类
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            Field[] superFields = superClass.getDeclaredFields();
            for (Field field : superFields) {
                if (field.getAnnotation(Id.class) != null) {
                    return field;
                }
            }
        }
        return null;
    }
    
    /**
     * 获取非空属性名数组（用于 BeanUtils.copyProperties）
     */
    private String[] getNoNullProperties(Object instance) {
        org.springframework.beans.BeanWrapper srcBean = new org.springframework.beans.BeanWrapperImpl(instance);
        java.beans.PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        java.util.Set<String> noEmptyName = new java.util.HashSet<>();
        for (java.beans.PropertyDescriptor p : pds) {
            Object value = srcBean.getPropertyValue(p.getName());
            if (value != null) {
                noEmptyName.add(p.getName());
            }
        }
        String[] result = new String[noEmptyName.size()];
        return noEmptyName.toArray(result);
    }
    
    /**
     * 统一的保存或更新方法，自动判断 insert/update，支持乐观锁、写入空值、合并 null 值等特性。
     * 开源版实现：基于 dynamicInsertByEntity 和 dynamicUpdateByEntity
     */
    @Override
    public <E> E saveOrUpdate(E entity, boolean isAutoFill, boolean includeNullValues, boolean mergeNullValues) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        Class<?> clazz = this.clazz(entity);
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot determine entity class for: " + entity.getClass().getName());
        }
        Map<String, Object> params = ObjectUtils.toMap(entity);
        String idFieldName = getIdFieldName(clazz);
        Object idValue = params.get(idFieldName);
        
        // 尝试使用 @Version 字段判断 insert/update（性能优化）
        Integer version = getVersionValue(entity);
        boolean isUpdate = false;
        
        if (version != null && version > 0) {
            // 有版本号且 > 0，说明是更新操作（无需查询）
            isUpdate = true;
            if (isAutoFill) {
                EntityAssists.beforeUpdated(entity, this.getUserId(), this.getUserIp());
            }
        } else if (idValue != null) {
            // 没有版本号，但有 id，需要查询判断
            String tableName = this.getTableNameByEntity(clazz);
            String idColName = this.getIdColNameByEntity(clazz);
            String checkSql = "SELECT COUNT(1) FROM " + tableName + " WHERE " + idColName + " = ?";
            Integer count = this.getJdbcOperations().queryForObject(checkSql, Integer.class, idValue);
            
            if (count != null && count > 0) {
                isUpdate = true;
                // 如果需要合并 null 值，从数据库读取旧值
                if (mergeNullValues) {
                    String selectSql = "SELECT * FROM " + tableName + " WHERE " + idColName + " = ?";
                    Object oldEntityObj = this.queryForObject(clazz, selectSql, new Object[]{idValue});
                    if (oldEntityObj != null && clazz.isInstance(oldEntityObj)) {
                        @SuppressWarnings("unchecked")
                        E oldEntity = (E) oldEntityObj;
                        // 把数据库中旧记录覆盖新记录中为null的那些域
                        String[] noNullProperties = getNoNullProperties(entity);
                        org.springframework.beans.BeanUtils.copyProperties(oldEntity, entity, noNullProperties);
                    }
                }
                if (isAutoFill) {
                    EntityAssists.beforeUpdated(entity, this.getUserId(), this.getUserIp());
                }
            } else {
                // 新实体，插入
                if (isAutoFill) {
                    EntityAssists.beforeInsert(entity, com.wldos.framework.common.IDGen.nextId(), this.getUserId(), this.getUserIp(), false);
                }
                // 设置 @Version 字段为 1
                if (version == null || version == 0) {
                    setVersionValue(entity, 1);
                }
            }
        } else {
            // 新实体，直接 insert
            if (isAutoFill) {
                EntityAssists.beforeInsert(entity, com.wldos.framework.common.IDGen.nextId(), this.getUserId(), this.getUserIp(), false);
            }
            // 设置 @Version 字段为 1
            setVersionValue(entity, 1);
        }
        
        // 执行 insert 或 update
        try {
            if (isUpdate) {
                // 更新操作：使用 dynamicUpdateByEntity 的逻辑，但支持写入空值
                dynamicUpdateByEntityInternal(entity, includeNullValues);
                // 更新 @Version 字段（乐观锁）
                Integer currentVersion = getVersionValue(entity);
                if (currentVersion != null) {
                    setVersionValue(entity, currentVersion + 1);
                }
                return entity;
            } else {
                // 插入操作：使用 dynamicInsertByEntity 的逻辑，但支持写入空值
                dynamicInsertByEntityInternal(entity, includeNullValues);
                return entity;
            }
        } catch (Exception e) {
            log.error("saveOrUpdate failed for entity: {}", clazz.getName(), e);
            throw new RuntimeException("saveOrUpdate failed for entity: " + clazz.getName(), e);
        }
    }

    @Override
    public <E> E saveOrUpdate(E entity) {
        return saveOrUpdate(entity, true, false, false);
    }

    @Override
    public <E> E saveOrUpdate(E entity, SaveOptions options) {
        if (options == null) {
            options = SaveOptions.defaults();
        }
        return saveOrUpdate(entity, options.isAutoFill(), options.isIncludeNullValues(), options.isMergeNullValues());
    }

    @Override
    public <E> Iterable<E> saveOrUpdateAll(Iterable<E> entities, SaveOptions options) {
        if (options == null) {
            options = SaveOptions.defaults();
        }
        List<E> entityList = new ArrayList<>();
        entities.forEach(entityList::add);
        if (entityList.isEmpty()) {
            return entityList;
        }
        for (E entity : entityList) {
            saveOrUpdate(entity, options);
        }
        return entityList;
    }

    @Override
    public <E> Iterable<E> saveOrUpdateAll(Iterable<E> entities) {
        return saveOrUpdateAll(entities, SaveOptions.defaults());
    }

    /**
     * 根据实体bean运行时动态拼装更新sql并更新
     * 开源版实现：基于实体字段动态生成 UPDATE SQL
     */
    @Override
    public <E> void dynamicUpdateByEntity(E entity, boolean isAutoFill) {
        if (isAutoFill) {
            EntityAssists.beforeUpdated(entity, this.getUserId(), this.getUserIp());
        }
        dynamicUpdateByEntityInternal(entity, false);
    }
    
    /**
     * 内部方法：执行更新操作（支持写入空值）
     */
    private <E> void dynamicUpdateByEntityInternal(E entity, boolean includeNullValues) {
        Map<String, Object> params = ObjectUtils.toMap(entity);
        Class<?> clazz = this.clazz(entity);
        
        // 获取表名
        Table entityRelTable = clazz.getAnnotation(Table.class);
        String tableName = null;
        if (entityRelTable != null) {
            tableName = entityRelTable.value();
        }
        if (ObjectUtils.isBlank(tableName)) {
            tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
        }
        
        StringBuilder sql = new StringBuilder(UPDATE).append(tableName).append(" set ");
        
        String pKeyName = "";
        Object pKeyValue = null;
        List<Object> newParam = new ArrayList<>();
        
        // 根据 includeNullValues 决定是否过滤 null 值
        List<Field> fieldList;
        if (includeNullValues) {
            fieldList = params.entrySet().stream().map(item ->
                    ReflectionUtils.findField(clazz, item.getKey())).filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            fieldList = params.entrySet().stream().filter(p -> p.getValue() != null).map(item ->
                    ReflectionUtils.findField(clazz, item.getKey())).filter(Objects::nonNull).collect(Collectors.toList());
        }
        
        for (Field f : fieldList) {
            String key = f.getName();
            Object value = params.get(key);
            
            // 跳过 @Version 字段（在 WHERE 子句中处理）
            if (f.getAnnotation(Version.class) != null) {
                continue;
            }
            
            // 获取可能的@Column注解
            Column propRelColumn = f.getAnnotation(Column.class);
            
            if (f.getAnnotation(Id.class) == null) {
                String columnName = propRelColumn == null ? NameConvert.humpToUnderLine(key) : propRelColumn.value();
                sql.append(columnName).append("=?,");
                newParam.add(value);
                continue;
            }
            
            // 处理主键
            pKeyName = propRelColumn != null ? propRelColumn.value() : NameConvert.humpToUnderLine(key);
            pKeyValue = value;
        }
        
        if (newParam.isEmpty() && pKeyValue == null) {
            log.warn("没有可更新的字段，entity: {}", clazz.getName());
            return;
        }
        
        sql.delete(sql.length() - 1, sql.length());
        sql.append(WHERE).append(pKeyName).append(" = ? ");
        newParam.add(pKeyValue);
        
        // 处理乐观锁：在 WHERE 子句中添加 @Version 条件
        Integer version = getVersionValue(entity);
        if (version != null && version > 0) {
            String versionFieldName = null;
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(Version.class) != null) {
                    Column versionColumn = field.getAnnotation(Column.class);
                    versionFieldName = versionColumn != null ? versionColumn.value() : NameConvert.humpToUnderLine(field.getName());
                    break;
                }
            }
            if (versionFieldName == null) {
                // 检查父类
                Class<?> superClass = clazz.getSuperclass();
                if (superClass != null && superClass != Object.class) {
                    Field[] superFields = superClass.getDeclaredFields();
                    for (Field field : superFields) {
                        if (field.getAnnotation(Version.class) != null) {
                            Column versionColumn = field.getAnnotation(Column.class);
                            versionFieldName = versionColumn != null ? versionColumn.value() : NameConvert.humpToUnderLine(field.getName());
                            break;
                        }
                    }
                }
            }
            if (versionFieldName != null) {
                sql.append(" AND ").append(versionFieldName).append(" = ?");
                newParam.add(version);
            }
        }
        
        this.getJdbcOperations().update(sql.toString(), newParam.toArray());
    }

    /**
     * 有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。
     * 开源版实现：基于实体字段动态生成 INSERT SQL
     */
    @Override
    public <E> Long dynamicInsertByEntity(E entity, boolean isAutoFill) {
        if (isAutoFill) {
            EntityAssists.beforeInsert(entity, com.wldos.framework.common.IDGen.nextId(), this.getUserId(), this.getUserIp(), false);
        }
        return dynamicInsertByEntityInternal(entity, false);
    }
    
    /**
     * 内部方法：执行插入操作（支持写入空值）
     */
    private <E> Long dynamicInsertByEntityInternal(E entity, boolean includeNullValues) {
        Map<String, Object> params = ObjectUtils.toMap(entity);
        Class<?> clazz = this.clazz(entity);
        
        // 获取表名
        Table entityRelTable = clazz.getAnnotation(Table.class);
        String tableName = null;
        if (entityRelTable != null) {
            tableName = entityRelTable.value();
        }
        if (ObjectUtils.isBlank(tableName)) {
            tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
        }
        
        StringBuilder sql = new StringBuilder("insert into ").append(tableName).append("(");
        StringBuilder wildCard = new StringBuilder();
        List<Object> newParam = new ArrayList<>();
        Object id = null;
        
        // 根据 includeNullValues 决定是否过滤 null 值
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (!includeNullValues && value == null) {
                continue;
            }
            
            Field f = ReflectionUtils.findField(clazz, key);
            if (f == null) {
                continue;
            }
            
            // 取出主键
            if (f.getAnnotation(Id.class) != null) {
                id = value;
            }
            
            // 获取普通属性的@Column注解
            Column propRelColumn = f.getAnnotation(Column.class);
            if (propRelColumn != null) {
                String columnName = propRelColumn.value();
                sql.append(NameConvert.humpToUnderLine(columnName)).append(",");
            } else {
                sql.append(NameConvert.humpToUnderLine(key)).append(",");
            }
            newParam.add(value);
            wildCard.append("?,");
        }
        
        if (newParam.isEmpty()) {
            log.warn("没有可插入的字段，entity: {}", clazz.getName());
            return null;
        }
        
        sql.delete(sql.length() - 1, sql.length());
        wildCard.delete(wildCard.length() - 1, wildCard.length());
        sql.append(") values(").append(wildCard).append(")");
        
        this.getJdbcOperations().update(sql.toString(), newParam.toArray());
        return id == null ? null : Long.parseLong(id.toString());
    }

    /**
     * 有选择地批量更新记录，空值不插入(采用数据库可能存在的默认值)。
     * 开源版实现：批量调用 dynamicUpdateByEntity
     */
    @Override
    public <E> void dynamicBatchUpdateByEntities(List<E> entities, boolean isAutoFill) {
        if (isAutoFill) {
            Long uId = this.getUserId();
            String uIp = this.getUserIp();
            entities.forEach(entity -> EntityAssists.beforeUpdated(entity, uId, uIp));
        }
        // 对于批量操作，使用循环调用单个更新（简单实现）
        // 如果需要真正的批量 SQL，可以使用 JdbcTemplate.batchUpdate()
        for (E entity : entities) {
            dynamicUpdateByEntityInternal(entity, false);
        }
    }

    /**
     * 有选择地批量insert记录，空值不插入(采用数据库可能存在的默认值)。
     * 开源版实现：使用 JdbcTemplate.batchUpdate() 实现真正的批量操作
     */
    @Override
    public <E> void dynamicBatchInsertByEntities(List<E> entities, boolean isAutoFill) {
        if (isAutoFill) {
            Long uId = this.getUserId();
            String uIp = this.getUserIp();
            entities.forEach(entity -> EntityAssists.beforeInsert(entity, com.wldos.framework.common.IDGen.nextId(), uId, uIp, false));
        }
        
        if (entities.isEmpty()) {
            return;
        }
        
        Class<?> clazz = this.clazz(entities.get(0));
        // 获取表名
        Table entityRelTable = clazz.getAnnotation(Table.class);
        String tableName = null;
        if (entityRelTable != null) {
            tableName = entityRelTable.value();
        }
        if (ObjectUtils.isBlank(tableName)) {
            tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
        }
        
        // 构建批量插入 SQL（基于第一个实体的字段）
        Map<String, Object> firstParams = ObjectUtils.toMap(entities.get(0));
        List<String> columnNames = new ArrayList<>();
        List<String> fieldNames = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : firstParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            
            Field f = ReflectionUtils.findField(clazz, key);
            if (f == null) {
                continue;
            }
            
            Column propRelColumn = f.getAnnotation(Column.class);
            if (propRelColumn != null) {
                columnNames.add(NameConvert.humpToUnderLine(propRelColumn.value()));
            } else {
                columnNames.add(NameConvert.humpToUnderLine(key));
            }
            fieldNames.add(key);
        }
        
        if (columnNames.isEmpty()) {
            log.warn("没有可插入的字段，entity: {}", clazz.getName());
            return;
        }
        
        StringBuilder sql = new StringBuilder("insert into ").append(tableName).append("(");
        sql.append(String.join(",", columnNames));
        sql.append(") values(");
        sql.append(columnNames.stream().map(c -> "?").collect(Collectors.joining(",")));
        sql.append(")");
        
        // 构建批量参数
        List<Object[]> paramsList = entities.stream().map(entity -> {
            Map<String, Object> params = ObjectUtils.toMap(entity);
            Object[] objects = new Object[fieldNames.size()];
            for (int i = 0; i < fieldNames.size(); i++) {
                objects[i] = params.get(fieldNames.get(i));
            }
            return objects;
        }).collect(Collectors.toList());
        
        this.getJdbcOperations().batchUpdate(sql.toString(), paramsList);
    }

    /**
     * 批量删除
     * 开源版实现：支持物理删除和逻辑删除
     */
    @Override
    public <E> void deleteByIds(E entity, Object[] ids, boolean isLogic) {
        if (entity == null || ids == null || ids.length == 0) {
            return;
        }
        
        Class<?> clazz = this.clazz(entity);
        // 获取表名
        Table entityRelTable = clazz.getAnnotation(Table.class);
        String tableName = null;
        if (entityRelTable != null) {
            tableName = entityRelTable.value();
        }
        if (ObjectUtils.isBlank(tableName)) {
            tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
        }
        
        // 取出主键字段名
        String pkName = this.getIdColNameByEntity(entity);
        
        StringBuilder sql = isLogic 
                ? new StringBuilder(UPDATE).append(tableName).append(" set delete_flag='deleted' ")
                : new StringBuilder("delete from ").append(tableName);
        sql.append(WHERE).append(pkName).append(" in (");
        sql.append(String.join(",", java.util.Arrays.stream(ids).map(id -> "?").collect(Collectors.toList())));
        sql.append(")");
        
        this.getJdbcOperations().update(sql.toString(), ids);
    }

    /**
     * 根据双组合主键批量删除
     * 开源版实现：支持物理删除和逻辑删除
     */
    @Override
    public <E> void deleteByMultiIds(E entity, Object[] ids, Object pid, boolean isLogic) {
        if (entity == null || ids == null || ids.length == 0) {
            return;
        }
        
        Class<?> clazz = this.clazz(entity);
        // 获取表名
        Table entityRelTable = clazz.getAnnotation(Table.class);
        String tableName = null;
        if (entityRelTable != null) {
            tableName = entityRelTable.value();
        }
        if (ObjectUtils.isBlank(tableName)) {
            tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
        }
        
        // 取出组合主键字段名
        List<String> pkNames = this.getMultiIdColNamesByEntity(clazz);
        if (pkNames.size() < 2) {
            log.warn("实体不是组合主键，entity: {}", clazz.getName());
            return;
        }
        
        StringBuilder sql = isLogic 
                ? new StringBuilder(UPDATE).append(tableName).append(" set delete_flag='deleted' ")
                : new StringBuilder("delete from ").append(tableName);
        sql.append(WHERE).append(pkNames.get(0)).append(" in (");
        sql.append(String.join(",", java.util.Arrays.stream(ids).map(id -> "?").collect(Collectors.toList())));
        sql.append(") and ").append(pkNames.get(1)).append(" = ?");
        
        // 构建参数数组
        Object[] params = new Object[ids.length + 1];
        System.arraycopy(ids, 0, params, 0, ids.length);
        params[ids.length] = pid;
        
        this.getJdbcOperations().update(sql.toString(), params);
    }

    @Override
    public List<Long> listSuperAdmin() {
        // 开源版简化实现：返回空列表
        return new ArrayList<>();
    }

    @Override
    public List<Long> listTrustMan() {
        // 开源版简化实现：返回空列表
        return new ArrayList<>();
    }

    @Override
    public List<Long> listTenantAdmin() {
        // 开源版简化实现：返回空列表
        return new ArrayList<>();
    }

    @Override
    public boolean isAdmin(Long userId) {
        // 开源版简化实现：返回 false
        return false;
    }

    @Override
    public boolean isCanTrust(Long userId) {
        // 开源版简化实现：返回 false
        return false;
    }

    @Override
    public boolean isTenantAdmin(Long userId) {
        // 开源版简化实现：返回 false
        return false;
    }

    @Override
    public Long getUserId() {
        try {
            HttpServletRequest request = getRequest();
            if (request == null) {
                return Constants.SYSTEM_USER_ID;
            }
            return Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID));
        } catch (Exception e) {
            log.warn("获取用户id为空：{}", this.getUserIp());
            return Constants.GUEST_ID;
        }
    }

    @Override
    public String getToken() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getHeader(this.tokenHeader) : null;
    }
    
    /**
     * 获取当前 HTTP 请求（开源版实现，使用 Spring RequestContextHolder）
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    @Override
    public boolean isMultiTenancy() {
        return this.isMultiTenancy;
    }

    @Override
    public Long getTenantId() {
        try {
            HttpServletRequest request = getRequest();
            if (request == null) {
                return Constants.TOP_COM_ID;
            }
            return Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_TENANT));
        } catch (NumberFormatException e) {
            log.warn("获取用户的归属租户企业id为空：{}", this.getUserIp());
            return Constants.TOP_COM_ID;
        }
    }

    @Override
    public String getPlatDomain() {
        return this.wldosDomain;
    }

    @Override
    public boolean isMultiDomain() {
        return this.isMultiDomain;
    }

    @Override
    public String getDomain() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "";
        }
        // 从请求头获取域名
        String domain = request.getHeader(this.domainHeader);
        if (StringUtils.isBlank(domain)) {
            // 如果没有请求头，从 Host 获取
            domain = request.getHeader("Host");
        }
        return domain != null ? domain : "";
    }

    @Override
    public Long getDomainId() {
        try {
            HttpServletRequest request = getRequest();
            if (request == null) {
                return Constants.DEFAULT_DOMAIN_ID;
            }
            return Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_DOMAIN));
        } catch (NumberFormatException e) {
            log.warn("获取当前请求域id为空：{}", this.getUserIp());
            return Constants.DEFAULT_DOMAIN_ID;
        }
    }

    @Override
    public String getUserIp() {
        HttpServletRequest request = getRequest();
        return request != null ? IpUtils.getClientIp(request) : "127.0.0.1";
    }

    @Override
    public void applyDomainFilter(PageQuery pageQuery) {
        if (this.isMultiDomain) {
            pageQuery.pushParam(Constants.COMMON_KEY_DOMAIN_COLUMN, this.getDomainId());
        }
    }

    @Override
    public void applyTenantFilter(PageQuery pageQuery) {
        Long userId = this.getUserId();
        if (this.isMultiTenancy && !this.isAdmin(userId)) {
            if (this.isTenantAdmin(userId)) {
                // TODO: 实现 getUserComId 方法
                // pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getUserComId(userId));
            } else {
                pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
            }
        }
    }

    @Override
    public long getTokenExpTime() {
        try {
            HttpServletRequest request = getRequest();
            if (request != null) {
                return Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_TOKEN_EXPIRE_TIME));
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        return 0L;
    }

    @Override
    public String genStateCode() {
        String uid = UUIDUtils.generateShortUuid();
        String text = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        this.cache.set(String.format(RedisKeyEnum.STATE.toString(), uid), text, 2, TimeUnit.MINUTES);
        return text + uid;
    }
    
    /**
     * 构建 COUNT SQL（优化：对于简单查询，直接 COUNT；对于复杂查询，使用子查询）
     */
    private String buildCountSql(String sql) {
        if (isSimpleQuery(sql)) {
            String countSql = extractCountSql(sql);
            if (countSql != null) {
                return countSql;
            }
        }
        return "select count(1) as total from ( " + sql + " ) w";
    }
    
    /**
     * 判断是否为简单查询
     */
    private boolean isSimpleQuery(String sql) {
        String upperSql = sql.toUpperCase();
        return !upperSql.contains("SELECT") || 
               (!upperSql.contains(" UNION ") && 
                !upperSql.contains(" INTERSECT ") && 
                !upperSql.contains(" EXCEPT ") &&
                !upperSql.contains(" GROUP BY ") &&
                !upperSql.contains(" DISTINCT ") &&
                !upperSql.contains("(SELECT") &&
                !upperSql.contains(" JOIN "));
    }
    
    /**
     * 从简单查询中提取 COUNT SQL
     */
    private String extractCountSql(String sql) {
        try {
            String upperSql = sql.toUpperCase().trim();
            int fromIndex = upperSql.indexOf(" FROM ");
            if (fromIndex < 0) {
                return null;
            }
            
            int whereIndex = upperSql.indexOf(" WHERE ", fromIndex);
            String fromClause;
            if (whereIndex >= 0) {
                fromClause = sql.substring(fromIndex + 6, sql.length());
            } else {
                int orderByIndex = upperSql.indexOf(" ORDER BY ", fromIndex);
                int limitIndex = upperSql.indexOf(" LIMIT ", fromIndex);
                int endIndex = sql.length();
                if (orderByIndex >= 0) endIndex = Math.min(endIndex, orderByIndex);
                if (limitIndex >= 0) endIndex = Math.min(endIndex, limitIndex);
                fromClause = sql.substring(fromIndex + 6, endIndex);
            }
            
            return "select count(1) as total from " + fromClause.trim();
        } catch (Exception e) {
            return null;
        }
    }
}
