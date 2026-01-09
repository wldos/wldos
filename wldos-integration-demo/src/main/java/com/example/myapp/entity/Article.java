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
package com.example.myapp.entity;

import com.wldos.framework.mvc.entity.BaseEntity;

/**
 * 文章实体类
 * 演示使用框架的 CRUD API 进行数据操作
 * 
 * 注意：BaseEntity 已经包含了以下字段：
 * - id: 主键
 * - createTime: 创建时间
 * - updateTime: 更新时间
 * - createBy: 创建人
 * - updateBy: 更新人
 * - version: 乐观锁版本号（如果使用 @Version 注解）
 */
public class Article extends BaseEntity {
    
    private String title;
    private String content;
    private String author;
    private Integer views;
    private Integer status; // 0-草稿, 1-已发布, 2-已删除
    
    public Article() {
    }
    
    public Article(Long id, String title, String content, String author, Integer views, Integer status) {
        this.setId(id);
        this.title = title;
        this.content = content;
        this.author = author;
        this.views = views;
        this.status = status;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public Integer getViews() {
        return views;
    }
    
    public void setViews(Integer views) {
        this.views = views;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Article{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", views=" + views +
                ", status=" + status +
                ", createTime=" + (getCreateTime() != null ? getCreateTime().toString() : null) +
                ", updateTime=" + (getUpdateTime() != null ? getUpdateTime().toString() : null) +
                '}';
    }
}
