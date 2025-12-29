/*
 * WLDOS 第三方集成示例项目
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
package com.example.myapp.entity;

import com.wldos.framework.mvc.entity.BaseEntity;

/**
 * 产品实体类
 * 演示使用 EntityController 进行 CRUD 操作
 * 
 * 注意：BaseEntity 已经包含了 id 字段，不需要重复定义
 */
public class Product extends BaseEntity {
    
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    
    public Product() {
    }
    
    public Product(Long id, String name, String description, Double price, Integer stock) {
        this.setId(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
}

