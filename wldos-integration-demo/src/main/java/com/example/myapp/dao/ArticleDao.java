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

import com.wldos.framework.mvc.dao.BaseDao;
import com.example.myapp.entity.Article;

/**
 * 文章数据访问层接口
 * 演示使用 BaseDao 进行数据访问
 * 
 * BaseDao 提供的常用方法：
 * - saveOrUpdate(E entity): 保存或更新（自动判断 insert/update）
 * - saveOrUpdate(E entity, SaveOptions options): 带配置的保存或更新
 * - saveOrUpdateAll(Iterable<E> entities): 批量保存或更新
 * - findById(PK id): 根据ID查询
 * - findAll(): 查询所有
 * - delete(E entity): 删除实体
 * - deleteById(PK id): 根据ID删除
 * - getCommonOperation(): 获取 CommonOperation 实例，用于其他高级操作
 */
public interface ArticleDao extends BaseDao<Article, Long> {
    // 可以添加自定义查询方法
    // 例如：List<Article> findByAuthor(String author);
}
