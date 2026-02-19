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
package com.example.myapp.service;

import com.example.myapp.dao.ArticleDao;
import com.example.myapp.dao.MockArticleDaoImpl;
import com.example.myapp.entity.Article;
import com.wldos.common.res.PageData;
import com.wldos.common.res.PageQuery;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.framework.common.SaveOptions;
import com.wldos.framework.mvc.service.EntityService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文章服务类
 * 演示使用 EntityService 进行实体 CRUD 操作
 * 
 * EntityService 提供的常用方法：
 * - saveOrUpdate(E entity): 保存或更新（自动判断 insert/update）
 * - saveOrUpdate(E entity, SaveOptions options): 带配置的保存或更新
 * - findById(PK id): 根据ID查询
 * - findAll(): 查询所有
 * - findAllWithCond(Class<E> clazz, Map<String, Object> condition): 带条件查询
 * - delete(E entity): 删除实体
 * - deleteById(PK id): 根据ID删除
 * - deletePhysicalByIds(Object... ids): 批量物理删除
 * 
 * 框架自动处理：
 * - 自动填充公共字段（createTime, updateTime, createBy, updateBy 等）
 * - 乐观锁支持（@Version 字段自动维护）
 * - 多租户隔离（如果启用）
 * - 域隔离（如果启用）
 */
@Service
public class ArticleService extends EntityService<ArticleDao, Article, Long> {
    
    /**
     * 分页列表（演示 Map + PageQuery + PageData 用法）
     * Mock 实现：从内存过滤、排序、分页
     *
     * @param pageQuery 分页查询参数（condition 可含 author、status 等）
     * @return 分页数据
     */
    public PageData<Article> pageList(PageQuery pageQuery) {
        List<Article> all = findAll().stream().collect(Collectors.toList());
        if (pageQuery != null && pageQuery.getCondition() != null) {
            Object author = pageQuery.getCondition().get("author");
            if (!ObjectUtils.isBlank(author)) {
                all = all.stream().filter(a -> author.toString().equals(a.getAuthor())).collect(Collectors.toList());
            }
            Object status = pageQuery.getCondition().get("status");
            if (status != null) {
                int s = Integer.parseInt(status.toString());
                all = all.stream().filter(a -> s == (a.getStatus() != null ? a.getStatus() : -1)).collect(Collectors.toList());
            }
        }
        int total = all.size();
        int current = pageQuery != null ? pageQuery.getCurrent() : 1;
        int pageSize = pageQuery != null ? pageQuery.getPageSize() : 10;
        if (pageQuery != null && pageQuery.getSorter() != null && !pageQuery.getSorter().isEmpty()) {
            org.springframework.data.domain.Sort.Order first = pageQuery.getSorter().iterator().next();
            String prop = first.getProperty();
            boolean desc = first.isDescending();
            all.sort(Comparator.comparing((Function<? super Article, ? extends String>) a -> {
                Object v = getFieldValue(a, prop);
                return v != null ? v.toString() : "";
            }, desc ? Comparator.reverseOrder() : Comparator.naturalOrder()));
        }
        int from = Math.min((current - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);
        List<Article> rows = all.subList(from, to);
        return new PageData<>(total, current, pageSize, rows);
    }

    private Object getFieldValue(Article a, String prop) {
        if ("title".equals(prop)) return a.getTitle();
        if ("author".equals(prop)) return a.getAuthor();
        if ("status".equals(prop)) return a.getStatus();
        if ("views".equals(prop)) return a.getViews();
        if ("createTime".equals(prop)) return a.getCreateTime();
        return null;
    }

    /**
     * 自定义方法：根据作者查询文章列表
     * 演示如何在 Service 中调用 DAO 的自定义方法
     * 
     * 注意：实际项目中，如果 DAO 是 Spring Data JDBC 实现的，可以直接在接口中定义方法
     * 这里为了演示，我们通过类型转换来调用 MockArticleDao 的自定义方法
     * 
     * @param author 作者
     * @return 文章列表
     */
    public List<Article> findByAuthor(String author) {
        // 如果 entityRepo 是 MockArticleDao 实例，可以调用自定义方法
        if (entityRepo instanceof MockArticleDaoImpl) {
            return ((MockArticleDaoImpl) entityRepo).findByAuthor(author);
        }
        // 否则使用通用方法：查询所有后过滤
        return findAll().stream()
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
        // 如果 entityRepo 是 MockArticleDao 实例，可以调用自定义方法
        if (entityRepo instanceof MockArticleDaoImpl) {
            return ((MockArticleDaoImpl) entityRepo).findByStatus(status);
        }
        // 否则使用通用方法：查询所有后过滤
        return findAll().stream()
                .filter(article -> status != null && status.equals(article.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * 自定义方法：发布文章
     * 演示业务逻辑处理
     * 
     * @param id 文章ID
     * @return 更新后的文章
     */
    public Article publishArticle(Long id) {
        Article article = findById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // 更新状态为已发布
        article.setStatus(1);
        
        // 使用框架的 saveOrUpdate 方法保存
        // 框架会自动填充 updateTime 等字段
        return saveOrUpdate(article);
    }
    
    /**
     * 自定义方法：批量发布文章
     * 演示批量操作
     * 
     * @param ids 文章ID列表
     * @return 更新后的文章列表
     */
    public List<Article> publishArticles(List<Long> ids) {
        List<Article> articles = new java.util.ArrayList<>();
        for (Long id : ids) {
            Article article = findById(id);
            if (article != null) {
                article.setStatus(1);
                articles.add(article);
            }
        }
        
        // 使用框架的批量保存方法
        // 框架会自动填充 updateTime 等字段
        return (List<Article>) entityRepo.saveOrUpdateAll(articles);
    }
    
    /**
     * 自定义方法：导入文章（不自动填充字段）
     * 演示使用 SaveOptions 控制保存行为
     * 
     * @param article 文章实体
     * @return 保存后的文章
     */
    public Article importArticle(Article article) {
        // 使用 SaveOptions.forImport() 创建导入配置
        // 不自动填充字段，写入所有字段（包括空值）
        SaveOptions options = SaveOptions.forImport();
        
        return saveOrUpdate(article, options);
    }
}
