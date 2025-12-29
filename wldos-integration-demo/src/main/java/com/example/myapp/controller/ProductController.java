/*
 * WLDOS 第三方集成示例项目
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
package com.example.myapp.controller;

import com.example.myapp.entity.Product;
import com.example.myapp.service.ProductService;
import com.wldos.framework.mvc.controller.EntityController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品管理 Controller
 * 
 * 演示使用 EntityController 基类进行实体 CRUD 操作
 * 
 * EntityController 自动提供的接口：
 * - POST /api/products/add - 新增产品
 * - POST /api/products/update - 更新产品
 * - DELETE /api/products/delete - 删除产品
 * - DELETE /api/products/deletes - 批量删除产品
 * - GET /api/products/get?id=xxx - 根据ID查询产品
 * - GET /api/products/all - 查询所有产品
 * - GET /api/products/page - 分页查询产品（已废弃，建议使用自定义分页）
 * 
 * 框架自动处理：
 * - 统一响应格式（自动包装为 Result）
 * - 异常处理（自动捕获并返回统一格式）
 * - 多租户隔离（如果启用）
 * - 域隔离（如果启用）
 */
@Api(tags = "产品管理（EntityController示例）")
@RestController
@RequestMapping("/api/products")
public class ProductController extends EntityController<ProductService, Product> {
    
    /**
     * 新增产品前的处理（Hook）
     * 可以在这里进行数据校验、设置默认值等
     */
    @Override
    protected void preAdd(Product entity) {
        // 示例：设置创建时间
        // entity.setCreateTime(new Date());
        System.out.println("新增产品前处理: " + entity.getName());
    }
    
    /**
     * 新增产品后的处理（Hook）
     * 可以在这里刷新缓存、发送通知等
     */
    @Override
    protected void postAdd(Product entity) {
        // 示例：刷新缓存
        // cache.refresh("products");
        System.out.println("新增产品后处理: " + entity.getName());
    }
    
    /**
     * 更新产品前的处理（Hook）
     */
    @Override
    protected void preUpdate(Product entity) {
        System.out.println("更新产品前处理: " + entity.getName());
    }
    
    /**
     * 更新产品后的处理（Hook）
     */
    @Override
    protected void postUpdate(Product entity) {
        System.out.println("更新产品后处理: " + entity.getName());
    }
    
    /**
     * 删除产品前的处理（Hook）
     */
    @Override
    protected void preDelete(Product entity) {
        System.out.println("删除产品前处理: " + entity.getId());
    }
    
    /**
     * 删除产品后的处理（Hook）
     */
    @Override
    protected void postDelete(Product entity) {
        System.out.println("删除产品后处理: " + entity.getId());
    }
    
    /**
     * 结果集过滤器
     * 可以在这里对查询结果进行过滤
     */
    @Override
    protected java.util.List<Product> doFilter(java.util.List<Product> res) {
        // 示例：过滤掉已下架的产品
        // return res.stream().filter(p -> p.getStatus() == 1).collect(Collectors.toList());
        return res;
    }
    
    /**
     * 自定义业务方法示例
     * 除了基类提供的CRUD方法，还可以添加自定义的业务方法
     */
    @ApiOperation(value = "根据名称查询产品", notes = "自定义业务方法示例")
    @org.springframework.web.bind.annotation.GetMapping("/by-name")
    public java.util.List<Product> getProductsByName(@org.springframework.web.bind.annotation.RequestParam String name) {
        // 使用 service 进行查询
        // 注意：这里需要在实际的 Service 中实现对应的方法
        return service.findAll();
    }
}

