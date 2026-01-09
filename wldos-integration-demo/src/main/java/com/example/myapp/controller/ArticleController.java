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
package com.example.myapp.controller;

import com.example.myapp.entity.Article;
import com.example.myapp.service.ArticleService;
import com.wldos.common.res.Result;
import com.wldos.framework.mvc.controller.EntityController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章管理 Controller
 * 
 * 演示使用 EntityController 基类进行实体 CRUD 操作
 * 展示框架 2.3.6 中常用的 CRUD API
 * 
 * EntityController 自动提供的接口：
 * - POST /api/articles/add - 新增文章
 * - POST /api/articles/update - 更新文章
 * - DELETE /api/articles/delete - 删除文章
 * - DELETE /api/articles/deletes - 批量删除文章
 * - GET /api/articles/get?id=xxx - 根据ID查询文章
 * - GET /api/articles/all - 查询所有文章
 * 
 * 框架自动处理：
 * - 统一响应格式（自动包装为 Result）
 * - 异常处理（自动捕获并返回统一格式）
 * - 多租户隔离（如果启用）
 * - 域隔离（如果启用）
 * - 自动填充公共字段（createTime, updateTime 等）
 * - 乐观锁支持（@Version 字段自动维护）
 */
@Api(tags = "文章管理（DAO层CRUD演示）")
@Slf4j
@RestController
@RequestMapping("/api/articles")
public class ArticleController extends EntityController<ArticleService, Article> {
    
    /**
     * 新增文章前的处理（Hook）
     * 可以在这里进行数据校验、设置默认值等
     */
    @Override
    protected void preAdd(Article entity) {
        log.info("新增文章前处理: {}", entity.getTitle());
        
        // 示例：设置默认值
        if (entity.getStatus() == null) {
            entity.setStatus(0); // 默认为草稿
        }
        if (entity.getViews() == null) {
            entity.setViews(0); // 默认浏览量为0
        }
    }
    
    /**
     * 新增文章后的处理（Hook）
     * 可以在这里刷新缓存、发送通知等
     */
    @Override
    protected void postAdd(Article entity) {
        log.info("新增文章后处理: {}", entity.getTitle());
        // 示例：刷新缓存
        // cache.refresh("articles");
    }
    
    /**
     * 更新文章前的处理（Hook）
     */
    @Override
    protected void preUpdate(Article entity) {
        log.info("更新文章前处理: {}", entity.getTitle());
    }
    
    /**
     * 更新文章后的处理（Hook）
     */
    @Override
    protected void postUpdate(Article entity) {
        log.info("更新文章后处理: {}", entity.getTitle());
    }
    
    /**
     * 删除文章前的处理（Hook）
     */
    @Override
    protected void preDelete(Article entity) {
        log.info("删除文章前处理: {}", entity.getId());
    }
    
    /**
     * 删除文章后的处理（Hook）
     */
    @Override
    protected void postDelete(Article entity) {
        log.info("删除文章后处理: {}", entity.getId());
    }
    
    /**
     * 结果集过滤器
     * 可以在这里对查询结果进行过滤
     */
    @Override
    protected List<Article> doFilter(List<Article> res) {
        // 示例：过滤掉已删除的文章
        return res.stream()
                .filter(article -> article.getStatus() != null && article.getStatus() != 2)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 自定义方法：根据作者查询文章列表
     * 演示调用 Service 的自定义方法
     * 
     * 访问：GET http://localhost:8080/api/articles/by-author?author=张三
     */
    @ApiOperation(value = "根据作者查询文章", notes = "演示调用 Service 的自定义方法")
    @GetMapping("/by-author")
    public Result getArticlesByAuthor(@RequestParam String author) {
        List<Article> articles = service.findByAuthor(author);
        return Result.ok(articles);
    }
    
    /**
     * 自定义方法：根据状态查询文章列表
     * 
     * 访问：GET http://localhost:8080/api/articles/by-status?status=1
     */
    @ApiOperation(value = "根据状态查询文章", notes = "演示根据状态查询文章列表")
    @GetMapping("/by-status")
    public Result getArticlesByStatus(@RequestParam Integer status) {
        List<Article> articles = service.findByStatus(status);
        return Result.ok(articles);
    }
    
    /**
     * 自定义方法：发布文章
     * 演示业务逻辑处理
     * 
     * 访问：POST http://localhost:8080/api/articles/publish?id=1
     */
    @ApiOperation(value = "发布文章", notes = "演示业务逻辑处理，将文章状态更新为已发布")
    @PostMapping("/publish")
    public Result publishArticle(@RequestParam Long id) {
        Article article = service.publishArticle(id);
        return Result.ok(article);
    }
    
    /**
     * 自定义方法：批量发布文章
     * 演示批量操作
     * 
     * 访问：POST http://localhost:8080/api/articles/publish-batch
     * Body: [1, 2, 3]
     */
    @ApiOperation(value = "批量发布文章", notes = "演示批量操作，将多个文章状态更新为已发布")
    @PostMapping("/publish-batch")
    public Result publishArticles(@RequestBody List<Long> ids) {
        List<Article> articles = service.publishArticles(ids);
        return Result.ok(articles);
    }
    
    /**
     * 自定义方法：导入文章
     * 演示使用 SaveOptions 控制保存行为
     * 
     * 访问：POST http://localhost:8080/api/articles/import
     * Body: {"title": "导入的文章", "content": "...", "author": "..."}
     */
    @ApiOperation(value = "导入文章", notes = "演示使用 SaveOptions 控制保存行为，不自动填充字段")
    @PostMapping("/import")
    public Result importArticle(@RequestBody Article article) {
        Article saved = service.importArticle(article);
        return Result.ok(saved);
    }
    
    /**
     * 自定义方法：演示 DAO 层的常用 CRUD API
     * 
     * 访问：GET http://localhost:8080/api/articles/demo-crud
     */
    @ApiOperation(value = "演示DAO层CRUD API", notes = "演示框架 2.3.6 中常用的 CRUD API 使用方式")
    @GetMapping("/demo-crud")
    public Result demoCrud() {
        java.util.Map<String, Object> demo = new java.util.HashMap<>();
        
        // 1. 演示 saveOrUpdate（自动判断 insert/update）
        Article newArticle = new Article();
        newArticle.setTitle("演示文章");
        newArticle.setContent("这是演示内容");
        newArticle.setAuthor("演示作者");
        newArticle.setViews(0);
        newArticle.setStatus(0);
        Article saved = service.saveOrUpdate(newArticle);
        demo.put("saveOrUpdate", saved);
        
        // 2. 演示 findById
        Article found = service.findById(saved.getId());
        demo.put("findById", found);
        
        // 3. 演示 findAll
        List<Article> all = service.findAll();
        demo.put("findAll_count", all.size());
        
        // 4. 演示 update
        found.setTitle("更新后的标题");
        found.setViews(100);
        Article updated = service.saveOrUpdate(found);
        demo.put("update", updated);
        
        // 5. 演示 delete
        service.deleteById(saved.getId());
        demo.put("delete", "已删除ID: " + saved.getId());
        
        // 6. 演示批量操作
        List<Article> batchArticles = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Article article = new Article();
            article.setTitle("批量文章 " + i);
            article.setContent("批量内容 " + i);
            article.setAuthor("批量作者");
            article.setViews(0);
            article.setStatus(0);
            batchArticles.add(article);
        }
        Iterable<Article> batchSaved = service.entityRepo.saveOrUpdateAll(batchArticles);
        demo.put("batchSaveOrUpdate", batchSaved);
        
        return Result.ok(demo);
    }
}
