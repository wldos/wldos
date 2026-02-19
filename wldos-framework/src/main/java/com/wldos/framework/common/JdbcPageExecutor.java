/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.common;

import com.wldos.common.res.PageData;
import com.wldos.common.res.PageQuery;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JDBC 分页执行模板：接收完整 SQL（含 ORDER BY，不含 LIMIT）+ 位置参数，统一完成 count、深分页/正向 list、组装 PageData。
 * Fragment 实现类只需拼装 SQL 与 params，然后调用 {@link #executePage} 即可。
 *
 * @author 元悉宇宙
 */
public final class JdbcPageExecutor {

    private static final String ORDER_BY_PREFIX = " order by ";
    private static final String DESC_SUFFIX = " desc";
    private static final String ASC_SUFFIX = " asc";
    private static final String DEFAULT_ORDER_BY = " order by 1 ";

    private JdbcPageExecutor() {
    }

    /**
     * 移除 SQL 末尾的 ORDER BY 子句（用于 count 与深分页反向子查询）。
     */
    public static String stripOrderBy(String sql) {
        if (sql == null || sql.isEmpty()) return sql;
        int idx = sql.toLowerCase().lastIndexOf(ORDER_BY_PREFIX);
        return idx > 0 ? sql.substring(0, idx).trim() : sql;
    }

    /**
     * 提取 SQL 中的 ORDER BY 子句，无则返回默认 " order by 1 "。
     */
    public static String extractOrderBy(String sql) {
        if (sql == null || sql.isEmpty()) return DEFAULT_ORDER_BY;
        int idx = sql.toLowerCase().lastIndexOf(ORDER_BY_PREFIX);
        return idx > 0 ? sql.substring(idx) : DEFAULT_ORDER_BY;
    }

    /**
     * 反转 ORDER BY 方向（ASC↔DESC），用于深分页反向查询。
     */
    public static String reverseOrderBy(String sql) {
        String orderBy = extractOrderBy(sql).trim();
        if (!orderBy.toLowerCase().startsWith("order by")) return DEFAULT_ORDER_BY;
        String clause = orderBy.substring(ORDER_BY_PREFIX.length()).trim();
        String[] parts = clause.split(",");
        List<String> reversed = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            if (part.toLowerCase().endsWith(DESC_SUFFIX)) {
                reversed.add(part.substring(0, part.length() - DESC_SUFFIX.length()).trim() + ASC_SUFFIX);
            } else if (part.toLowerCase().endsWith(ASC_SUFFIX)) {
                reversed.add(part.substring(0, part.length() - ASC_SUFFIX.length()).trim() + DESC_SUFFIX);
            } else {
                reversed.add(part + DESC_SUFFIX);
            }
        }
        return ORDER_BY_PREFIX + String.join(", ", reversed) + " ";
    }

    /**
     * 分页执行模板方法：count → 深分页或正向 limit → 返回 PageData。
     *
     * @param fullSql   完整 SELECT（含 WHERE、ORDER BY），不含 LIMIT
     * @param params    位置参数，与 fullSql 中 ? 一一对应
     * @param pageQuery 分页参数，可为 null（则 current=1, pageSize=20）
     * @param jdbcOps   JdbcTemplate
     * @param rowMapper 行映射
     * @return PageData
     */
    public static <T> PageData<T> executePage(String fullSql, List<Object> params,
                                               PageQuery pageQuery, JdbcOperations jdbcOps,
                                               RowMapper<T> rowMapper) {
        if (pageQuery == null) {
            pageQuery = new PageQuery();
        }
        int current = pageQuery.getCurrent() <= 0 ? 1 : pageQuery.getCurrent();
        int pageSize = pageQuery.getPageSize() <= 0 ? 20 : pageQuery.getPageSize();

        String countSql = "select count(1) as total from (" + fullSql + ") w";
        List<Map<String, Object>> countRows = jdbcOps.queryForList(countSql, params != null ? params.toArray() : new Object[0]);
        long total = countRows.isEmpty() ? 0 : Long.parseLong(String.valueOf(countRows.get(0).get("total")));
        int totalPages = (int) Math.max(1, (total - 1) / pageSize + 1);
        current = Math.min(current, totalPages);
        int offset = (current - 1) * pageSize;

        List<T> rows;
        if (pageSize > 0) {
            if (offset > total / 2) {
                // 深分页反向查询：从末尾倒查，避免大 offset 扫描
                String sqlForCount = stripOrderBy(fullSql);
                String reversedOrderBy = reverseOrderBy(fullSql);
                int reverseOffset = (int) Math.max(0, total - offset - pageSize);
                String reverseSql = "select * from (" + sqlForCount + " " + reversedOrderBy + " limit " + reverseOffset + ", " + pageSize + ") t " + extractOrderBy(fullSql);
                Object[] args = params != null ? params.toArray() : new Object[0];
                rows = jdbcOps.query(reverseSql, rowMapper, args);
            } else {
                List<Object> listParams = params != null ? new ArrayList<>(params) : new ArrayList<>();
                listParams.add(offset);
                listParams.add(pageSize);
                rows = jdbcOps.query(fullSql + " limit ?, ? ", rowMapper, listParams.toArray());
            }
        } else {
            rows = jdbcOps.query(fullSql, rowMapper, params != null ? params.toArray() : new Object[0]);
        }
        return new PageData<>(total, current, pageSize, rows != null ? rows : Collections.emptyList());
    }
}
