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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.wldos.framework.support.internal.BaseWrap;
import com.wldos.framework.common.FreeJdbcTemplate;
import com.wldos.common.dto.LevelNode;
import com.wldos.common.dto.SQLTable;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.NameConvert;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.framework.support.internal.God;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

/**
 * 开源版 FreeJdbcTemplate 实现类
 * 提供基础的 JDBC 操作模板功能，不依赖加密 JAR
 * 
 * 注意：此实现类为开源版本，功能与加密版保持一致，但不包含商业授权相关的安全特性
 * 
 * @author 元悉宇宙
 * @date 2026-01-08
 * @version 2.0
 */
public class OpenSourceFreeJdbcTemplate extends BaseWrap implements FreeJdbcTemplate {
    
    private static final Logger log = LoggerFactory.getLogger(OpenSourceFreeJdbcTemplate.class);
    
    public OpenSourceFreeJdbcTemplate(BaseWrap baseWrap) {
        super(baseWrap);
    }
    
    public OpenSourceFreeJdbcTemplate(God jdbcTemplate) {
        super(jdbcTemplate.getBaseWrap());
    }

    @Override
    public JdbcAggregateTemplate getJdbcAggTemplate() {
        return this.jdbcAggTemplate;
    }

    @Override
    public NamedParameterJdbcTemplate getNamedParamJdbcTemplate() {
        return namedParamJdbcTemplate;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return this.namedParamJdbcTemplate.getJdbcTemplate();
    }

    @Override
    public JdbcOperations getJdbcOperations() {
        return this.namedParamJdbcTemplate.getJdbcOperations();
    }

    @Override
    public <E> E queryForObject(Class<E> clazz, String sql, Object[] params) {
        return this.getJdbcOperations().queryForObject(sql, new BeanPropertyRowMapper<>(clazz), params);
    }

    @Override
    public <C> List<C> queryForList(Class<C> clazz, String sql, Object[] params) {
        return this.getJdbcOperations().query(sql, new BeanPropertyRowMapper<>(clazz), params);
    }

    @Override
    public List<Map<String, Object>> execQueryForPage(String sql, int currentPage, int pageSize, Object[] params) {
        int startRow = (currentPage - 1) * pageSize;
        String pageSql = sql + " limit " + startRow + "," + pageSize;
        return this.getJdbcOperations().queryForList(pageSql, params);
    }

    @Override
    public <E> List<E> execQueryForPage(Class<E> clazz, String sql, int currentPage, int pageSize, Object... params) {
        int startRow = (currentPage - 1) * pageSize;
        String pageSql = sql + " limit " + startRow + "," + pageSize;
        return this.getJdbcOperations().query(pageSql, new BeanPropertyRowMapper<>(clazz), params);
    }

    @Override
    public PageableResult<Map<String, Object>> execQueryForPageNoOrder(String sql, int currentPage, int pageSize, Object... params) {
        String countSql = "select count(1) as total from (" + sql + ") t";
        Integer total = this.getJdbcOperations().queryForObject(countSql, Integer.class, params);
        if (total == null) {
            total = 0;
        }
        
        int totalPageNum = (total - 1) / pageSize + 1;
        currentPage = Math.min(currentPage, totalPageNum);
        
        List<Map<String, Object>> list = execQueryForPage(sql, currentPage, pageSize, params);
        return new PageableResult<>(total, currentPage, pageSize, list);
    }

    @Override
    public <E> PageableResult<E> execQueryForPageNoOrder(Class<E> clazz, String sql, int currentPage, int pageSize, Object... params) {
        String countSql = "select count(1) as total from (" + sql + ") t";
        Integer total = this.getJdbcOperations().queryForObject(countSql, Integer.class, params);
        if (total == null) {
            total = 0;
        }
        
        int totalPageNum = (total - 1) / pageSize + 1;
        currentPage = Math.min(currentPage, totalPageNum);
        
        List<E> list = execQueryForPage(clazz, sql, currentPage, pageSize, params);
        return new PageableResult<>(total, currentPage, pageSize, list);
    }

    @Override
    public PageableResult<Map<String, Object>> execQueryForPage(String sql, String sqlOrder, int currentPage, int pageSize, Object[] params) {
        String finalSql = sql + " " + sqlOrder;
        return execQueryForPageNoOrder(finalSql, currentPage, pageSize, params);
    }

    @Override
    public <E> PageableResult<E> execQueryForPage(Class<E> clazz, String sql, String sqlOrder, PageQuery pageQuery, Object... params) {
        String finalSql = sql + " " + sqlOrder;
        return execQueryForPageNoOrder(clazz, finalSql, pageQuery.getCurrent(), pageQuery.getPageSize(), params);
    }

    @Override
    public <E> String getTableNameByEntity(Class<E> clazz) {
        Table entityRelTable = clazz.getAnnotation(Table.class);
        if (entityRelTable != null) {
            return entityRelTable.value();
        }
        return NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
    }

    @Override
    public <E> String getIdColNameByEntity(E entity) {
        return getIdColNameByEntity((Class<E>) entity.getClass());
    }

    @Override
    public <E> String getIdColNameByEntity(Class<E> clazz) {
        String key = clazz.getName();
        Object value = this.cache.get(key);
        String pKeyName = "";
        if (value == null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.getAnnotation(Id.class) != null) {
                    Column propRelColumn = f.getAnnotation(Column.class);
                    if (propRelColumn != null) {
                        pKeyName = propRelColumn.value();
                    } else {
                        pKeyName = NameConvert.humpToUnderLine(f.getName());
                    }
                    break;
                }
            }
            this.cache.set(key, pKeyName, 30, TimeUnit.MINUTES);
            return pKeyName;
        }
        return String.valueOf(value);
    }

    @Override
    public <E> List<String> getMultiIdColNamesByEntity(Class<E> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> pKeyNames = new ArrayList<>();
        for (Field f : fields) {
            if (f.getAnnotation(Id.class) != null) {
                Column propRelColumn = f.getAnnotation(Column.class);
                if (propRelColumn != null) {
                    pKeyNames.add(propRelColumn.value());
                } else {
                    pKeyNames.add(NameConvert.humpToUnderLine(f.getName()));
                }
            }
        }
        return pKeyNames;
    }

    @Override
    public <E> Class<E> clazz(E entity) {
        Class<E> clazz = null;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            try {
                clazz = (Class<E>) Class.forName(entity.getClass().getName(), false, contextClassLoader);
                return clazz;
            } catch (ClassNotFoundException e1) {
                log.warn("未找到类：{}", entity.getClass().getCanonicalName());
            }
        }
        try {
            clazz = (Class<E>) Class.forName(entity.getClass().getName());
        } catch (Exception e) {
            log.warn("未找到类，去插件中找：{}", entity.getClass().getCanonicalName());
        }
        return clazz;
    }

    @Override
    public StringBuilder querySqlByTable(String tableName, Class<?> entity, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter) {
        StringBuilder sql = new StringBuilder(50).append("select a.* from ").append(tableName).append(" ").append("a").append(" where 1=1 ");
        
        if (!ObjectUtils.isBlank(condition)) {
            StringBuilder querySql = new StringBuilder();
            condition.forEach((key, value) -> {
                if (value == null) return;
                Field field = ReflectionUtils.findField(entity, key);
                if (field == null) return;
                if (field.getType().equals(String.class)) {
                    querySql.append(" and instr(a.").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
                } else if (field.getType().equals(Timestamp.class)) {
                    querySql.append(" and DATE_FORMAT(a.").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
                } else {
                    querySql.append(" and a.").append(NameConvert.humpToUnderLine(key)).append("= ? ");
                }
                params.add(value);
            });
            sql.append(querySql);
        }
        
        if (!ObjectUtils.isBlank(filter)) {
            StringBuilder finalSql = new StringBuilder();
            List<Field> fieldList = this.fieldsMatchFilter(filter, entity);
            fieldList.forEach(f -> {
                String key = f.getName();
                List<Object> value = filter.get(key);
                finalSql.append(" and a.").append(NameConvert.humpToUnderLine(key)).append(" in (");
                value.forEach(item -> {
                    finalSql.append("?,");
                    params.add(item);
                });
                if (finalSql.length() > 0) {
                    finalSql.delete(finalSql.length() - 1, finalSql.length());
                    finalSql.append(")");
                }
            });
            sql.append(finalSql);
        }
        
        return sql;
    }

    @Override
    public StringBuilder querySqlByTable(String sqlNoWhere, SQLTable[] sqlTables, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter) {
        StringBuilder sql = sqlNoWhere.contains(" where ") ? new StringBuilder().append(sqlNoWhere) : new StringBuilder().append(sqlNoWhere).append(" where 1=1 ");
        
        if (!ObjectUtils.isBlank(condition)) {
            StringBuilder querySql = new StringBuilder();
            Arrays.stream(sqlTables).forEach(sqlTable -> {
                String alias = sqlTable.getAlias();
                condition.forEach((key, value) -> {
                    if (value == null) return;
                    Field field = ReflectionUtils.findField(sqlTable.getType(), key);
                    if (field == null) return;
                    if (field.getType().equals(String.class)) {
                        querySql.append(" and instr(").append(alias).append(".").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
                    } else if (field.getType().equals(Timestamp.class)) {
                        querySql.append(" and DATE_FORMAT(").append(alias).append(".").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
                    } else {
                        querySql.append(" and ").append(alias).append(".").append(NameConvert.humpToUnderLine(key)).append("= ? ");
                    }
                    params.add(value);
                });
            });
            sql.append(querySql);
        }
        
        if (!ObjectUtils.isBlank(filter)) {
            StringBuilder finalSql = new StringBuilder();
            Arrays.stream(sqlTables).forEach(sqlTable -> {
                String alias = sqlTable.getAlias();
                List<Field> fieldList = this.fieldsMatchFilter(filter, sqlTable.getType());
                fieldList.forEach(f -> {
                    String key = f.getName();
                    List<Object> value = filter.get(key);
                    finalSql.append(" and ").append(alias).append(".").append(NameConvert.humpToUnderLine(key)).append(" in (");
                    value.forEach(item -> {
                        finalSql.append("?,");
                        params.add(item);
                    });
                    if (finalSql.length() > 0) {
                        finalSql.delete(finalSql.length() - 1, finalSql.length());
                        finalSql.append(")");
                    }
                });
            });
            sql.append(finalSql);
        }
        
        return sql;
    }

    private <E> List<Field> fieldsMatchFilter(Map<String, List<Object>> filter, Class<E> entity) {
        return filter.entrySet().stream().filter(p -> p.getValue() != null).map(item ->
                ReflectionUtils.findField(entity, item.getKey())).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void orderSql(Sort sort, @NonNull StringBuilder querySql) {
        if (sort != null && !sort.isEmpty()) {
            StringBuilder temp = new StringBuilder(" order by ");
            StringBuilder finalTemp = temp;
            sort.stream().iterator().forEachRemaining(s ->
                    finalTemp.append(NameConvert.humpToUnderLine(s.getProperty())).append(" ").append(s.isAscending() ? "," : " desc,"));
            if (temp.toString().endsWith(",")) {
                temp = temp.delete(temp.length() - 1, temp.length());
            }
            querySql.append(temp);
        }
    }

    @Override
    public String makeBaseExistsSql(String cTable, String cAlias, String pAlias, String pIdKey) {
        return "select 1 from " + cTable + " " + cAlias + " where " + cAlias + "." + pIdKey + "=" + pAlias + ".id";
    }

    @Override
    public String existsSql(String cAlias, Class<?> entity, String baseExistSql, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter) {
        if (ObjectUtils.isBlank(condition) && ObjectUtils.isBlank(filter)) {
            return "";
        }
        
        StringBuilder existsSql = new StringBuilder(" and exists(").append(baseExistSql);
        int start = params.size();
        
        if (!ObjectUtils.isBlank(condition)) {
            condition.forEach((key, value) -> {
                if (value == null) return;
                Field field = ReflectionUtils.findField(entity, key);
                if (field == null) return;
                if (field.getType().equals(String.class)) {
                    existsSql.append(" and instr(").append(cAlias).append(".").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
                } else if (field.getType().equals(Timestamp.class)) {
                    existsSql.append(" and date_format(").append(cAlias).append(".").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = date_format(?, '%Y-%m-%d') ");
                } else {
                    existsSql.append(" and ").append(cAlias).append(".").append(NameConvert.humpToUnderLine(key)).append("= ? ");
                }
                params.add(value);
            });
        }
        
        if (!ObjectUtils.isBlank(filter)) {
            StringBuilder finalSql = new StringBuilder();
            List<Field> fieldList = this.fieldsMatchFilter(filter, entity);
            fieldList.forEach(f -> {
                String key = f.getName();
                List<Object> value = filter.get(key);
                finalSql.append(" and ").append(cAlias).append(".").append(NameConvert.humpToUnderLine(key)).append(" in (");
                StringBuilder finalTemp = new StringBuilder();
                value.forEach(item -> {
                    finalTemp.append("?,");
                    params.add(item);
                });
                if (!ObjectUtils.isBlank(value)) {
                    finalSql.append(finalTemp.delete(finalTemp.length() - 1, finalTemp.length()));
                    finalSql.append(")");
                }
            });
            existsSql.append(finalSql);
        }
        
        if (params.size() - start > 0) {
            existsSql.append(")");
            return existsSql.toString();
        }
        
        return "";
    }

    @Override
    public String existsSql(String cAlias, Class<?> entity, String baseExistSql, List<Object> params, PageQuery pageQuery) {
        return this.existsSql(cAlias, entity, baseExistSql, params, pageQuery.getCondition(), pageQuery.getFilter());
    }

    /**
     * 通过父节点id查询所有子节点(含父节点自身)
     * 
     * 注意：此实现使用 MySQL 特定的语法（用户变量、GROUP_CONCAT、FIND_IN_SET），
     * 仅适用于 MySQL 数据库。如需支持其他数据库，请考虑使用递归 CTE 或数据库方言适配。
     * 
     * @param table 带parent_id 和 id的目标表名称
     * @param pId 要查询的父节点id
     * @return 一棵节点树
     */
    @Override
    public List<LevelNode> queryTreeByParentId(String table, Object pId) {
        String sql = "SELECT idt.level, target.id FROM (SELECT @ids AS _ids, (SELECT @ids := GROUP_CONCAT(id) FROM " +
                table + " WHERE FIND_IN_SET(parent_id, @ids) ) as cIds, @lv := @lv+1 as level FROM " +
                table + ", (SELECT @ids :=?, @lv := 0 ) b WHERE @ids IS NOT NULL ) idt, " +
                table + " target WHERE FIND_IN_SET(target.id, idt._ids) ORDER BY level, id";
        return this.getJdbcOperations().query(sql, new BeanPropertyRowMapper<>(LevelNode.class), pId);
    }

    /**
     * 通过子节点id查询所有父节点(含子节点自身)
     * 
     * 注意：此实现使用 MySQL 特定的语法（用户变量），仅适用于 MySQL 数据库。
     * 如需支持其他数据库，请考虑使用递归 CTE 或数据库方言适配。
     * 
     * @param table 带parent_id 和 id的目标表名称
     * @param cId 要查询的子节点id
     * @return 一棵节点树
     */
    @Override
    public List<LevelNode> queryTreeByChildId(String table, Object cId) {
        // 修复：与加密版实现保持一致，使用更简单的逐级向上查询方式
        String sql = "SELECT idt.level, target.id FROM (SELECT @id as _id, (SELECT @id := parent_id FROM " +
                table + " WHERE id = @id limit 1) as _pid, @l := @l+1 as level FROM " +
                table + ",(SELECT @id := ?, @l := 0 ) b WHERE @id > 0 ) idt, " +
                table + " target WHERE idt._id = target.id ORDER BY level desc";
        return this.getJdbcOperations().query(sql, new BeanPropertyRowMapper<>(LevelNode.class), cId);
    }

    @Override
    public int queryOnlineUserNum() {
        // 开源版暂不实现，返回 0
        return 0;
    }

    @Override
    public int queryUserSum() {
        // 开源版暂不实现，返回 0
        return 0;
    }

    @Override
    public int queryDomainSum() {
        // 开源版暂不实现，返回 0
        return 0;
    }

    @Override
    public int queryComSum() {
        // 开源版暂不实现，返回 0
        return 0;
    }
}
