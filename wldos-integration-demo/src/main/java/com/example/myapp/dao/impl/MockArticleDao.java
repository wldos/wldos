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
package com.example.myapp.dao.impl;

import com.example.myapp.dao.ArticleDao;
import com.example.myapp.entity.Article;
import com.wldos.framework.common.CommonOperation;
import com.wldos.framework.common.SaveOptions;
import org.springframework.stereotype.Repository;

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
@Repository
public class MockArticleDao implements ArticleDao {
    
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
     * 统一的保存或更新方法（推荐使用）
     * 
     * 功能特性：
     * 1. 自动判断 insert/update（通过 id 是否存在）
     * 2. 支持乐观锁（@Version 字段自动维护）
     * 3. 自动填充公共字段（createTime, updateTime 等）
     * 4. 只写入非空字段（选择性插入/更新）
     * 
     * @param entity 实体
     * @return 保存或更新后的实体对象
     */
    @Override
    public Article saveOrUpdate(Article entity) {
        return saveOrUpdate(entity, SaveOptions.defaults());
    }
    
    /**
     * 统一的保存或更新方法（使用配置对象）
     * 
     * @param entity 实体
     * @param options 保存选项配置
     * @return 保存或更新后的实体对象
     */
    @Override
    public Article saveOrUpdate(Article entity, SaveOptions options) {
        if (entity == null) {
            return null;
        }
        
        // 判断是新增还是更新
        boolean isNew = (entity.getId() == null || !dataStore.containsKey(entity.getId()));
        
        if (isNew) {
            // 新增：自动生成ID
            if (entity.getId() == null) {
                entity.setId(idGenerator.getAndIncrement());
            }
            
            // 自动填充创建时间
            if (options.isAutoFill() && entity.getCreateTime() == null) {
                entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
            }
        }
        
        // 自动填充更新时间
        if (options.isAutoFill()) {
            entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        }
        
        // 保存到内存存储
        dataStore.put(entity.getId(), entity);
        
        return entity;
    }
    
    /**
     * 批量保存或更新（推荐使用）
     * 
     * @param entities 实体集合
     * @return 保存或更新后的实体集合
     */
    @Override
    public Iterable<Article> saveOrUpdateAll(Iterable<Article> entities) {
        return saveOrUpdateAll(entities, SaveOptions.defaults());
    }
    
    /**
     * 批量保存或更新（使用配置对象）
     * 
     * @param entities 实体集合
     * @param options 保存选项配置
     * @return 保存或更新后的实体集合
     */
    @Override
    public Iterable<Article> saveOrUpdateAll(Iterable<Article> entities, SaveOptions options) {
        List<Article> results = new ArrayList<>();
        if (entities != null) {
            for (Article entity : entities) {
                Article saved = saveOrUpdate(entity, options);
                results.add(saved);
            }
        }
        return results;
    }
    
    /**
     * 根据ID查询
     * 
     * @param id 主键
     * @return 实体对象，如果不存在返回 Optional.empty()
     */
    @Override
    public Optional<Article> findById(Long id) {
        Article article = dataStore.get(id);
        return Optional.ofNullable(article);
    }
    
    /**
     * 查询所有
     * 
     * @return 所有实体对象
     */
    @Override
    public Iterable<Article> findAll() {
        return new ArrayList<>(dataStore.values());
    }
    
    /**
     * 根据ID列表查询
     * 
     * @param ids 主键列表
     * @return 实体对象列表
     */
    @Override
    public Iterable<Article> findAllById(Iterable<Long> ids) {
        List<Article> results = new ArrayList<>();
        if (ids != null) {
            for (Long id : ids) {
                Article article = dataStore.get(id);
                if (article != null) {
                    results.add(article);
                }
            }
        }
        return results;
    }
    
    /**
     * 判断是否存在
     * 
     * @param id 主键
     * @return 是否存在
     */
    @Override
    public boolean existsById(Long id) {
        return dataStore.containsKey(id);
    }
    
    /**
     * 统计数量
     * 
     * @return 总数量
     */
    @Override
    public long count() {
        return dataStore.size();
    }
    
    /**
     * 删除实体
     * 
     * @param entity 实体对象
     */
    @Override
    public void delete(Article entity) {
        if (entity != null && entity.getId() != null) {
            dataStore.remove(entity.getId());
        }
    }
    
    /**
     * 根据ID删除
     * 
     * @param id 主键
     */
    @Override
    public void deleteById(Long id) {
        dataStore.remove(id);
    }
    
    /**
     * 批量删除
     * 
     * @param entities 实体对象集合
     */
    @Override
    public void deleteAll(Iterable<? extends Article> entities) {
        if (entities != null) {
            for (Article entity : entities) {
                delete(entity);
            }
        }
    }
    
    /**
     * 删除所有
     */
    @Override
    public void deleteAll() {
        dataStore.clear();
    }
    
    /**
     * 获取 CommonOperation 实例
     * 
     * 用于调用框架提供的其他高级操作：
     * - execQueryForList: 父子表查询
     * - execQueryForPage: 分页查询
     * - dynamicUpdateByEntity: 动态更新
     * - dynamicInsertByEntity: 动态插入
     * - deleteByIds: 批量删除
     * 等等...
     * 
     * 注意：这是一个 Mock 实现，实际项目中应该返回真实的 CommonOperation 实例
     * 
     * @return CommonOperation 实例
     */
    @Override
    public CommonOperation getCommonOperation() {
        // 注意：实际项目中，应该通过依赖注入获取 CommonOperation 实例
        // 这里返回 null，因为 Mock 实现不需要真实的数据库操作
        // 实际使用示例：
        // @Autowired
        // private CommonOperation commonOperation;
        // 
        // @Override
        // public CommonOperation getCommonOperation() {
        //     return commonOperation;
        // }
        return null;
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
