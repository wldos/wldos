/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/*
 * WLDOS 第三方集成示例项目
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
package com.example.myapp.dao;

import com.example.myapp.entity.Article;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 文章数据访问层 Mock 实现
 * 
 * 演示框架 2.3.6 中常用的 CRUD API：
 * 1. saveOrUpdate: 统一的保存或更新方法（自动判断 insert/update）
 * 2. saveOrUpdateAll: 批量保存或更新
 * 3. findById: 根据ID查询
 * 4. findAll: 查询所有
 * 5. delete: 删除实体
 * 6. deleteById: 根据ID删除
 * 7. getCommonOperation: 获取 CommonOperation 实例，用于其他高级操作
 * 
 * 注意：这是一个 Mock 实现，使用内存存储模拟数据库操作
 * 实际项目中，应该使用 Spring Data JDBC 或 MyBatis-Plus 来实现
 */
public class MockArticleDaoImpl implements MockArticleJdbc {
    
    // 使用内存 Map 模拟数据库存储
    private final Map<Long, Article> dataStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    // 初始化一些示例数据
    {
        // 初始化示例数据
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Article article1 = new Article(1L, "WLDOS 框架介绍", "WLDOS 是一个强大的应用支撑平台...", "张三", 100, 1);
        article1.setCreateTime(now);
        article1.setUpdateTime(now);
        dataStore.put(1L, article1);
        
        Article article2 = new Article(2L, "Spring Boot 最佳实践", "Spring Boot 是 Java 开发的首选框架...", "李四", 200, 1);
        article2.setCreateTime(now);
        article2.setUpdateTime(now);
        dataStore.put(2L, article2);
        
        Article article3 = new Article(3L, "数据库设计规范", "良好的数据库设计是系统稳定的基础...", "王五", 150, 0);
        article3.setCreateTime(now);
        article3.setUpdateTime(now);
        dataStore.put(3L, article3);
        
        idGenerator.set(4L);
    }
    
    /**
     * 自定义方法：根据作者查询文章列表
     * 演示如何添加自定义查询方法
     * 
     * @param author 作者
     * @return 文章列表
     */
    public List<Article> findByAuthor(String author) {
        return dataStore.values().stream()
                .filter(article -> author != null && author.equals(article.getAuthor()))
                .collect(Collectors.toList());
    }
    
    /**
     * 自定义方法：根据状态查询文章列表
     * 
     * @param status 状态（0-草稿, 1-已发布, 2-已删除）
     * @return 文章列表
     */
    public List<Article> findByStatus(Integer status) {
        return dataStore.values().stream()
                .filter(article -> status != null && status.equals(article.getStatus()))
                .collect(Collectors.toList());
    }
}
